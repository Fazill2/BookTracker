package pl.torlop.booktracker

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.torlop.booktracker.dao.BookDao
import pl.torlop.booktracker.dao.ReadingSessionDao
import pl.torlop.booktracker.entity.Book
import pl.torlop.booktracker.entity.Converters
import pl.torlop.booktracker.entity.ReadingSession

@Database(entities = [Book::class, ReadingSession::class], version = 5)
@TypeConverters(Converters::class)

abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun readingSessionDao(): ReadingSessionDao
}
