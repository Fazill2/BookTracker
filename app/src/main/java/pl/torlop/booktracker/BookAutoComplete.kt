package pl.torlop.booktracker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import pl.torlop.booktracker.entity.Book

@Composable
fun BookAutoComplete(
    bookList: List<Book>,
    onBookSelected: (Book) -> Unit,
    preselectedBook: Book? = null,
    label: String = "Search books"
) {
    var searchText by remember { mutableStateOf("") }
    val filteredBooks = remember { mutableStateOf<List<Book>>(emptyList()) }
    var selectedIsbn by remember { mutableStateOf("") }
    LaunchedEffect(preselectedBook) {
        searchText = preselectedBook?.title ?: ""
    }
    Column(
        modifier = Modifier.padding(16.dp).background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp).fillMaxWidth()
    ) {
        TextField(
            value = searchText,
            onValueChange = { newText ->
                selectedIsbn = ""
                searchText = newText
                filteredBooks.value = bookList.filter {
                    it.title.contains(newText, ignoreCase = true)
                }
            },
            label = { Text(label) },
            modifier = Modifier.padding(8.dp).fillMaxWidth()
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth().heightIn(0.dp, 200.dp)
        ) {
            items(filteredBooks.value) { book ->
                BookAutocompleteItem(
                    book = book,
                    onClick = {
                        onBookSelected(book)
                        searchText = book.title
                        selectedIsbn = book.isbn
                        filteredBooks.value = bookList.filter {
                            it.title.contains(searchText, ignoreCase = true)
                        }
                    },
                    isSelected = book.isbn == selectedIsbn
                )
            }
        }
    }
}

@Composable
fun BookAutocompleteItem(book: Book, onClick: () -> Unit, isSelected: Boolean) {
    Row(
        modifier = Modifier.clickable { onClick() }
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        AsyncImage(
            modifier = Modifier.size(50.dp),
            contentScale = ContentScale.FillHeight,
            contentDescription = book.title,
            model = ImageRequest.Builder(LocalContext.current)
                .data(book.coverUrl)
                .crossfade(true)
                .build(),

            )
        Column {
            Text(
                text = book.title,
                modifier = Modifier.padding(start = 8.dp).fillMaxWidth(0.8f),
                style = MaterialTheme.typography.titleSmall,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "ISBN: " + book.isbn,
                modifier = Modifier.padding(start = 8.dp).fillMaxWidth(0.8f),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        }

    }
}