package pl.torlop.booktracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import pl.torlop.booktracker.R
import pl.torlop.booktracker.entity.Book
import pl.torlop.booktracker.entity.ReadingSession
import pl.torlop.booktracker.entity.ReadingStatus


@Composable
fun BookDetailsComponent(book: State<Book>, readingSessions: State<List<ReadingSession>>, onClickStartReading: () -> Unit, onClickFinishReading: () -> Unit){
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
                contentDescription = book.value.title,
                error = painterResource(R.drawable.missing_cover),
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
        if (readingSessions.value.isNotEmpty()) {
            Text("Reading sessions", style = MaterialTheme.typography.titleMedium)
            SessionListComponent(readingSessions.value)
        }

        val label = when (book.value.readingStatus) {
            ReadingStatus.IN_PROGRESS.name -> "Continue reading"
            ReadingStatus.FINISHED.name -> "Read again"
            else -> {
                "Start reading"
            }
        }

        Button(onClick = onClickStartReading, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(label)
        }
    }
}