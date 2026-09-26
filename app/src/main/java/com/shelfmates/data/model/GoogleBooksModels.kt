package com.shelfmates.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleBooksResponse(
    @Json(name = "kind") val kind: String? = null,
    @Json(name = "totalItems") val totalItems: Int = 0,
    @Json(name = "items") val items: List<GoogleBookVolumeItem>? = null
)

@JsonClass(generateAdapter = true)
data class GoogleBookVolumeItem(
    @Json(name = "id") val id: String,
    @Json(name = "kind") val kind: String? = null,
    @Json(name = "etag") val etag: String? = null,
    @Json(name = "selfLink") val selfLink: String? = null,
    @Json(name = "volumeInfo") val volumeInfo: VolumeInfo? = null,
    @Json(name = "saleInfo") val saleInfo: SaleInfo? = null,
    @Json(name = "accessInfo") val accessInfo: AccessInfo? = null
) {
    val displayTitle: String
        get() = volumeInfo?.title ?: "Untitled Book"

    val displayAuthors: String
        get() = volumeInfo?.authors?.joinToString(", ") ?: "Unknown Author"

    val primaryAuthor: String
        get() = volumeInfo?.authors?.firstOrNull() ?: "Unknown Author"

    val displayDescription: String
        get() = volumeInfo?.description ?: "No description available for this edition."

    val secureCoverUrl: String
        get() {
            val link = volumeInfo?.imageLinks?.bestCoverUrl
            return if (!link.isNullOrBlank()) {
                if (link.startsWith("http://")) {
                    link.replaceFirst("http://", "https://")
                } else {
                    link
                }
            } else {
                "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=600&auto=format&fit=crop&q=80"
            }
        }

    val displayCategory: String
        get() = volumeInfo?.categories?.firstOrNull()?.split("/")?.firstOrNull()?.trim() ?: "General Fiction"

    val pageCountText: String
        get() = if ((volumeInfo?.pageCount ?: 0) > 0) "${volumeInfo?.pageCount} pages" else "Pages unspecified"

    val publishedYear: String
        get() = volumeInfo?.publishedDate?.take(4) ?: "Unknown"

    val isbn13: String
        get() = volumeInfo?.industryIdentifiers?.firstOrNull { it.type == "ISBN_13" }?.identifier
            ?: volumeInfo?.industryIdentifiers?.firstOrNull { it.type == "ISBN_10" }?.identifier
            ?: ""

    val webReaderUrl: String?
        get() = accessInfo?.webReaderLink ?: volumeInfo?.previewLink
}

@JsonClass(generateAdapter = true)
data class VolumeInfo(
    @Json(name = "title") val title: String? = null,
    @Json(name = "subtitle") val subtitle: String? = null,
    @Json(name = "authors") val authors: List<String>? = null,
    @Json(name = "publisher") val publisher: String? = null,
    @Json(name = "publishedDate") val publishedDate: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "industryIdentifiers") val industryIdentifiers: List<IndustryIdentifier>? = null,
    @Json(name = "pageCount") val pageCount: Int? = null,
    @Json(name = "printType") val printType: String? = null,
    @Json(name = "categories") val categories: List<String>? = null,
    @Json(name = "averageRating") val averageRating: Double? = null,
    @Json(name = "ratingsCount") val ratingsCount: Int? = null,
    @Json(name = "maturityRating") val maturityRating: String? = null,
    @Json(name = "imageLinks") val imageLinks: ImageLinks? = null,
    @Json(name = "language") val language: String? = null,
    @Json(name = "previewLink") val previewLink: String? = null,
    @Json(name = "infoLink") val infoLink: String? = null,
    @Json(name = "canonicalVolumeLink") val canonicalVolumeLink: String? = null
)

@JsonClass(generateAdapter = true)
data class IndustryIdentifier(
    @Json(name = "type") val type: String? = null,
    @Json(name = "identifier") val identifier: String? = null
)

@JsonClass(generateAdapter = true)
data class ImageLinks(
    @Json(name = "smallThumbnail") val smallThumbnail: String? = null,
    @Json(name = "thumbnail") val thumbnail: String? = null,
    @Json(name = "small") val small: String? = null,
    @Json(name = "medium") val medium: String? = null,
    @Json(name = "large") val large: String? = null,
    @Json(name = "extraLarge") val extraLarge: String? = null
) {
    val bestCoverUrl: String?
        get() = extraLarge ?: large ?: medium ?: small ?: thumbnail ?: smallThumbnail
}

@JsonClass(generateAdapter = true)
data class SaleInfo(
    @Json(name = "country") val country: String? = null,
    @Json(name = "saleability") val saleability: String? = null,
    @Json(name = "isEbook") val isEbook: Boolean? = null,
    @Json(name = "buyLink") val buyLink: String? = null
)

@JsonClass(generateAdapter = true)
data class AccessInfo(
    @Json(name = "country") val country: String? = null,
    @Json(name = "viewability") val viewability: String? = null,
    @Json(name = "embeddable") val embeddable: Boolean? = null,
    @Json(name = "publicDomain") val publicDomain: Boolean? = null,
    @Json(name = "webReaderLink") val webReaderLink: String? = null,
    @Json(name = "accessViewStatus") val accessViewStatus: String? = null
)
