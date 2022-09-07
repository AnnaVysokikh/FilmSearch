package ru.otus

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.otus.model.FilmModel

class FilmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    private val nameTv: TextView = itemView.findViewById(R.id.name_film)
    private val imageIv: ImageView = itemView.findViewById(R.id.image_film)
    private var btnfavorite: ImageView = itemView.findViewById(R.id.favorite_film)
    private val btnDetali: Button = itemView.findViewById(R.id.detali)
    var filmID: Int = 0

    fun bind(item: FilmModel
             ,onDetailsClick: (position: Int) -> Unit
             ,onFavoriteClick: (position: Int) -> Unit
    ) {
        filmID = item.id
        nameTv.text = item.name

        Glide.with(imageIv.context)
            .load(item.poster)
            .timeout(10000)
            .placeholder(R.drawable.ic_baseline_image_24)
            .error(com.google.android.material.R.drawable.mtrl_ic_error)
            .centerCrop()
            .into(imageIv)

        btnDetali.setOnClickListener {
            onDetailsClick(item.id)
        }

        btnfavorite.setOnClickListener {
            onFavoriteClick(item.id)
        }

        if (item.isFavorite == true) {
            btnfavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
        } else {
            btnfavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
    }

}