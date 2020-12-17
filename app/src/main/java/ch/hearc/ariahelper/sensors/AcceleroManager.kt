package ch.hearc.ariahelper.sensors

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import ch.hearc.ariahelper.ui.character.CharacterComponentViewModel
import kotlinx.coroutines.*
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * WIP - basic sensor, intelligence is yet to be made
 */
class AcceleroManager(
    private val characterComponentViewModel: CharacterComponentViewModel,
    context: Context
) : Runnable, SensorEventListener {
    //sensor and sensor manager (should not change through calls to the service)
    private var sensorManager: SensorManager =
        context.getSystemService(SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val BIGINT_2_28: Int = 2.0.pow(28.0).roundToInt()

    //accelerometer attributes
    private var currentEntropy: Float = 0f
    private val ENTROPY_TRESHOLD: Float = 1000f

    //coroutine of descending loading bar
    private var loadingBarBouncer : Job ? = null
    private var countEntropy : Boolean = true
    private val COOLDOWN_DICE = 500L

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER && countEntropy) {

            val x: Float = event.values[0]
            val y: Float = event.values[1]
            val z: Float = event.values[2]
            val acceleration = sqrt(x * x + y * y + z + z) - SensorManager.GRAVITY_EARTH
            if (acceleration > 0.1f) {
                currentEntropy += acceleration
            }
            val entropyRatio = (currentEntropy / ENTROPY_TRESHOLD)
            //bar is full
            if (entropyRatio >= 1) {
                characterComponentViewModel._Progress.postValue((100).toInt())
                rollTheDice(generateEntropy(x, y, z))
                //cap the entropy charge
                currentEntropy = ENTROPY_TRESHOLD
                //avoid the bar filling
                countEntropy = false
                startEmptyCooldown()
            }
            startTimeOut()
            characterComponentViewModel._Progress.postValue((100 * entropyRatio).toInt())
        }
    }

    private fun startEmptyCooldown(){
        GlobalScope.launch {
            delay(COOLDOWN_DICE)
            countEntropy = true
        }
    }

    private fun startTimeOut() {
        loadingBarBouncer?.cancel()
        loadingBarBouncer = GlobalScope.launch {
            var nextPrintTime = System.currentTimeMillis()
            // Init values : Sub progress -deltaProgress% every deltaTimeMs
            val pourcentage : Float = 0.1F / 100F
            val deltaProgress : Float = ENTROPY_TRESHOLD * pourcentage
            val deltaTime = 5L
            while (isActive) { // cancellable computation loop
                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    currentEntropy -= deltaProgress
                    if(currentEntropy <= 0){
                        currentEntropy = 0F
                        cancel()
                    }
                    updateProgressBar()
                    nextPrintTime += deltaTime
                }
            }
        }
    }

    private fun updateProgressBar(){
        val entropyRatio = (currentEntropy / ENTROPY_TRESHOLD)
        characterComponentViewModel._Progress.postValue((100 * entropyRatio).toInt())
    }

    private fun rollTheDice(entropy: Int) {
        val seededRandom = Random(entropy)
        val randomValue = characterComponentViewModel.DCUSTOMREQ.value ?: 0
        characterComponentViewModel.assignDices(
            seededRandom.nextInt(6),//6
            seededRandom.nextInt(10),// 10
            seededRandom.nextInt(100),// 100
            if (randomValue == 0) randomValue else seededRandom.nextInt(randomValue)// custom
        )
    }

    private fun generateEntropy(x: Float, y: Float, z: Float): Int {
        val xShift: Int = (x * BIGINT_2_28).toInt()
        val yShift: Int = (y * BIGINT_2_28).toInt()
        val zShift: Int = (z * BIGINT_2_28).toInt()
        return xShift xor yShift xor zShift
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}


    public fun stopSensor() {
        sensorManager.unregisterListener(this)
    }

    override fun run() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }
}