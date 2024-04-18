package pl.torlop.booktracker.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Book(
    @PrimaryKey val isbn: String,
    @ColumnInfo(name="title") val title: String,
    @ColumnInfo(name="author") val author: String,
    @ColumnInfo(name="pages") val pages: Int,
    @ColumnInfo(name="genre") val genre: String,
    @ColumnInfo(name="rating") val rating: Int,
    @ColumnInfo(name="description") val description: String,
    @ColumnInfo(name="coverUrl") val coverUrl: String
)