package com.example.ohukurorecipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_title.*

class TitleActivity : AppCompatActivity() {

    val handler = Handler()

    var timeValue = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        settingButton.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
        testButton.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }
        titleBackground.setOnClickListener {
            val intent = Intent(this, SelectActivity::class.java)
            startActivity(intent)
        }
    }
}
