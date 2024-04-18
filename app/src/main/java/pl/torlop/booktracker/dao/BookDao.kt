package pl.torlop.booktracker.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import pl.torlop.booktracker.entity.Book

@Dao
interface BookDao {
    @Query("SELECT * FROM book")
    fun selectAll(): List<Book>

    @Query("SELECT * FROM book WHERE isbn = (:isbn)")
    fun selectBookById(isbn: String): Book

    @Query("SELECT * FROM book WHERE title = (:title)")
    fun selectBookByTitle(title: String): List<Book>

    @Query("SELECT * FROM book WHERE author = (:author)")
    fun selectBookByAuthor(author: String): List<Book>

    @Query("SELECT * FROM book WHERE genre = (:genre)")
    fun selectBookByGenre(genre: String): List<Book>

    @Insert
    fun insertAll(vararg books: Book)

    @Query("DELETE FROM book WHERE isbn = (:isbn)")
    fun deleteBook(isbn: String)

    @Delete
    fun delete(book: Book)
}