package com.example.ohukurorecipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_select.*

class SelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        makeRecipeButton.setOnClickListener{
            val intent = Intent(this, MakeRecipeActivity::class.java)
            startActivity(intent)
        }
    }
}
