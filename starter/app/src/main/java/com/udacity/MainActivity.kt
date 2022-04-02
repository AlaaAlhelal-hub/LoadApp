package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*



class MainActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager

    private var downloadID: Long = 0
    private var downloadUrl: String? = null
    private var fileName: String? = null
    private var selectedOption = ""
    lateinit var customButton: LoadingButton

    companion object {
        private const val URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        customButton = findViewById(R.id.custom_button)
        createNotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_title),
            getString(R.string.notification_description)
        )

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {

            if(downloadUrl.isNullOrEmpty()) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.choose_file_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                //start loading
                customButton.setState( ButtonState.Loading )
                download()
            }
        }

    }

    fun onRadioButtonClicked(view: View) {
        val selectedRadio = view as RadioButton
        when (selectedRadio.id) {
            R.id.glide_option -> {
                downloadUrl = getString(R.string.glide_project_url)
                fileName = getString(R.string.glide_download_description)
                selectedOption = "glide_option"
            }
            R.id.loadapp_option -> {
                downloadUrl = getString(R.string.udacity_project_url)
                fileName = getString(R.string.udacity_download_description)
                selectedOption = "loadapp_option"
            }
            R.id.retrofit_option -> {
                downloadUrl = getString(R.string.retrofit_project_url)
                fileName = getString(R.string.retrofit_download_description)
                selectedOption = "retrofit_option"
            }
        }
    }


    private fun createNotificationChannel(
        channelId: String, channelName: String,
        channelDescription: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
                description = channelDescription
                enableLights(true)
                lightColor = R.color.colorPrimary
            }

            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(downloadUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        Log.i("downloadID", "$downloadID")

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            var status: String? = null
            var downloadStatus: Int? = null

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val query = id?.let { DownloadManager.Query().setFilterById(id) }
            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))


                when (downloadStatus) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        status = "SUCCESS"
                    }
                    DownloadManager.STATUS_FAILED -> {
                        status = "FAIL"
                    }
                }
                notificationManager = getSystemService(NotificationManager::class.java)

                notificationManager.sendNotification(
                    fileName!!,
                    status!!,
                    getString(R.string.notification_description),
                    getString(R.string.notification_title),
                    CHANNEL_ID,
                    context!!
                )

                customButton.setState( ButtonState.Completed )

            }
        }
    }





    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
