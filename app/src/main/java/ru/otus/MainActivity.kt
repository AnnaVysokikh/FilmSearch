package ru.otus

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView
import ru.otus.R.color

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.holod).setOnClickListener {
            currentFilm = HOLOD
            findViewById<TextView>(R.id.name_holod).setTextColor(Color.rgb(200,0,0))
            findViewById<TextView>(R.id.name_zolushka).setTextColor(Color.rgb(0,0,0))
            val intent = Intent(this, DescriptionActivity::class.java)
            intent.putExtra(NAME_FILM, HOLOD)
            startActivity(intent)
        }

        findViewById<Button>(R.id.zolushka).setOnClickListener {
            currentFilm = ZOLUSHKA
            findViewById<TextView>(R.id.name_zolushka).setTextColor(Color.rgb(200,0,0))
            findViewById<TextView>(R.id.name_holod).setTextColor(Color.rgb(0,0,0))
            val intent = Intent(this, DescriptionActivity::class.java)
            intent.putExtra(MainActivity.NAME_FILM, MainActivity.ZOLUSHKA)
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentFilm", currentFilm)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val film = savedInstanceState?.getString("currentFilm", "")
        if (film != "")
        if (film == HOLOD){
            findViewById<TextView>(R.id.name_holod).setTextColor(Color.rgb(200,0,0))
            findViewById<TextView>(R.id.name_zolushka).setTextColor(Color.rgb(0,0,0))
        } else if (film == ZOLUSHKA){
            findViewById<TextView>(R.id.name_zolushka).setTextColor(Color.rgb(200,0,0))
            findViewById<TextView>(R.id.name_holod).setTextColor(Color.rgb(0,0,0))
        }

    }

    var currentFilm = ""
    private companion object{
        const val NAME_FILM = "name_film"
        const val HOLOD = "holod"
        const val ZOLUSHKA = "zolushka"
    }
}