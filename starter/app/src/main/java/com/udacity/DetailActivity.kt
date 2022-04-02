package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val filename = findViewById<TextView>(R.id.filename)
        val status = findViewById<TextView>(R.id.status)

        filename.text = intent.getStringExtra("filename").toString()
        status.text = intent.getStringExtra("status").toString()

        if(status.text == "SUCCESS") {
            status.setTextColor(resources.getColor(R.color.green))
        } else {
            status.setTextColor(resources.getColor(R.color.red))
        }

        val notificationManager = ContextCompat.getSystemService(this,
            NotificationManager::class.java) as NotificationManager

        notificationManager.cancelNotifications()
    }



    fun onBack(view: View) {
        val  mainActivity = Intent(this, MainActivity::class.java)
        startActivity(mainActivity)
    }
}
