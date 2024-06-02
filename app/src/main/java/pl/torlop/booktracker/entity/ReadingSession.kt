package pl.torlop.booktracker.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.Date

@Entity
data class ReadingSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var bookIsbn: String,
    var duration: Int,
    var date: Date,
    var pagesStart : Int,
    var pagesEnd: Int
)

fun getEmptyReadingSession(): ReadingSession {
    return ReadingSession(0, "", 0, Date.from(Instant.now()), 0, 0)
}

data class SumDurationByDate(
    val date: Date,
    val duration: Int
)