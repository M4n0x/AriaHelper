package ch.hearc.ariahelper.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import ch.hearc.ariahelper.ui.character.CharacterComponentViewModel

class AcceleroManager(private val characterComponentViewModel: CharacterComponentViewModel) : SensorEventListener {
    private var mSensorManager : SensorManager?= null
    private var mAccelerometer : Sensor ?= null


    override fun onSensorChanged(event: SensorEvent?) {
        TODO("Not yet implemented")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }
}