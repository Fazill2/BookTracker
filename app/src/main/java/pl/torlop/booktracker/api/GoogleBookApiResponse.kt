package pl.torlop.booktracker.api

data class GoogleBookApiResponse(
    val kind: String?,
    val totalItems: Int?,
    val items: List<Item>?
)

data class Item(
    val kind: String?,
    val id: String?,
    val etag: String?,
    val selfLink: String?,
    val volumeInfo: VolumeInfo?,
    val saleInfo: SaleInfo?,
    val accessInfo: AccessInfo?,
)

data class VolumeInfo(
    val title: String?,
    val authors: List<String>?,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    val industryIdentifiers: List<IndustryIdentifier>?,
    val readingModes: ReadingModes?,
    val pageCount: Int?,
    val printType: String?,
    val categories: List<String>?,
    val averageRating: Double?,
    val ratingsCount: Int?,
    val maturityRating: String?,
    val allowAnonLogging: Boolean?,
    val contentVersion: String?,
    val panelizationSummary: PanelizationSummary?,
    val language: String?,
    val previewLink: String?,
    val infoLink: String?,
    val canonicalVolumeLink: String?
)

data class IndustryIdentifier(
    val type: String?,
    val identifier: String?
)

data class ReadingModes(
    val text: Boolean?,
    val image: Boolean?
)

data class PanelizationSummary(
    val containsEpubBubbles: Boolean?,
    val containsImageBubbles: Boolean?
)


data class SaleInfo(
    val country: String?,
    val saleability: String?,
    val isEbook: Boolean?
)

data class AccessInfo(
    val country: String?,
    val viewability: String?,
    val embeddable: Boolean?,
    val publicDomain: Boolean?,
    val textToSpeechPermission: String?,
    val epub: Epub?,
    val pdf: Pdf?,
    val webReaderLink: String?,
    val accessViewStatus: String?,
    val quoteSharingAllowed: Boolean?
)

data class Epub(
    val isAvailable: Boolean?
)

data class Pdf(
    val isAvailable: Boolean?
)