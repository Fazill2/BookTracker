package pl.torlop.booktracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.torlop.booktracker.api.IsbnApiService
import pl.torlop.booktracker.api.OpenLibraryISBNResponseClass

class IsbnApiViewModel(private val isbnApiService: IsbnApiService) : ViewModel() {
        private val _book = MutableLiveData<OpenLibraryISBNResponseClass>()
        val book: LiveData<OpenLibraryISBNResponseClass> = _book


}

