package pl.torlop.booktracker.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import pl.torlop.booktracker.entity.ReadingSession
import java.util.Date

@Dao
interface ReadingSessionDao {
    @Query("SELECT * FROM readingsession")
    fun getAllSessions(): Flow<List<ReadingSession>>

    @Query("SELECT * FROM readingsession WHERE bookIsbn = (:isbn)")
    fun getSessionsByIsbn(isbn: String): Flow<List<ReadingSession>>

    @Query("SELECT * FROM readingsession WHERE id = (:id)")
    fun getSessionById(id: Int): ReadingSession

    @Query("SELECT * FROM readingsession WHERE date BETWEEN (:startDate) AND (:endDate)")
    fun getSessionsBetweenDates(startDate: Date, endDate: Date): Flow<List<ReadingSession>>

    @Query("SELECT * FROM readingsession WHERE date = (:date)")
    fun getSessionsByDate(date: Date): Flow<List<ReadingSession>>

    @Query("SELECT * FROM readingsession WHERE bookIsbn = (:isbn) AND date BETWEEN (:startDate) AND (:endDate)")
    fun getSessionsByIsbnBetweenDates(isbn: String, startDate: Date, endDate: Date): Flow<List<ReadingSession>>

    @Query("SELECT * FROM readingsession WHERE bookIsbn = (:isbn) AND date = (:date)")
    fun getSessionsByIsbnAndDate(isbn: String, date: Date): Flow<List<ReadingSession>>

    @Update
    fun update(session: ReadingSession)

    @Query("DELETE FROM readingsession WHERE id = (:id)")
    fun deleteSession(id: Int)

    @Upsert
    fun insert(session: ReadingSession)
}