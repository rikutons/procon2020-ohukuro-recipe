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
import com.arthenica.mobileffmpeg.FFprobe
import kotlinx.android.synthetic.main.activity_test.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.Channels


class TestActivity : AppCompatActivity() {
    private var mSpeechService: SpeechService? = null

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
        bindService(Intent(this, SpeechService::class.java), mServiceConnection, BIND_AUTO_CREATE)
        Log.d("user", "onStart()")
    }

    private fun sendWavData(){
        val inputStream = resources.openRawResource(R.raw.naidesu)
        mSpeechService?.recognizeInputStream(inputStream, 48000, 2)
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

        videoFile.delete()

        val minuteCount = getFileDurationMinutes(audioFile.path)
        val hertz = getFileAudioHertz(audioFile.path)
        val channelCount = getFileChannelCount(audioFile.path)
        Log.d("user", "$minuteCount $hertz $channelCount")

        for(i in 0..minuteCount) {
            val fragmentFile = File(cacheDir, "fragment.wav")
            if(i == minuteCount) {
                Log.d("user", "-ss " +  (i * 60).toString() + " -i ${audioFile.path} -c copy ${fragmentFile.path}")
                FFmpeg.execute("-ss " +  (i * 60).toString() + " -i ${audioFile.path} -c copy ${fragmentFile.path}")
            }
            else {
                Log.d("user", "-ss " + (i * 60).toString() + " -i ${audioFile.path} -t 60 -c copy ${fragmentFile.path}")
                FFmpeg.execute("-ss " + (i * 60).toString() + " -i ${audioFile.path} -t 60 -c copy ${fragmentFile.path}")
            }
            val fragmentInputStream = FileInputStream(fragmentFile)
            mSpeechService?.recognizeInputStream(fragmentInputStream, hertz, channelCount)
            fragmentFile.delete()
        }
        audioFile.delete()
    }

    private fun getFileDurationMinutes(path : String) : Int{
        FFprobe.execute(path)
        val output = getLastCommandOutput()
        Log.d("user", output)
        var result = 0
        if (Regex("Duration: ").containsMatchIn(output)) {
            val strings = output.split("Duration: ")
            Log.d("user", strings.toString())
            Log.d("user", strings[1].substring(3, 5))
            result = Integer.parseInt(strings[1].substring(3, 5))
        }
        return result
    }

    private fun getFileAudioHertz(path : String) : Int{
        FFprobe.execute(path)
        val output = getLastCommandOutput()
        Log.d("user", output)
        var result = 0
        if (Regex(" Hz,").containsMatchIn(output)) {
            val strings = output.split(" Hz,")
            Log.d("user", strings.toString())
            Log.d("user", strings[0].substring(strings[0].length - 5))
            result = Integer.parseInt(strings[0].substring(strings[0].length - 5))
        }
        return result
    }

    private fun getFileChannelCount(path : String) : Int{
        FFprobe.execute(path)
        val output = getLastCommandOutput()
        Log.d("user", output)
        var result = 0
        if (Regex(" channel").containsMatchIn(output)) {
            val strings = output.split(" channel")
            Log.d("user", strings.toString())
            Log.d("user", strings[0].substring(strings[0].length - 1))
            result = Integer.parseInt(strings[0].substring(strings[0].length - 1))
        }
        return result
    }
}
