package com.dozen.world

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_demo.setOnClickListener{v->kotlin.run {

            val intent=Intent("MESSAGE_DEMO")
            startActivity(intent)

//            startActivity(Intent(baseContext, Class.forName("com.dozen.world.face.MessageActivity")))
        }}

    }

}
