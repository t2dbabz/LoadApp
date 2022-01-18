package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.util.createDownloadNotificationChannel
import com.udacity.util.sendDownloadComplete
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager

        createDownloadNotificationChannel(applicationContext)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.mainContainer.customButton.setOnClickListener {
            binding.mainContainer.customButton.buttonState = ButtonState.Clicked

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
            binding.mainContainer.customButton.buttonState = ButtonState.Completed
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val query = DownloadManager.Query()
                query.setFilterById(id)
                val cursor = downloadManager.query(query)

                if (cursor.moveToFirst()) {

                    val status = when (cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_SUCCESSFUL -> getString(R.string.success)
                        DownloadManager.STATUS_FAILED -> getString(R.string.failed)
                        else -> getString(R.string.unknown)
                    }

                    val fileName = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TITLE))

                    val fileDescription = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_DESCRIPTION))


                    notificationManager.sendDownloadComplete(fileName, fileDescription, status, applicationContext)

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
                    request.setTitle(getString(R.string.load_app))
                    request.setDescription(getString(R.string.load_app_description))
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
