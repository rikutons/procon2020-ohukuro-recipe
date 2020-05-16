package com.example.ohukurorecipe

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import kotlinx.android.synthetic.main.activity_make_recipe.*
import java.io.File
import com.arthenica.mobileffmpeg.FFprobe

const val REQUEST_VIDEO_CAPTURE = 1
class MakeRecipeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_recipe)

        dispatchTakeVideoIntent()
    }

    private fun dispatchTakeVideoIntent(){
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK && data != null) {
            val videoUri : Uri = data.data ?: return
            val filePath = PathUtil.getPath(this, videoUri)
            Log.d("user", filePath)
            FFprobe.execute("-i $filePath")
            videoView.setVideoURI(videoUri)
            videoView.start()
        }
    }
}
