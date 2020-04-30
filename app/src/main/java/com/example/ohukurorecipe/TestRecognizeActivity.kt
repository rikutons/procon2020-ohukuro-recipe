package com.example.ohukurorecipe

import android.R
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.cloud.android.speech.SpeechService


class TestRecognizeActivity : AppCompatActivity() {

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_recognize)
        val speechToTextConverter = SpeechToTextConverter(this)
        speechToTextConverter.connectToServer()
        speechToTextConverter.startStreaming()

        val inputStream = resources.openRawResource(R.raw.naidesu)
        val soundByte = ByteArray(inputStream.available())

        Log.v("user", soundByte.toString())
        speechToTextConverter.convert(soundByte, soundByte.size)

    }

    private val mSpeechServiceListener: SpeechToTextConverter.Listener = object : SpeechToTextConverter.Listener {
        override fun onSpeechRecognized(text: String?, isFinal: Boolean) {
            runOnUiThread {
                textView2.text = text
            }
        }
    }*/

    private val FRAGMENT_MESSAGE_DIALOG = "message_dialog"

    private val STATE_RESULTS = "results"

    private val REQUEST_RECORD_AUDIO_PERMISSION = 1

    private var mSpeechService: SpeechService? = null

    //private val mVoiceRecorder: VoiceRecorder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_recognize)
        //mColorHearing = ResourcesCompat.getColor(resources, R.color.status_hearing, theme)
        //mColorNotHearing = ResourcesCompat.getColor(resources, R.color.status_not_hearing, theme)
        //setSupportActionBar(findViewById(R.id.toolbar) as Toolbar?)
        //mStatus = findViewById(R.id.status) as TextView
        //mRecyclerView = findViewById(R.id.recycler_view) as RecyclerView
        //mRecyclerView.setLayoutManager(LinearLayoutManager(this))
        val results =
            savedInstanceState?.getStringArrayList(
                STATE_RESULTS
            )
        mAdapter = ResultAdapter(results)
        mRecyclerView.setAdapter(mAdapter)
    }

    override fun onStart() {
        super.onStart()

        // Prepare Cloud Speech API
        bindService(
            Intent(this, SpeechService::class.java),
            mServiceConnection,
            Context.BIND_AUTO_CREATE
        )

        // Start listening to voices
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startVoiceRecorder()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            showPermissionMessageDialog()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        }
    }

    override fun onStop() {
        // Stop listening to voice
        stopVoiceRecorder()

        // Stop Cloud Speech API
        mSpeechService!!.removeListener(mSpeechServiceListener)
        unbindService(mServiceConnection)
        mSpeechService = null
        super.onStop()
    }
}
