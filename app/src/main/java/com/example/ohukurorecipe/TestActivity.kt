package com.example.ohukurorecipe

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.arthenica.mobileffmpeg.Config.*
import com.arthenica.mobileffmpeg.FFmpeg
import kotlinx.android.synthetic.main.activity_test.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.Channels


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
            // sendWavData()
            sendMp4Data()
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
        Log.d("user", "onStart()")
    }

    private fun sendWavData(){
        val inputStream = resources.openRawResource(R.raw.naidesu)
        mSpeechService?.recognizeInputStream(inputStream, 48000)
    }

    private fun sendMp4Data(){
        val videoInputStream = resources.openRawResource(R.raw.tamago)
        val videoFile = File(cacheDir, "input.mp4")
        val audioFile = File(cacheDir, "output.wav")
        val fileOutputStream = FileOutputStream(videoFile)
        fileOutputStream.channel.transferFrom(Channels.newChannel(videoInputStream), 0 , Long.MAX_VALUE)
        fileOutputStream.close()
        Log.d("user", "-i ${videoFile.path} ${audioFile.path}")

        FFmpeg.execute("-i ${videoFile.path} ${audioFile.path}")
        val audioInputStream = FileInputStream(audioFile)
        mSpeechService?.recognizeInputStream(audioInputStream, 16000)
        videoFile.delete()
        audioFile.delete()
    }

}
