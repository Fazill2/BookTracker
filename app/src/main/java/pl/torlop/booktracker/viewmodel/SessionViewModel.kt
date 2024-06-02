package pl.torlop.booktracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pl.torlop.booktracker.dao.ReadingSessionDao
import pl.torlop.booktracker.entity.ReadingSession
import pl.torlop.booktracker.entity.SumDurationByDate
import java.util.*

class SessionViewModel(private val sessionDao: ReadingSessionDao) : ViewModel(){
    fun getAllSessions(): Flow<List<ReadingSession>> {
        return sessionDao.getAllSessions()
    }

    fun getSessionsByIsbn(isbn: String): Flow<List<ReadingSession>> {
        return sessionDao.getSessionsByIsbn(isbn)
    }

    fun getSessionById(id: Int): ReadingSession {
        return sessionDao.getSessionById(id)
    }

    fun getSessionsBetweenDates(startDate: Date, endDate: Date): Flow<List<ReadingSession>> {
        return sessionDao.getSessionsBetweenDates(startDate, endDate)
    }

    fun getSessionsByDate(date: Date): Flow<List<ReadingSession>> {
        return sessionDao.getSessionsByDate(date)
    }

    fun getSessionsByIsbnBetweenDates(isbn: String, startDate: Date, endDate: Date): Flow<List<ReadingSession>> {
        return sessionDao.getSessionsByIsbnBetweenDates(isbn, startDate, endDate)
    }

    fun getSessionsByIsbnAndDate(isbn: String, date: Date): Flow<List<ReadingSession>> {
        return sessionDao.getSessionsByIsbnAndDate(isbn, date)
    }

    fun getDailyReadingTime(): Flow<List<SumDurationByDate>> {
        return sessionDao.getDailyReadingTime()
    }

    fun getDailyReadingTimeBetweenDates(startDate: Date, endDate: Date): Flow<List<SumDurationByDate>> {
        return sessionDao.getDailyReadingTimeBetweenDates(startDate, endDate)
    }

    fun update(session: ReadingSession) {
        sessionDao.update(session)
    }

    fun deleteSession(id: Int) {
        sessionDao.deleteSession(id)
    }

    fun getTotalReadingTime(): Flow<Long> {
        return sessionDao.getTotalReadingTime()
    }

    fun getTotalPagesRead(): Flow<Int> {
        return sessionDao.getTotalPagesRead()
    }

    fun addSession(session: ReadingSession) {
        viewModelScope.launch {
            sessionDao.insert(session)
        }
    }

    fun upsertAll(sessions: List<ReadingSession>) {
        viewModelScope.launch {
            sessionDao.upsertAll(sessions)
        }
    }
}