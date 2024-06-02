package pl.torlop.booktracker.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.torlop.booktracker.entity.ReadingSession
import pl.torlop.booktracker.entity.SumDurationByDate
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
    suspend fun insert(session: ReadingSession)

    @Query("SELECT date, SUM(duration) as duration FROM readingsession GROUP BY date ORDER BY date ASC")
    fun getDailyReadingTime(): Flow<List<SumDurationByDate>>

    @Query("SELECT SUM(duration) FROM readingsession")
    fun getTotalReadingTime(): Flow<Long>

    @Query("SELECT SUM(pagesEnd - pagesStart) FROM readingsession")
    fun getTotalPagesRead(): Flow<Int>

    // get daily reading time in specific date range
    @Query("SELECT date, SUM(duration) as duration FROM readingsession WHERE date BETWEEN (:startDate) AND (:endDate) GROUP BY date ORDER BY date ASC")
    fun getDailyReadingTimeBetweenDates(startDate: Date, endDate: Date): Flow<List<SumDurationByDate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(sessions: List<ReadingSession>)
}