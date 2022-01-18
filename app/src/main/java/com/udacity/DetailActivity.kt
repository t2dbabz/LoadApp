package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val extras = intent.extras
        if (extras != null) {
            val fileName = extras.getString("fileName")
            val fileDescription = extras.getString("fileDescription")
            val status = extras.getString("status")


            binding.mainContainer.fileNameTextView.text = getString(R.string.fileName_description, fileName, fileDescription)

            when (status) {
                getString(R.string.success) -> status_textView.setTextColor(resources.getColor(R.color.green, null))
                getString(R.string.failed) -> status_textView.setTextColor(resources.getColor(R.color.red, null))
                getString(R.string.unknown) -> status_textView.setTextColor(resources.getColor(R.color.red, null))
            }
            binding.mainContainer.statusTextView.text = status
        }

        ok_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
