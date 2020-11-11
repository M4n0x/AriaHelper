package ch.hearc.ariahelper.sensors

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import ch.hearc.ariahelper.ui.character.CharacterComponentViewModel
import kotlin.math.pow
import kotlin.math.sqrt


class AcceleroManager(
    private val characterComponentViewModel: CharacterComponentViewModel,
    context: Context
) : Runnable, SensorEventListener {
    //sensor and sensor manager (should not change through calls to the service)
    private var sensorManager: SensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    //accelerometer attributes
    private var currentEntropy: Float=0f
    private val ENTROPY_TRESHOLD : Float = 10f

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            Log.d("TAG", "register sensor")
            val x: Float = event.values[0]
            val y: Float = event.values[1]
            val z: Float = event.values[2]
            val acceleration = sqrt(x*x + y*y + z+z) - SensorManager.GRAVITY_EARTH
            if(acceleration > 0.1f)
                currentEntropy += acceleration
            val entropyRatio = (100 * (currentEntropy / ENTROPY_TRESHOLD))
            characterComponentViewModel._Progress.postValue((entropyRatio % 100).toInt())
            characterComponentViewModel.assignDices(x.toInt(), y.toInt(), z.toInt(), (x+y+z).toInt())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    public fun stopSensor(){
        Log.d("TAG", "stopSensor: Sensor unregistered")
        sensorManager.unregisterListener(this)
    }

    override fun run() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }
}