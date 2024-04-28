package pl.torlop.booktracker.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.torlop.booktracker.entity.Book
import pl.torlop.booktracker.entity.getEmptyBook
import pl.torlop.booktracker.ui.components.IntegerInputField
import pl.torlop.booktracker.ui.components.StopwatchComponent
import pl.torlop.booktracker.viewmodel.BookViewModel
import pl.torlop.booktracker.viewmodel.SessionViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun StartReadingSessionView(drawerState: DrawerState, viewModel: BookViewModel, sessionViewModel: SessionViewModel,
                            navController: NavController, isbn: String) {
    val book = viewModel.selectBookById(isbn).collectAsState(initial = getEmptyBook())
    val time = remember { mutableLongStateOf(0L) }
    val isRunning = remember { mutableStateOf(false) }

    val startPage = remember { mutableStateOf(book.value.currentPages) }

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
        val currentDate = LocalDate.now()


    }

}


@Composable
fun StartReadingSessionComponent(
    book: Book,
    time: Long,
    isRunning: Boolean,
    onStartStop: () -> Unit,
    onReset: () -> Unit,
    onSave: () -> Unit
) {
    val currentPages = if (book.currentPages == book.pages) book.currentPages + 1 else 1
    val pagesStart = remember { mutableStateOf(currentPages.toString()) }
    val pagesEnd = remember { mutableStateOf(currentPages.toString()) }

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
            minValue = currentPages,
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