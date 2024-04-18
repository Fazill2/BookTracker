package pl.torlop.booktracker

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.room.Room
import pl.torlop.booktracker.entity.Book
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun BookListView(drawerState: DrawerState, db: AppDatabase?) {
    val scope = rememberCoroutineScope()
    val books = listOf(
        Book(
            isbn = "978-3-16-148410-0",
            title = "The Catcher in the Rye",
            author = "J.D. Salinger",
            pages = 277,
            genre = "Novel",
            rating = 5,
            description = "The Catcher in the Rye is a novel by J. D. Salinger, partially published in serial form in 1945–1946 and as a novel in 1951. It was originally intended for adults but is often read by adolescents for its themes of angst, alienation, and as a critique on superficiality in society. It has been translated widely. Around one million copies are sold each year, with total sales of more than 65 million books. The novel's protagonist Holden Caulfield has become an icon for teenage rebellion. The novel also deals with complex issues of innocence, identity, belonging, loss, connection, and alienation.",
            coverUrl = "https://upload.wikimedia.org/wikipedia/en/3/32/Rye_catcher.jpg"),
        Book(
            isbn = "978-3-16-148410-1",
            title = "1984",
            author = "George Orwell",
            pages = 328,
            genre = "Dystopian",
            rating = 5,
            description = "Nineteen Eighty-Four: A Novel, often referred to as 1984, is a dystopian social science fiction novel by the English novelist George Orwell. It was published on 8 June 1949 by Secker & Warburg as Orwell's ninth and final book completed in his lifetime. Thematically, Nineteen Eighty-Four centres on the consequences of totalitarianism, mass surveillance, and repressive regimentation of persons and behaviours within society.",
            coverUrl = "https://upload.wikimedia.org/wikipedia/en/c/c3/1984first.jpg")
    )
    val bookState = rememberLazyListState()
    val bookListState = remember {
        mutableStateOf(books)
    }
//    scope.launch {
//
//    }
//    val bookDao = db?.bookDao()!!
//    val books = bookDao.selectAll()
//    val booksState = rememberLazyListState(
//
//    )

    LazyColumn(
        state = bookState,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        items(
            items = bookListState.value,
            key = { book ->
                // Return a stable + unique key for the item
                book.isbn
            }
        ) { book ->
            println(book.title)
            BookListItem(book)
        }
    }
}

@Composable
fun BookListItem(book: Book) {
    Row {
        Text(text = book.title)
        Text(text = book.author)
    }
}