package pl.torlop.booktracker

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.torlop.booktracker.dao.BookDao
import pl.torlop.booktracker.entity.Book

@Database(entities = [Book::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}
