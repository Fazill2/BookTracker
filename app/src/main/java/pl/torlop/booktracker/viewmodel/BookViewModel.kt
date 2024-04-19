package pl.torlop.booktracker.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pl.torlop.booktracker.AppDatabase
import pl.torlop.booktracker.dao.BookDao
import pl.torlop.booktracker.entity.Book

class BookViewModel(private val dao: BookDao) : ViewModel() {

    fun getAllBooks(): Flow<List<Book>> {
        return dao.getAllBooksFlow()
    }

    fun selectBookById(isbn: String): Book {
        return dao.selectBookById(isbn)
    }

    fun addBook(book: Book) {
        viewModelScope.launch {
            dao.insertAll(book)
        }
    }
}