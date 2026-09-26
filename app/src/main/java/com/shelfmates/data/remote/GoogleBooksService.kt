package com.shelfmates.data.remote

import android.util.Log
import com.shelfmates.BuildConfig
import com.shelfmates.data.model.GoogleBookVolumeItem
import com.shelfmates.data.model.GoogleBooksResponse
import com.shelfmates.data.model.ImageLinks
import com.shelfmates.data.model.IndustryIdentifier
import com.shelfmates.data.model.VolumeInfo
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Google Books API service.
 *
 * HTTP 400 fix: The original code sent X-Android-Package and X-Android-Cert headers
 * with a hardcoded SHA-1. When the API key in Google Cloud is restricted to a specific
 * package + SHA-1 fingerprint the key itself is the restriction — those headers are not
 * needed for server calls and caused 400 Bad Request. Removed.
 *
 * If you want to use an Android-restricted key (recommended for production), generate the
 * SHA-1 via `./gradlew signingReport` and add it in Google Cloud Console →
 * Credentials → your key → Application restrictions → Android apps.
 * For now, use an unrestricted key or restrict by IP only.
 */
class GoogleBooksService {

    companion object {
        private const val TAG = "GoogleBooksService"
        private const val BASE_URL = "https://www.googleapis.com/books/v1/"

        @Volatile private var instance: GoogleBooksService? = null

        fun getInstance(): GoogleBooksService =
            instance ?: synchronized(this) {
                instance ?: GoogleBooksService().also { instance = it }
            }
    }

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val apiKey = BuildConfig.GOOGLE_BOOKS_KEY.trim()

            // Only append key if not already present — no Android restriction headers
            val newUrl = if (apiKey.isNotEmpty() &&
                original.url.queryParameter("key") == null) {
                original.url.newBuilder()
                    .addQueryParameter("key", apiKey)
                    .build()
            } else {
                original.url
            }

            chain.proceed(original.newBuilder().url(newUrl).build())
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api: GoogleBooksApi = retrofit.create(GoogleBooksApi::class.java)

