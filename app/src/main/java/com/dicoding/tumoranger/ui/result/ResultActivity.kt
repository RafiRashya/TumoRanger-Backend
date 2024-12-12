package com.dicoding.tumoranger.ui.result

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.tumoranger.R
import java.util.Locale

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val resultImageView: ImageView = findViewById(R.id.resultImageView)
        val predictionTextView: TextView = findViewById(R.id.predictionTextView)
        val confidenceScoreTextView: TextView = findViewById(R.id.confidenceScoreTextView)
        val nameTextView: TextView = findViewById(R.id.nameTextView)
        val birthdateTextView: TextView = findViewById(R.id.birthdateTextView)
        val genderTextView: TextView = findViewById(R.id.genderTextView)

        val imageUri = intent.getStringExtra("IMAGE_URI")
        val prediction = intent.getStringExtra("PREDICTION")
        val confidenceScore = intent.getStringExtra("CONFIDENCE_SCORE")?.toDoubleOrNull()
        val name = intent.getStringExtra("NAME")
        val birthdate = intent.getStringExtra("BIRTHDATE")
        val gender = intent.getStringExtra("GENDER")

        resultImageView.setImageURI(Uri.parse(imageUri))
        predictionTextView.text = prediction
        confidenceScoreTextView.text = confidenceScore?.let { String.format(Locale.ROOT, "%.0f%%", it) }
        nameTextView.text = name
        birthdateTextView.text = birthdate
        genderTextView.text = gender
    }
}