package pl.torlop.booktracker.firebase

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.UserData
import pl.torlop.booktracker.entity.Book
import pl.torlop.booktracker.entity.ReadingSession
import pl.torlop.booktracker.viewmodel.BookViewModel
import pl.torlop.booktracker.viewmodel.SessionViewModel
import java.util.*
import kotlin.collections.HashMap

class FirebaseUtils(private val auth: FirebaseAuth, private val db: FirebaseFirestore,
    private val sessionViewModel: SessionViewModel, private val bookViewModel: BookViewModel) {
    fun syncData(books: List<Book>, sessions: List<ReadingSession>, context: Context) {
        if (auth.currentUser != null) {
            readUserData()
            writeUserBooksAndSessions(books, sessions, context)
        } else {
            Toast.makeText(context, "You need to be logged in to sync data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userDocRef = db.collection("users").document(userId)
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val books = hashMapListToBooks(document.data?.get("books") as List<HashMap<String, Any>>)
                        val sessions = hashMapListToSessions(document.data?.get("sessions") as List<HashMap<String, Any>>)
                        bookViewModel.upsertAll(books)
                        sessionViewModel.upsertAll(sessions)
                    } else {
                        Log.d("FirebaseUtils", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("FirebaseUtils", "get failed with ", exception)
                }
        }
    }

    private fun writeUserBooksAndSessions(books: List<Book>, sessions: List<ReadingSession>, context: Context) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userDocRef = db.collection("users").document(userId)
            userDocRef.set(mapOf(
                "books" to books,
                "sessions" to sessions
            ))
                .addOnSuccessListener {
                    Toast.makeText(context, "Data synced successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error syncing data", Toast.LENGTH_SHORT).show()
                    Log.w("FirebaseUtils", "Error writing document", e)
                }
        }
    }

    fun hashMapListToBooks(list: List<HashMap<String, Any>>): List<Book> {
        val books = mutableListOf<Book>()
        for (map in list) {
            books.add(hashMapToBook(map))
        }
        return books
    }

    fun hashMapListToSessions(list: List<HashMap<String, Any>>): List<ReadingSession> {
        val sessions = mutableListOf<ReadingSession>()
        for (map in list) {
            sessions.add(hashMapToSession(map))
        }
        return sessions
    }

    private fun hashMapToBook(map: HashMap<String, Any>): Book {
        val pages = map["pages"] as Long
        val currentPages = map["currentPages"] as Long
        val rating = map["rating"] as Long
        val timesRead = map["timesRead"] as Long
        return Book(
            isbn = map["isbn"] as String,
            title = map["title"] as String,
            author = map["author"] as String,
            pages = pages.toInt(),
            readingStatus = map["readingStatus"] as String,
            dateStarted = map["dateStarted"] as String,
            dateFinished = map["dateFinished"] as String,
            coverUrl = map["coverUrl"] as String,
            currentPages = currentPages.toInt(),
            dateAdded = map["dateAdded"] as String,
            datePublished = map["datePublished"] as String,
            description = map["description"] as String,
            genre = map["genre"] as String,
            language = map["language"] as String,
            ownershipStatus = map["ownershipStatus"] as String,
            publisher = map["publisher"] as String,
            rating = rating.toInt(),
            timesRead = timesRead.toInt()
        )
    }

    private fun hashMapToSession(map: HashMap<String, Any>): ReadingSession {
        val id = map["id"] as Long
        val duration = map["duration"] as Long
        val pagesStart = map["pagesStart"] as Long
        val pagesEnd = map["pagesEnd"] as Long
        val timestamp = map["date"] as Timestamp
        val date = timestamp.toDate()
        return ReadingSession(
            id = id.toInt(),
            bookIsbn = map["bookIsbn"] as String,
            date = date,
            duration = duration.toInt(),
            pagesStart = pagesStart.toInt(),
            pagesEnd = pagesEnd.toInt()
        )
    }
}