    /** Search books by free text — title, author, ISBN, genre. */
    suspend fun searchBooks(
        query: String,
        maxResults: Int = 20,
        startIndex: Int = 0,
        orderBy: String = "relevance"
    ): Result<List<GoogleBookVolumeItem>> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext Result.success(emptyList())
        try {
            val response: GoogleBooksResponse = api.searchVolumes(
                query = query.trim(),
                startIndex = startIndex,
                maxResults = maxResults.coerceIn(1, 40),
                orderBy = orderBy
            )
            val items = response.items ?: emptyList()
            Log.d(TAG, "Google Books: ${items.size} results for '$query'")
            Result.success(items)
        } catch (e: Exception) {
            Log.w(TAG, "Google Books search failed: ${e.message}.")
            if (BuildConfig.DEBUG) {
                val fallback = getCuratedFallbackBooks().filter { book ->
                    book.displayTitle.contains(query, ignoreCase = true) ||
                            book.displayAuthors.contains(query, ignoreCase = true) ||
                            book.displayCategory.contains(query, ignoreCase = true)
                }
                if (fallback.isNotEmpty()) Result.success(fallback) else Result.failure(e)
            } else {
                Result.failure(e)
            }
        }
    }

    /** Search by genre / subject category. */
    suspend fun searchByCategory(category: String, maxResults: Int = 20): Result<List<GoogleBookVolumeItem>> {
        val q = if (category.equals("All", ignoreCase = true)) "fiction bestselling"
        else "subject:\"${category.lowercase()}\""
        return searchBooks(query = q, maxResults = maxResults)
    }

    /** Look up a single book by ISBN. */
    suspend fun searchByIsbn(isbn: String): Result<GoogleBookVolumeItem?> = withContext(Dispatchers.IO) {
        try {
            val clean = isbn.replace("-", "").trim()
            val response = api.searchVolumes(query = "isbn:$clean", maxResults = 1)
            Result.success(response.items?.firstOrNull())
        } catch (e: Exception) {
            Log.e(TAG, "ISBN lookup failed for $isbn: ${e.message}")
            Result.failure(e)
        }
    }

    /** Fetch full volume by Google Books ID. */
    suspend fun getVolumeById(volumeId: String): Result<GoogleBookVolumeItem> = withContext(Dispatchers.IO) {
        try {
            Result.success(api.getVolumeById(volumeId))
        } catch (e: Exception) {
            Log.e(TAG, "getVolumeById failed for $volumeId: ${e.message}")
            if (BuildConfig.DEBUG) {
                getCuratedFallbackBooks().find { it.id == volumeId }
                    ?.let { Result.success(it) }
                    ?: Result.failure(e)
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * Fetch a cover thumbnail URL for a given ISBN via the Google Books search endpoint.
     * Used to back-fill NYT bestseller entries that have no cover URL.
     */
    suspend fun fetchCoverByIsbn(isbn: String): String? = withContext(Dispatchers.IO) {
        try {
            val clean = isbn.replace("-", "").trim()
            val response = api.searchVolumes(query = "isbn:$clean", maxResults = 1)
            response.items?.firstOrNull()?.volumeInfo?.imageLinks?.thumbnail
                ?.replace("http://", "https://")
        } catch (e: Exception) {
            Log.w(TAG, "Cover fetch failed for ISBN $isbn: ${e.message}")
            null
        }
    }

    /** Offline/fallback curated catalogue. */
    fun getCuratedFallbackBooks(): List<GoogleBookVolumeItem> = listOf(
        GoogleBookVolumeItem(
            id = "gb_dune",
            volumeInfo = VolumeInfo(
                title = "Dune", subtitle = "Deluxe Edition",
                authors = listOf("Frank Herbert"), publisher = "Ace Books",
                publishedDate = "1965-08-01",
                description = "Set on the desert planet Arrakis, Dune is the story of the boy Paul Atreides, heir to a noble family tasked with ruling an inhospitable world.",
                pageCount = 688, categories = listOf("Science Fiction / Space Opera"),
                averageRating = 4.8, ratingsCount = 14500,
                imageLinks = ImageLinks(thumbnail = "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=600&auto=format&fit=crop&q=80"),
                industryIdentifiers = listOf(IndustryIdentifier("ISBN_13", "9780441013593"))
            )
        ),
        GoogleBookVolumeItem(
            id = "gb_fourth_wing",
            volumeInfo = VolumeInfo(
                title = "Fourth Wing", subtitle = "The Empyrean Book 1",
                authors = listOf("Rebecca Yarros"), publisher = "Red Tower Books",
                publishedDate = "2023-05-02",
                description = "Twenty-year-old Violet Sorrengail was supposed to enter the Scribe Quadrant — until the commanding general ordered her to join the dragon riders.",
                pageCount = 528, categories = listOf("Fantasy / Romance"),
                averageRating = 4.9, ratingsCount = 28900,
                imageLinks = ImageLinks(thumbnail = "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=600&auto=format&fit=crop&q=80"),
                industryIdentifiers = listOf(IndustryIdentifier("ISBN_13", "9781649374042"))
            )
        ),
        GoogleBookVolumeItem(
            id = "gb_silent_patient",
            volumeInfo = VolumeInfo(
                title = "The Silent Patient", subtitle = "A Psychological Thriller",
                authors = listOf("Alex Michaelides"), publisher = "Celadon Books",
                publishedDate = "2019-02-05",
                description = "Alicia Berenson shoots her husband five times in the face — and never speaks another word.",
                pageCount = 336, categories = listOf("Psychological Thriller"),
                averageRating = 4.6, ratingsCount = 19400,
                imageLinks = ImageLinks(thumbnail = "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=600&auto=format&fit=crop&q=80"),
                industryIdentifiers = listOf(IndustryIdentifier("ISBN_13", "9781250301696"))
            )
        ),
        GoogleBookVolumeItem(
            id = "gb_atomic_habits",
            volumeInfo = VolumeInfo(
                title = "Atomic Habits",
                subtitle = "An Easy & Proven Way to Build Good Habits & Break Bad Ones",
                authors = listOf("James Clear"), publisher = "Avery",
                publishedDate = "2018-10-16",
                description = "A proven framework for improving every day. James Clear reveals practical strategies that teach you how to form good habits and break bad ones.",
                pageCount = 320, categories = listOf("Self-Help / Personal Growth"),
                averageRating = 4.9, ratingsCount = 45000,
                imageLinks = ImageLinks(thumbnail = "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=600&auto=format&fit=crop&q=80"),
                industryIdentifiers = listOf(IndustryIdentifier("ISBN_13", "9780735211292"))
            )
        )
    )
}