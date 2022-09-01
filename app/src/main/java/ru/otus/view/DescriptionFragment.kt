package ru.otus.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import ru.otus.MainActivity
import ru.otus.R
import ru.otus.viewmodel.FilmsViewModel

class DescriptionFragment : Fragment() {

    private val viewModel: FilmsViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectedFilm.observe(viewLifecycleOwner) {
            it.apply {
                val imageDesc = view.findViewById<ImageView>(R.id.image_desc)

                Glide.with(imageDesc.context)
                    .load(this.poster)
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .error(com.google.android.material.R.drawable.mtrl_ic_error)
                    .centerCrop()
                    .into(imageDesc)

                view.findViewById<TextView>(R.id.name_desc).text = name
                view.findViewById<TextView>(R.id.text_desc).text = description

                view.findViewById<Button>(R.id.invitation).setOnClickListener {
                    sendMail(view)
                }
            } ?: throw IllegalStateException("No data uploaded")

            val toolbar: Toolbar = view.findViewById(R.id.toolbar)
            toolbar.setNavigationOnClickListener { (activity as MainActivity).onBackPressed() }
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
}