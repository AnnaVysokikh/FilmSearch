package ru.otus.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(
    indices = [
        Index(value = ["id"]),
        Index(value = ["name"])
    ]
)

data class FilmModel (
    @Expose
    val positionID: Int?,
    @SerializedName(value="id", alternate = ["kinopoiskId", "filmId"])
    @Expose
    @PrimaryKey
    val id: Int,
    @SerializedName("nameRu")
    @Expose
    val name: String,
    @SerializedName("posterUrlPreview")
    @Expose
    val poster: String,
    @Expose
    var description: String? = "",
    @Expose
    var isFavorite: Boolean? = false,
    @Expose
    var watchLater: Long? = -1
)