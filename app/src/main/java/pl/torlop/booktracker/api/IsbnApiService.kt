package pl.torlop.booktracker.api

import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.GET
import retrofit2.http.Query

// interface for Open Library API to get book information by ISBN


interface IsbnApiService {
    @Headers(
        "Accept: application/json"
    )
    @GET("isbn/{isbn}.json")
    fun getBookByIsbn(@Path("isbn") isbn: String): Call<OpenLibraryISBNResponseClass>
    @Headers(
        "Accept: application/json"
    )
    @GET("volumes")
    fun getBookByIsbnGoogleApi(@Query("q") isbn: String): Call<GoogleBookApiResponse>
}