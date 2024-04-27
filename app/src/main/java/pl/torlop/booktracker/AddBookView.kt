package pl.torlop.booktracker

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.torlop.booktracker.api.ApiConstants.Companion.BASE_URL
import pl.torlop.booktracker.api.GoogleBookApiResponse
import pl.torlop.booktracker.api.IsbnApiService
import pl.torlop.booktracker.entity.Book
import pl.torlop.booktracker.entity.ReadingStatus
import pl.torlop.booktracker.navigation.MainNavOption
import pl.torlop.booktracker.viewmodel.BookViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AddBookView(drawerState: DrawerState, viewModel: BookViewModel,  navController: NavController) {
    var isbn by rememberSaveable { mutableStateOf("") }
    var title by rememberSaveable { mutableStateOf("") }
    var author by rememberSaveable { mutableStateOf("") }
    var datePublished by rememberSaveable { mutableStateOf("") }
    var pages by rememberSaveable { mutableStateOf("") }
    var genre by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var coverUrl by rememberSaveable { mutableStateOf("") }
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiService = retrofit.create(IsbnApiService::class.java)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
       Row(
           modifier = Modifier.fillMaxWidth(),
           horizontalArrangement = Arrangement.spacedBy(16.dp)
       ) {
           TextField(
               value = isbn,
               onValueChange = { isbn = it },
               label = { Text("ISBN") },
               modifier = Modifier.fillMaxWidth(0.5f)
           )
           Button(
               onClick = {
                   val formattedIsbn = "isbn:" + isbn.replace(Regex("[^0-9]"), "")
                   val call = apiService.getBookByIsbnGoogleApi(formattedIsbn)
                   call.enqueue(object : Callback<GoogleBookApiResponse> {
                       override fun onResponse(
                           call: Call<GoogleBookApiResponse>,
                           response: Response<GoogleBookApiResponse>
                       ) {
                           val bookData = response.body()
                           if (bookData?.items != null && bookData.items.isNotEmpty()
                               && bookData.items[0].volumeInfo != null
                           ) {
                               val tempCover = if (bookData.items[0].volumeInfo?.imageLinks?.thumbnail != null) {
                                   bookData.items[0].volumeInfo?.imageLinks?.thumbnail.toString()
                                       .replace("http://", "https://")
                               } else {
                                   ""
                               }
                               title = bookData.items[0].volumeInfo?.title ?: ""
                               author = bookData.items[0].volumeInfo?.authors?.get(0) ?: ""
                               pages = bookData.items[0].volumeInfo?.pageCount ?: ""
                               genre = bookData.items[0].volumeInfo?.categories?.get(0) ?: ""
                               description = bookData.items[0].volumeInfo?.description ?: ""
                               coverUrl = tempCover
                               datePublished = bookData.items[0].volumeInfo?.publishedDate ?: ""
                           }
                       }

                       override fun onFailure(call: Call<GoogleBookApiResponse>, t: Throwable) {
                           println("Error: ${t.message}")
                       }
                   })
               },
               modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically).fillMaxWidth()
           ) {
               Text("Get data", modifier = Modifier.padding(8.dp).align(Alignment.CenterVertically))
           }
       }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            TextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("Author") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = pages,
                onValueChange = { pages = it },
                label = { Text("Pages") },
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            TextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("Genre") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = datePublished,
            onValueChange = { datePublished = it },
            label = { Text("Date Published") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = coverUrl,
            onValueChange = { coverUrl = it },
            label = { Text("Cover URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                val pagesInt = pages.toIntOrNull() ?: 0
                // set dateAdded to current date in  format dd.MM.yyyy
                val currentDateTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val dateAdded = currentDateTime.format(formatter)
                val newBook = Book(
                    isbn = isbn,
                    title = title,
                    author = author,
                    datePublished = datePublished,
                    pages = pagesInt,
                    genre = genre,
                    rating = 0,
                    description = description,
                    publisher = "",
                    language = "",
                    coverUrl = coverUrl,
                    currentPages = 0,
                    readingStatus = ReadingStatus.NOT_STARTED.name,
                    dateStarted = "",
                    dateFinished = "",
                    dateAdded = dateAdded,
                    ownershipStatus = ""
                )
                viewModel.addBook(newBook)
                navController.navigate(MainNavOption.BooksScreen.name )

            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add Book")
        }
    }
}