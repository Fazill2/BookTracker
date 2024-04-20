package pl.torlop.booktracker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.room.Room
import pl.torlop.booktracker.entity.Book
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import pl.torlop.booktracker.navigation.MainNavOption

import pl.torlop.booktracker.viewmodel.BookViewModel

@Composable
fun BookListView(drawerState: DrawerState, viewModel: BookViewModel,  navController: NavController) {
    val bookList = viewModel.getAllBooks().collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = bookList.value,
            key = { book ->
                // Return a stable + unique key for the item
                book.isbn
            }
        ) { book ->
            println(book.title)
            BookListItem(book, navController)
        }
    }
}

@Composable
fun BookListItem(book: Book, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp)
            .clickable {
                navController.navigate("bookDetails/${book.isbn}")
            }
    ) {
        val placeholder = Color.Gray
        AsyncImage(
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.FillHeight,
            contentDescription = book.title,
            model = ImageRequest.Builder(LocalContext.current)
                .data(book.coverUrl)
                .crossfade(true)
                .build(),

        )
        Column(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        ) {
            Text(text = book.title, style = MaterialTheme.typography.labelLarge)
            Text(text = book.author, style = MaterialTheme.typography.labelMedium)
        }
    }
}
