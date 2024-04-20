package pl.torlop.booktracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import pl.torlop.booktracker.entity.getEmptyBook
import pl.torlop.booktracker.viewmodel.BookViewModel

@Composable
fun BookDetailsView(drawerState: DrawerState, viewModel: BookViewModel, navController: NavController, isbn: String) {
    val book = viewModel.selectBookById(isbn).collectAsState(initial = getEmptyBook())

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row (
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainer).padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AsyncImage(
                modifier = Modifier.size(140.dp),
                contentScale = ContentScale.FillHeight,
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
    }

}