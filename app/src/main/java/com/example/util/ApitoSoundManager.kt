package com.example.util

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object ApitoSoundManager {
    private var toneGenerator: ToneGenerator? = null
    private var soundJob: Job? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
        } catch (_: Exception) {}
    }

    /**
     * Plays a repeating pleasant Uber-like chime / double-beep pattern
     * while a match offer is actively incoming to the referee.
     */
    fun startIncomingMatchSound(scope: CoroutineScope) {
        stopSound()
        soundJob = scope.launch(Dispatchers.Default) {
            try {
                if (toneGenerator == null) {
                    try {
                        toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
                    } catch (_: Exception) {}
                }
                while (isActive) {
                    // Double beep pattern: high tone then higher tone
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 220)
                    delay(260)
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 200)
                    delay(1400)
                }
            } catch (_: Exception) {}
        }
    }

    /**
     * Plays an affirmative single chime when the referee accepts a match.
     */
    fun playAcceptedSound() {
        stopSound()
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 350)
        } catch (_: Exception) {}
    }

    /**
     * Stops any currently playing alert tone.
     */
    fun stopSound() {
        soundJob?.cancel()
        soundJob = null
        try {
            toneGenerator?.stopTone()
        } catch (_: Exception) {}
    }
}
