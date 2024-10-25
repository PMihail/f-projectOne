package com.mipa.f1stat.common.helpers

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.ContextCompat

class VibrationHelper(context: Context) {

    private var vibrator: Vibrator? = ContextCompat.getSystemService(context, Vibrator::class.java)

    fun vibrateShort() {
        vibrate(longArrayOf(0, VIBRATE_SHORT))
    }

    fun vibrateLong() {
        vibrate(longArrayOf(0, VIBRATE_LONG))
    }

    fun vibrateDoubleShort() {
        vibrate(longArrayOf(0, VIBRATE_SHORT, PAUSE_SHORT, VIBRATE_SHORT))
    }

    fun vibrate(pattern: LongArray) {
        var silent = true
        var amplitudes = emptyList<Int>()
        pattern.forEach { _ ->
            amplitudes = amplitudes.plus(if (silent) 0 else 255)
            silent = !silent
        }
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, amplitudes.toIntArray(), -1))
    }

    companion object {
        const val VIBRATE_SHORT = 30L
        const val VIBRATE_LONG = 300L
        const val PAUSE_SHORT = 70L
        const val PAUSE_LONG = 200L
    }

}