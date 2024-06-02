package pl.torlop.booktracker

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import pl.torlop.booktracker.entity.Book
import pl.torlop.booktracker.entity.ReadingSession
import pl.torlop.booktracker.entity.ReadingStatus
import pl.torlop.booktracker.entity.getEmptyBook
import pl.torlop.booktracker.ui.components.BookDetailsComponent
import pl.torlop.booktracker.viewmodel.BookViewModel
import pl.torlop.booktracker.viewmodel.SessionViewModel

@Composable
fun BookDetailsView(drawerState: DrawerState, viewModel: BookViewModel, sessionViewModel: SessionViewModel, navController: NavController, isbn: String) {
    println(isbn)
    val book = viewModel.selectBookById(isbn).collectAsState(initial = getEmptyBook())
    val readingStatus = remember { mutableStateOf(book.value.readingStatus) }
    val readingSessions = sessionViewModel.getSessionsByIsbn(isbn).collectAsState(initial = emptyList())
    val context = LocalContext.current
    val onClickStartReading: () -> Unit = {
        navController.navigate("newSession/${book.value.isbn}")
    }

    val deleteSession = { session: ReadingSession ->


        sessionViewModel.deleteSession(session)
        Toast.makeText(context, "Session deleted", Toast.LENGTH_SHORT).show()
    }
    BookDetailsComponent(book, readingSessions, onClickStartReading, deleteSession)
}