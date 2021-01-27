package ch.hearc.ariahelper

import android.app.AlertDialog
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.Menu
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ch.hearc.ariahelper.models.Item
import ch.hearc.ariahelper.models.persistence.CharacterPersistenceManager
import ch.hearc.ariahelper.models.persistence.LootPersistenceManager
import ch.hearc.ariahelper.models.persistence.PicturePersistenceManager
import ch.hearc.ariahelper.sensors.wifip2p.WifiP2PReceiver
import ch.hearc.ariahelper.ui.character.CharacterViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.stream.Collectors

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var intentFilter : IntentFilter

    //wifiP2P
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var manager: WifiP2pManager
    //vibration on item received
    private val VIBRATION_DURATION_MS : Long = 600

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //init persistence managers from the start of the activity (need context to access file system)
        CharacterPersistenceManager.init(this)
        LootPersistenceManager.init(this)
        PicturePersistenceManager.init(this)

        //init layouts and controllers
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_character, R.id.nav_character_loot, R.id.nav_lootdm
            ), drawerLayout
        )
        //init action bar
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        initWifiPeer2PeerReceiver()
        initP2PSwitchObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        initP2pSwitch()
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(WifiP2PReceiver)
        CharacterPersistenceManager.saveAllCharacter()
        LootPersistenceManager.save()
    }

    override fun onResume() {
        registerReceiver(WifiP2PReceiver, intentFilter)
        super.onResume()
    }

    private fun initWifiPeer2PeerReceiver(){
        // --- wifi P2P intent filter init ---
        intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }

        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)
        WifiP2PReceiver.init(channel, manager, this)
    }

    private fun initP2PSwitchObserver(){
        //observe and send message to receiver
        WifiP2PReceiver.wifiViewModel.p2pActivated.observe(this, {
            updateP2pDiscovery(it)
        })
        //init receiver with good value
        updateP2pDiscovery(WifiP2PReceiver.wifiViewModel.p2pActivated.value ?: false)
    }

    private fun updateP2pDiscovery(activate: Boolean){
        if(activate)
            WifiP2PReceiver.discoverPeers()
        else
            WifiP2PReceiver.stopDiscovery()
    }

    private fun initP2pSwitch(){
        val switch : Switch = findViewById(R.id.p2pSwitch)
        //initialize switch value
        switch.isChecked = WifiP2PReceiver.wifiViewModel._p2pActivated.value ?: false

        //link switch -> discovery
        switch.setOnClickListener{
            if(switch.isChecked){
                //user want to activate discovery : Verify (ex : wifi is disabled, or not supported...)
                if(WifiP2PReceiver.wifiViewModel.canP2PActivate()){
                    //discovery enabled: change discovery value
                    WifiP2PReceiver.wifiViewModel._p2pActivated.value = switch.isChecked
                } else {
                    //discovery impossible: put the switch back
                    switch.isChecked = false
                }
            } else {
                //discovery can always be disabled
                WifiP2PReceiver.wifiViewModel._p2pActivated.value = switch.isChecked
            }
        }
        //link discovery -> switch
        WifiP2PReceiver.wifiViewModel.p2pActivated.observe(this, {
            switch.isChecked = WifiP2PReceiver.wifiViewModel.p2pActivated.value ?: false
        })

        WifiP2PReceiver.wifiViewModel.p2pSupported.observe(this, {
            if (it) {
                switch.isEnabled = true
            } else {
                //p2p not supported : exchange is not usable at all
                WifiP2PReceiver.wifiViewModel._p2pActivated.value = false
                switch.isChecked = false
                switch.isEnabled = false
            }
        })

        WifiP2PReceiver.wifiViewModel.p2pEnabled.observe(this, {
            if (!it) {
                //p2p not enabled -> p2p deactivation
                WifiP2PReceiver.wifiViewModel._p2pActivated.value = false
                switch.isChecked = false
            }
        })
    }

    /**
     * Add the received items from p2p to this player and notify him
     *
     */
    fun onReceiveItems(items: List<Item>){
        //alert the player with the list of names
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Items received")

        val names = items.stream().map { it.name }.collect(Collectors.toList()) as List<String>?
        builder.setItems(names?.toTypedArray(), null)
        // add OK and Cancel buttons
        builder.setPositiveButton("OK", null)

        runOnUiThread{
            vibratePhone()
            builder.create().show()
            //add items to the player if charviewmodel exists
            CharacterViewModel.instance?.let {
                val char = it.character.value
                char!!.itemList.addAll(items)
                it._character.value = char //post to alert observers of the character
            }
        }
    }

    /**
     * Safely vibrate the phone (with retro-compatibility for old SDK)
     */
    fun vibratePhone() {
        val vibrator = applicationContext?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION_MS, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(VIBRATION_DURATION_MS) //used for backward compatibility
        }
    }
}