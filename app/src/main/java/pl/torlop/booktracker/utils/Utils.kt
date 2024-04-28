package pl.torlop.booktracker.utils

import androidx.datastore.preferences.core.stringPreferencesKey
import pl.torlop.booktracker.entity.Book
import pl.torlop.booktracker.entity.ReadingSession
import pl.torlop.booktracker.entity.ReadingStatus
import java.time.Instant
import java.util.*

class Utils {
    companion object {
        fun secondsToTimeString(seconds: Long): String {
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val secs = seconds % 60
            return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
        }

        fun convertMillisToDate(millis: Long): Date {
            return Date.from(Instant.ofEpochMilli(millis))
        }

        fun updateBookAfterSession(book: Book, session: ReadingSession): Book {
            book.currentPages = session.pagesEnd
            if (book.pages == session.pagesEnd) {
                book.readingStatus = ReadingStatus.FINISHED.name
                book.dateFinished = session.date.toString()
            } else {
                book.readingStatus = ReadingStatus.IN_PROGRESS.name
                if (book.dateStarted.isEmpty()) {
                    book.dateStarted = session.date.toString()
                }
            }
            return book
        }

        public val USER_NAME = stringPreferencesKey("username")
        val TOKEN_ID = stringPreferencesKey("token_id")
    }
}