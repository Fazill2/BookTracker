package pl.torlop.booktracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.torlop.booktracker.entity.*
import pl.torlop.booktracker.ui.components.BookAutoComplete
import pl.torlop.booktracker.viewmodel.BookViewModel
import pl.torlop.booktracker.viewmodel.SessionViewModel

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
            onClick = { navController.navigate("startReadingSession/${selectedBook.value.isbn}") },
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






