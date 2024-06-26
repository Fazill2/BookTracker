package pl.torlop.booktracker.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pl.torlop.booktracker.AppDatabase
import pl.torlop.booktracker.dao.BookDao
import pl.torlop.booktracker.entity.Book
import pl.torlop.booktracker.entity.ReadingStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BookViewModel(private val dao: BookDao) : ViewModel() {

    fun getAllBooks(): Flow<List<Book>> {
        return dao.getAllBooksFlow()
    }

    fun selectBookById(isbn: String): Flow<Book> {
        return dao.selectBookById(isbn)
    }


    fun selectBookByReadingStatus(status: String): Flow<List<Book>> {
        return dao.selectBookByReadingStatus(status)
    }

    fun startReading(book: Book) {
        viewModelScope.launch {
            book.readingStatus = ReadingStatus.IN_PROGRESS.name
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val dateStarted = currentDateTime.format(formatter)
            book.dateStarted = dateStarted
            dao.update(book)
        }
    }

    fun finishReading(book: Book) {
        viewModelScope.launch {
            book.readingStatus = ReadingStatus.FINISHED.name
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val dateFinished = currentDateTime.format(formatter)
            book.dateFinished = dateFinished
            dao.update(book)
        }
    }

    fun getNumberOfReadBooks(): Flow<Int> {
        return dao.getNumberOfReadBooks()
    }

    fun update(book: Book) {
        viewModelScope.launch {
            dao.update(book)
        }
    }

    fun addBook(book: Book) {
        viewModelScope.launch {
            dao.insertAll(book)
        }
    }

    fun upsertAll(books: List<Book>) {
        viewModelScope.launch {
            dao.upsertAll(books)
        }
    }


}