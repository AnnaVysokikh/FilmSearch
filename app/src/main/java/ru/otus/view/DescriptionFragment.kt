package ru.otus.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import ru.otus.MainActivity
import ru.otus.NotificationReceiver
import ru.otus.R
import ru.otus.viewmodel.FilmsViewModel
import java.util.*

class DescriptionFragment : Fragment() {

    private val viewModel: FilmsViewModel by activityViewModels()
    private lateinit var btnWatchLater: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnWatchLater = view.findViewById(R.id.watch_later)
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

        val reminderDate = Calendar.getInstance()

        val timePicker = MaterialTimePicker.Builder()
            .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .build()

        val datePickerDialog = MaterialDatePicker.Builder.datePicker().build()

        datePickerDialog.addOnPositiveButtonClickListener { date ->
            timePicker.show(parentFragmentManager, "Time")
            timePicker.addOnPositiveButtonClickListener {
                reminderDate.timeInMillis = date
                reminderDate.set(Calendar.HOUR, timePicker.hour)
                reminderDate.set(Calendar.MINUTE, timePicker.minute)
                val name = viewModel.selectedFilm.value?.name
                val id = viewModel.selectedFilm.value?.id
                setNotification(requireContext(), reminderDate, name, id)

                Log.d("__OTUS__", (
                        DateUtils.formatDateTime(
                            requireContext(),
                            reminderDate.timeInMillis,
                            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
                                    or DateUtils.FORMAT_SHOW_TIME
                        )))
            }
        }

        btnWatchLater.setOnClickListener {
            datePickerDialog.show(parentFragmentManager, "Date")
        }
    }

    private fun setNotification(context: Context, date: Calendar, filmTitle: String?, filmId: Int?) {
        val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)
        //val alarmIntent = Intent(context, NotificationReceiver::class.java)
        val alarmIntent = Intent(filmId.toString(), null, context, NotificationReceiver::class.java)
        alarmIntent.putExtra(EXTRA_FILM_ID, filmId)
        alarmIntent.putExtra(EXTRA_FILM_NAME, filmTitle)

        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            filmId!!,
            alarmIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT )

        alarmManager?.set(AlarmManager.RTC_WAKEUP, date.timeInMillis, pendingIntent)
        btnWatchLater.setImageResource(R.drawable.ic_baseline_watch_later_24)
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
        const val EXTRA_FILM_ID = "id"
        const val EXTRA_FILM_NAME = "name"

    }
}