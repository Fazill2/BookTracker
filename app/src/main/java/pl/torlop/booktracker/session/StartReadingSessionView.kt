package pl.torlop.booktracker.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.torlop.booktracker.entity.Book
import pl.torlop.booktracker.entity.ReadingSession
import pl.torlop.booktracker.entity.getEmptyBook
import pl.torlop.booktracker.navigation.MainNavOption
import pl.torlop.booktracker.ui.components.IntegerInputField
import pl.torlop.booktracker.ui.components.StopwatchComponent
import pl.torlop.booktracker.utils.Utils.Companion.updateBookAfterSession
import pl.torlop.booktracker.viewmodel.BookViewModel
import pl.torlop.booktracker.viewmodel.SessionViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun StartReadingSessionView(drawerState: DrawerState, viewModel: BookViewModel, sessionViewModel: SessionViewModel,
                            navController: NavController, isbn: String) {
    val book = viewModel.selectBookById(isbn).collectAsState(initial = getEmptyBook())
    val time = remember { mutableLongStateOf(0L) }
    val isRunning = remember { mutableStateOf(false) }
    val currentPages = if (book.value.currentPages != book.value.pages) book.value.currentPages else 1
    val pagesStart = remember { mutableStateOf(currentPages.toString()) }
    val pagesEnd = remember { mutableStateOf(currentPages.toString()) }
    pagesStart.value = currentPages.toString()
    pagesEnd.value = currentPages.toString()
    val scope = rememberCoroutineScope()
    val onReset: () -> Unit = {
        time.longValue = 0L
        isRunning.value = false
    }
    val onStartStop: () -> Unit = {
        isRunning.value = !isRunning.value
        scope.launch {
            while (isRunning.value) {
                delay(1000)
                if (isRunning.value)
                    time.longValue++
            }
        }
    }
    val onSave: () -> Unit = {
        val session = ReadingSession(
            bookIsbn = book.value.isbn,
            date = Date(),
            duration = time.longValue.toInt() / 60,
            pagesStart = pagesStart.value.toIntOrNull() ?: 0,
            pagesEnd = pagesEnd.value.toIntOrNull() ?: 0
        )
        sessionViewModel.addSession(session)
        val updatedBook = updateBookAfterSession(book.value, session)
        viewModel.update(updatedBook)
        navController.navigate("bookDetails/${updatedBook.isbn}"){
            NavOptionsBuilder().popUpTo(MainNavOption.HomeScreen.name){
                inclusive = true
            }
        }


    }
    StartReadingSessionComponent(
        book = book.value,
        time = time.longValue,
        isRunning = isRunning.value,
        onStartStop = onStartStop,
        onReset = onReset,
        onSave = onSave,
        pagesStart = pagesStart,
        pagesEnd = pagesEnd,
        currentPages = currentPages
    )
}


@Composable
fun StartReadingSessionComponent(
    book: Book,
    time: Long,
    isRunning: Boolean,
    onStartStop: () -> Unit,
    onReset: () -> Unit,
    onSave: () -> Unit,
    pagesStart: MutableState<String>,
    pagesEnd: MutableState<String>,
    currentPages: Int
) {


    StopwatchComponent(
        time = time,
        isRunning = isRunning,
        onStartStop = onStartStop,
        onReset = onReset,
        onSave = onSave
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IntegerInputField(
            value = pagesStart.value.toIntOrNull() ?: 1,
            onValueChange = { pagesStart.value = it.toString() },
            minValue = 1,
            maxValue = pagesEnd.value.toIntOrNull() ?: book.pages,
            label = "Starting page",
            modifier = Modifier.fillMaxWidth(0.5f)
        )
        IntegerInputField(
            value = pagesEnd.value.toIntOrNull() ?: 1,
            onValueChange = { pagesEnd.value = it.toString() },
            minValue = pagesStart.value.toIntOrNull() ?: 1,
            maxValue = book.pages,
            label = "Ending page",
            modifier = Modifier.fillMaxWidth()
        )
    }
}