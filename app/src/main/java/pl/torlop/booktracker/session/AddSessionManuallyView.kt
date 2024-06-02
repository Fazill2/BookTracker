package pl.torlop.booktracker.session

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import pl.torlop.booktracker.entity.Book
import pl.torlop.booktracker.entity.ReadingSession
import pl.torlop.booktracker.entity.getEmptyBook
import pl.torlop.booktracker.navigation.MainNavOption
import pl.torlop.booktracker.ui.components.IntegerInputField
import pl.torlop.booktracker.utils.Utils.Companion.convertMillisToDate
import pl.torlop.booktracker.utils.Utils.Companion.updateBookAfterSession
import pl.torlop.booktracker.viewmodel.BookViewModel
import pl.torlop.booktracker.viewmodel.SessionViewModel
import java.time.Instant
import java.util.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSessionManuallyView(drawerState: DrawerState, viewModel: BookViewModel, sessionViewModel: SessionViewModel,
                           navController: NavController, isbn: String){
    val book = viewModel.selectBookById(isbn).collectAsState(initial = getEmptyBook())
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis <= System.currentTimeMillis()
        }
    })
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    }
    val duration = remember { mutableStateOf("") }
    val currentPages = if (book.value.currentPages != book.value.pages) book.value.currentPages else 1
    val pagesStart = remember { mutableStateOf(currentPages.toString()) }
    val pagesEnd = remember { mutableStateOf(currentPages.toString()) }
    println("Current pages: $currentPages")
    println("Pages start: ${pagesStart.value}")
    println("Current pages: ${book.value.currentPages}")
    ReadingSessionForm(navController, viewModel, sessionViewModel, book.value,
        datePickerState, duration, pagesStart, pagesEnd, selectedDate)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingSessionForm(navController: NavController, viewModel: BookViewModel, sessionViewModel: SessionViewModel, book: Book,
                       datePickerState: DatePickerState, duration: MutableState<String>, pagesStart: MutableState<String>,
                       pagesEnd: MutableState<String>, selectedDate: Date?){
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),

        ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            IntegerInputField(
                value = duration.value.toIntOrNull() ?: 0,
                onValueChange = { duration.value = it.toString() },
                minValue = 0,
                maxValue = 1440,
                label = "Minutes",
                modifier = Modifier.fillMaxWidth(0.3f)
            )
            IntegerInputField(
                value = pagesStart.value.toIntOrNull() ?: 1,
                onValueChange = { pagesStart.value = it.toString() },
                minValue = 1,
                maxValue = book.pages,
                label = "Starting page",
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            IntegerInputField(
                value = pagesEnd.value.toIntOrNull() ?: 1,
                onValueChange = { pagesEnd.value = it.toString() },
                minValue = 1,
                maxValue = book.pages,
                label = "Ending page",
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        DatePicker(
            state = datePickerState
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (pagesEnd.value < pagesStart.value) {
                    Toast.makeText(context, "Ending page must be greater than starting page", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val session = ReadingSession(
                    bookIsbn = book.isbn,
                    date = selectedDate ?: Date(),
                    duration = duration.value.toIntOrNull() ?: 0,
                    pagesStart = pagesStart.value.toIntOrNull() ?: 0,
                    pagesEnd = pagesEnd.value.toIntOrNull() ?: 0
                )
                sessionViewModel.addSession(session)
                val updatedBook = updateBookAfterSession(book, session)
                viewModel.update(updatedBook)
                navController.navigate("bookDetails/${updatedBook.isbn}"){
                    NavOptionsBuilder().popUpTo(MainNavOption.HomeScreen.name){
                        inclusive = true
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = selectedDate != null && duration.value.isNotEmpty() && pagesStart.value.isNotEmpty()
                    && pagesEnd.value.isNotEmpty()
        ) {
            Text(text = "Add session")
        }
    }
}