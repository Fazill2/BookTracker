package pl.torlop.booktracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import pl.torlop.booktracker.entity.*
import pl.torlop.booktracker.navigation.MainNavOption
import pl.torlop.booktracker.ui.components.IntegerInputField
import pl.torlop.booktracker.viewmodel.BookViewModel
import pl.torlop.booktracker.viewmodel.SessionViewModel
import java.time.Instant
import java.util.*

@Composable
fun NewSessionView(drawerState: DrawerState, viewModel: BookViewModel, sessionViewModel: SessionViewModel,
                   navController: NavController, isbn: String? = null) {
    val bookList = viewModel.getAllBooks().collectAsState(initial = emptyList())
    val book = viewModel.selectBookById(isbn ?: "").collectAsState(initial = getEmptyBook())
    val selectedBook = remember { mutableStateOf(book.value) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        Text(text = "Add new reading session", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
        BookAutoComplete(bookList.value, onBookSelected = { selectedBook.value = it }, preselectedBook = book.value, selectedIsb = isbn ?: "")
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = selectedBook.value.isbn.isNotEmpty()
        ) {
            Text(text = "Start reading session")
        }
        Button(
            onClick = { navController.navigate("addSessionManually/${selectedBook.value.isbn}") },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = selectedBook.value.isbn.isNotEmpty()
        ) {
            Text(text = "Add session manually")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSessionManuallyView(drawerState: DrawerState, viewModel: BookViewModel, sessionViewModel: SessionViewModel,
                           navController: NavController, isbn: String){
    println(isbn)
    val book = viewModel.selectBookById(isbn).collectAsState(initial = getEmptyBook())

    ReadingSessionForm(navController, viewModel, sessionViewModel, book.value)
}

private fun convertMillisToDate(millis: Long): Date {
    return Date.from(Instant.ofEpochMilli(millis))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingSessionForm(navController: NavController, viewModel: BookViewModel, sessionViewModel: SessionViewModel, book: Book){
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis <= System.currentTimeMillis()
        }
    })
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    }
    val duration = remember { mutableStateOf("") }
    val pagesStart = remember { mutableStateOf("0") }
    val pagesEnd = remember { mutableStateOf("0") }

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
                value = pagesStart.value.toIntOrNull() ?: 0,
                onValueChange = { pagesStart.value = it.toString() },
                minValue = 0,
                maxValue = pagesEnd.value.toIntOrNull() ?: book.pages,
                label = "Starting page",
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            IntegerInputField(
                value = pagesEnd.value.toIntOrNull() ?: 0,
                onValueChange = { pagesEnd.value = it.toString() },
                minValue = pagesStart.value.toIntOrNull() ?: 0,
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

fun updateBookAfterSession(book: Book, session: ReadingSession): Book {
    book.currentPages = session.pagesEnd
    if (book.pages == session.pagesEnd) {
        book.readingStatus = ReadingStatus.FINISHED.name
        book.dateFinished = session.date.toString()
    } else {
        book.readingStatus = ReadingStatus.IN_PROGRESS.name
        if (book.dateStarted.isEmpty()) {
            book.dateStarted = session.date.toString()
        }
    }
    return book
}
