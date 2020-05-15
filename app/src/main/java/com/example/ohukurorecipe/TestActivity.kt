package com.example.ohukurorecipe

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.common.io.ByteStreams.toByteArray
import kotlinx.android.synthetic.main.activity_test.*
import java.util.*

class TestActivity : AppCompatActivity() {
    private var mSpeechService: SpeechService? = null
    private val SAMPLING_LATE = 48000

    private val mServiceConnection: ServiceConnection = object :
        ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
            Log.d("user", "Connected.")
            mSpeechService = SpeechService.from(binder)
            mSpeechService?.addListener(mSpeechServiceListener)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mSpeechService = null
        }
    }

    private val mSpeechServiceListener: SpeechService.Listener = object : SpeechService.Listener {
        override fun onSpeechRecognized(text: String?, isFinal: Boolean) {
            Log.d("user", "Recognized $text $isFinal")
            if (isFinal) {
                mSpeechService?.finishRecognizing()
            }
            if (textView != null && !TextUtils.isEmpty(text)) {
                runOnUiThread {
                    textView.text = text
                }
            }
        }

        override fun onAPIReadied() {
            Log.d("user", "API Readied.")
            // Start listening to voices
            sendWavData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    override fun onStart() {
        super.onStart()

        // Prepare Cloud Speech API
        val ret = bindService(Intent(this, SpeechService::class.java), mServiceConnection, BIND_AUTO_CREATE)
        Log.d("user", ret.toString())
        Log.d("user", "onStart()")
    }

    private fun sendWavData(){
        val inputStream = resources.openRawResource(R.raw.naidesu)
        mSpeechService?.recognizeInputStream(inputStream)
        // val bytes = getByteData(R.raw.audio)
        // mSpeechService?.recognize(bytes, 1)
        /* val task: TimerTask = object : TimerTask() {
            override fun run() {
                Log.i("user", "run")
                mSpeechService?.finishRecognizing()
            }
        }
        val timer = Timer()
        timer.schedule(task, 2000) */
    }

    private fun getByteData(id : Int) : ByteArray{
        val inputStream = resources.openRawResource(id)
        mSpeechService?.recognizeInputStream(inputStream)
        val bytes = toByteArray(inputStream)
        Log.d("user", bytes.toString())
        return bytes
    }
}
