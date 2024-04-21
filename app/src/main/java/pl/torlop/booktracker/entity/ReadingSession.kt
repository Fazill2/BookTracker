package pl.torlop.booktracker.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReadingSession(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val bookIsbn: String,
    val timeStarted: String,
    val timeEnded: String,
    val pagesRead: Int,
    val notes: String
)

fun getEmptyReadingSession(): ReadingSession {
    return ReadingSession(0, "", "", "", 0, "")
}

