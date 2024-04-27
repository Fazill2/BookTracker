package pl.torlop.booktracker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.room.Room
import pl.torlop.booktracker.entity.Book
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import pl.torlop.booktracker.entity.ReadingStatus
import pl.torlop.booktracker.navigation.MainNavOption

import pl.torlop.booktracker.viewmodel.BookViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookListView(drawerState: DrawerState, viewModel: BookViewModel,  navController: NavController) {
    val bookList = viewModel.getAllBooks().collectAsState(initial = emptyList())
    val bookInProgressList = viewModel.selectBookByReadingStatus(ReadingStatus.IN_PROGRESS.name)
        .collectAsState(initial = emptyList())
    val tabs = listOf("All", "In Progress")
    var selectedTabIndex by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }
        when(selectedTabIndex){
            0 -> BookList(bookList.value, navController)
            1 -> BookList(bookInProgressList.value, navController)
        }
    }
}

@Composable
fun BookList(bookList: List<Book>, navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = bookList,
            key = { book ->
                book.isbn
            }
        ) { book ->
            BookListItem(book, navController)
        }
    }
}

@Composable
fun BookListItem(book: Book, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp)
            .clickable {
                navController.navigate("bookDetails/${book.isbn}")
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            AsyncImage(
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.FillHeight,
                contentDescription = book.title,
                error = painterResource(R.drawable.missing_cover),
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
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LinearProgressIndicator(
                progress = { book.currentPages.toFloat() / book.pages.toFloat() },
                modifier = Modifier.fillMaxWidth(0.7f),
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(text = "${book.currentPages}/${book.pages}", style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
