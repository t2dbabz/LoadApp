package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            custom_button.buttonState = ButtonState.Clicked

            val downloadURL = when (downloadGroup.checkedRadioButtonId) {
                glide_download.id -> GLIDE_URL
                loadApp_download.id -> LOAD_APP_URL
                retrofit_download.id -> RETROFIT_URL
                else -> null
            }
            download(downloadURL)
        }


    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val query = DownloadManager.Query()
                query.setFilterById(id)
                val cursor = downloadManager.query(query)

                if (cursor.moveToFirst()) {

                    val status = when (cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_SUCCESSFUL -> "Success"
                        DownloadManager.STATUS_FAILED -> "Error"
                        else -> "Unknown"
                    }

                    val fileName = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TITLE))

                    val fileDescription = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_DESCRIPTION))



                }
            }


        }


    }

    private fun download(downloadURL: String?) {
        if (downloadURL != null) {
            val request =
                DownloadManager.Request(Uri.parse(downloadURL))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)

            when (downloadURL) {
                LOAD_APP_URL -> {
                    request.setTitle(getString(R.string.app_name))
                    request.setDescription(getString(R.string.app_description))
                }

                GLIDE_URL -> {
                    request.setTitle(getString(R.string.glide))
                    request.setDescription(getString(R.string.glide_description))
                }

                RETROFIT_URL -> {
                    request.setTitle(getString(R.string.retrofit))
                    request.setDescription(getString(R.string.retrofit_description))
                }
            }

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        } else {
            Toast.makeText(applicationContext, getString(R.string.no_download_selection), Toast.LENGTH_SHORT).show()
        }

    }



    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"

        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"

        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/refs/heads/master.zip"
    }

}
