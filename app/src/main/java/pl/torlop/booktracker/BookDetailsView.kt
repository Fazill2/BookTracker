package pl.torlop.booktracker

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
import pl.torlop.booktracker.entity.ReadingStatus
import pl.torlop.booktracker.entity.getEmptyBook
import pl.torlop.booktracker.viewmodel.BookViewModel

@Composable
fun BookDetailsView(drawerState: DrawerState, viewModel: BookViewModel, navController: NavController, isbn: String) {
    println(isbn)
    val book = viewModel.selectBookById(isbn).collectAsState(initial = getEmptyBook())
    val readingStatus = remember { mutableStateOf(book.value.readingStatus) }


    val onClickStartReading: () -> Unit = { viewModel.startReading(book.value) }
    val onClickFinishReading: () -> Unit = { viewModel.finishReading(book.value) }
    BookDetails(book, onClickStartReading, onClickFinishReading)
}


@Composable
fun BookDetails(book: State<Book>, onClickStartReading: () -> Unit, onClickFinishReading: () -> Unit){
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        println(book.value.coverUrl)
        Row (
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainer).padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AsyncImage(
                modifier = Modifier.padding(8.dp).height(140.dp).widthIn(0.dp, 100.dp),
                contentScale = ContentScale.Fit,
                model = ImageRequest.Builder(LocalContext.current)
                    .data(book.value.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(book.value.title, style = MaterialTheme.typography.titleLarge)
                Text(book.value.author, style = MaterialTheme.typography.titleMedium)
                Text("Pages: ${book.value.pages}", style = MaterialTheme.typography.bodyMedium)
                Text("Genre: ${book.value.genre}", style = MaterialTheme.typography.bodyMedium)
                Text("ISBN: ${book.value.isbn}", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Text(book.value.description, style = MaterialTheme.typography.bodyMedium)
        if (book.value.readingStatus == "" || book.value.readingStatus == ReadingStatus.NOT_STARTED.name) {
            Button(onClick = onClickStartReading) {
                Text("Start reading")
            }

        } else if (book.value.readingStatus == ReadingStatus.IN_PROGRESS.name) {
            Text("Started reading on ${book.value.dateStarted}")
            Button(onClick = onClickFinishReading) {
                Text("Finish reading")
            }
        } else {
            Text("Started reading on ${book.value.dateStarted}")
            Text("Finished reading on ${book.value.dateFinished}")
        }
    }
}
