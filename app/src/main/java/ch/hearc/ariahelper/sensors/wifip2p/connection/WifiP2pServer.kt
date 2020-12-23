package ch.hearc.ariahelper.sensors.wifip2p.connection

import android.util.Log
import ch.hearc.ariahelper.sensors.wifip2p.WifiP2PReceiver
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket

class WifiP2pServer (
    private val port: Int,
    private val action : SocketAction
) : Thread() {
    private lateinit var ephemeralServerSocket : ServerSocket
    private lateinit var client: Socket

    override fun run() {
        try {
            Log.d("Server", "Server waiting...")
            ephemeralServerSocket = ServerSocket(port)
            ephemeralServerSocket.soTimeout = 5000
            client = ephemeralServerSocket.accept()
            Log.d("Server", "Server accepted... : ${client.toString()}")
            action.perform(client!!)
            Log.d("Server", "Server closing...")
        } catch(e : Exception){
            Log.d("server", "Error in server : ${e.printStackTrace()}")
        } finally {
            client?.takeIf { it.isConnected }?.close()
            ephemeralServerSocket?.close()
            WifiP2PReceiver.disconnect()
        }
    }

}