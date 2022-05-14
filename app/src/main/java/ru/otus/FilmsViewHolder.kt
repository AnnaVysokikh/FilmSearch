package ru.otus

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FilmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    private val nameTv: TextView = itemView.findViewById(R.id.name_film)
    private val imageIv: ImageView = itemView.findViewById(R.id.image_film)
    private var favoriteIv: ImageView = itemView.findViewById(R.id.favorite_film)
    private val btn: Button = itemView.findViewById(R.id.detali)

    fun bind(item:FilmInfo, listener: FilmsAdapter.FilmsClickListener) {
        nameTv.text = item.name
        imageIv.setImageResource(item.resId)

        if (item.isFavorite) {
            favoriteIv.setImageResource(R.drawable.ic_baseline_favorite_24)
        } else {
            favoriteIv.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }

        favoriteIv.setOnClickListener {
            listener.onFavoriteClick(item, adapterPosition)
        }
        btn.setOnClickListener{
            listener.onDetailsClick(item, adapterPosition)
        }
    }

}