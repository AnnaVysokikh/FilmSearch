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

        val name = intent.getStringExtra(EXTRA_NAME_FILM)
        when (name){
            DescriptionActivity.HOLOD ->
                findViewById<ImageView>(R.id.image_desc).setImageResource(R.drawable.holod)
            DescriptionActivity.ZOLUSHKA ->
                findViewById<ImageView>(R.id.image_desc).setImageResource(R.drawable.zolushka)
        }
        findViewById<TextView>(R.id.text_desc).text = catalog[name]

        findViewById<Button>(R.id.invitation).setOnClickListener {
            SendMail()
        }
    }

    private fun SendMail() {
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

    private companion object{
        const val EXTRA_NAME_FILM = "name_film"
        const val HOLOD = "holod"
        const val ZOLUSHKA = "zolushka"
    }

    val catalog = mapOf(
        HOLOD to "Комедийное анимационное приключение от студии Disney. Когда древнее предсказание сбывается и королевство погружается в объятья вечной зимы, трое бесстрашных героев — принцесса Анна, отважный Кристофф и его верный олень Свен — отправятся в горы, чтобы найти сестру Анны, которая может снять с королевства леденящее заклятье. По пути их ждет множество увлекательных сюрпризов и захватывающих приключений — встреча с мистическими троллями, знакомство с очаровательным снеговиком по имени Олаф, горные вершины покруче Эвереста и магия в каждой снежинке. Анне и Кристоффу предстоит сплотиться и противостоять могучей стихии, чтобы спасти королевство и тех, кто им дорог!",
        ZOLUSHKA to "Волшебная история Золушки от Disney, покорившая многие поколения своей восхитительной музыкой и незабываемыми персонажами! Золушка верит, что когда-нибудь сбудутся все ее мечты. Взмах волшебной палочки доброй феи — и лохмотья девочки превращаются в прекрасное платье, в котором Золушка отправляется на королевский бал, чтобы встретить принца. Но когда часы пробили полночь, чары рассеялись, и наряд Золушки превратился в старые обноски — осталась только хрустальная туфелька... Насладитесь классической сказкой от Disney в кругу семьи, и она навсегда останется в вашем сердце. И помните: мечты действительно сбываются!"

    )
}