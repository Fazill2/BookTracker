package pl.torlop.booktracker.api



data class OpenLibraryISBNResponseClass(
    val isbn_10: List<String>?,
    val isbn_13: List<String>?,
    val title: String?,
    val authors: List<Author>?,
    val number_of_pages: String?,
    val publish_date: String?,
    val publishers: List<String>?,
    val subjects: List<String>?,
    val languages: List<ApiLanguage>?
)

data class Author(
    val key: String?
)

data class ApiLanguage(
    val key: String?
)