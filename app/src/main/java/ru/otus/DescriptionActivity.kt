package ru.otus

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DescriptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        val film = intent.getSerializableExtra("film") as FilmInfo
        findViewById<TextView>(R.id.name_desc).text = film.name
        findViewById<TextView>(R.id.text_desc).text = film.description
        findViewById<ImageView>(R.id.image_desc).setImageResource(film.resId)

        findViewById<Button>(R.id.invitation).setOnClickListener {
            sendMail()
        }
    }

    private fun sendMail() {
        val recipient = ""
        val mIntent = Intent(Intent.ACTION_SEND)
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, "Приглашение в кино")
        mIntent.putExtra(Intent.EXTRA_TEXT, "Пошли в кино")
        try {
            startActivity(Intent.createChooser(mIntent, "Send Email"))
        }
        catch (e: Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }
}