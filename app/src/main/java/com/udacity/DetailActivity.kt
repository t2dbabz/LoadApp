package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val extras = intent.extras
        if (extras != null) {
            val fileName = extras.getString("fileName")
            val fileDescription = extras.getString("fileDescription")
            val status = extras.getString("status")


            fileName_textView.text = getString(R.string.fileName_description, fileName, fileDescription)

            when (status) {
                getString(R.string.success) -> status_textView.setTextColor(resources.getColor(R.color.green, null))
                getString(R.string.failed) -> status_textView.setTextColor(resources.getColor(R.color.red, null))
                getString(R.string.unknown) -> status_textView.setTextColor(resources.getColor(R.color.red, null))
            }
            status_textView.text = status
        }

        ok_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
