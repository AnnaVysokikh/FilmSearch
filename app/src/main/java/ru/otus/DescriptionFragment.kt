package ru.otus

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class DescriptionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val film = arguments?.getSerializable(ARG_VALUE) as FilmInfo
        view.findViewById<TextView>(R.id.name_desc).text = film.name
        view.findViewById<TextView>(R.id.text_desc).text = film.description
        view.findViewById<ImageView>(R.id.image_desc).setImageResource(film.resId)
        view.findViewById<Button>(R.id.invitation).setOnClickListener {
            sendMail(view)
        }
    }

    private fun sendMail(view: View) {
        val recipient = ""
        val mIntent = Intent(Intent.ACTION_SEND)
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.invitation)
        mIntent.putExtra(Intent.EXTRA_TEXT, R.string.invitation_text)
        try {
            startActivity(Intent.createChooser(mIntent, "Send Email"))
        }
        catch (e: Exception){
            Toast.makeText(view.context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val ARG_VALUE = "film_description"

        fun create(value: FilmInfo): DescriptionFragment {
            val fragment = DescriptionFragment()
            val arguments = Bundle()
            arguments.putSerializable(ARG_VALUE, value)
            fragment.arguments = arguments
            return fragment
        }
    }
}