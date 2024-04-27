package pl.torlop.booktracker.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Book(
    @PrimaryKey val isbn: String,
    @ColumnInfo(name="title") var title: String,
    @ColumnInfo(name="author") val author: String,
    @ColumnInfo(name="pages") val pages: Int,
    @ColumnInfo(name="genre") val genre: String,
    @ColumnInfo(name="datePublished") val datePublished: String,
    @ColumnInfo(name="rating") val rating: Int,
    @ColumnInfo(name="description") val description: String,
    @ColumnInfo(name="coverUrl") val coverUrl: String,
    @ColumnInfo(name="publisher") val publisher: String,
    @ColumnInfo(name="language") val language: String,
    @ColumnInfo(name="currentPages") var currentPages: Int,
    @ColumnInfo(name="readingStatus") var readingStatus: String,
    @ColumnInfo(name="dateStarted") var dateStarted: String,
    @ColumnInfo(name="dateFinished") var dateFinished: String,
    @ColumnInfo(name="dateAdded") val dateAdded: String,
    @ColumnInfo(name="ownershipStatus") var ownershipStatus: String
)

fun getEmptyBook(): Book {
    return Book("", "", "", 0, "", "", 0, "", "", "", "", 0, "", "", "", "", "")
}

enum class ReadingStatus {
    NOT_STARTED,
    IN_PROGRESS,
    FINISHED
}

enum class OwnershipStatus {
    OWNED,
    WISHLIST,
    BORROWED,
    LENT,
    LOST
}