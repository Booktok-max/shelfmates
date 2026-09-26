package com.shelfmates.data.remote

import com.shelfmates.data.model.GoogleBookVolumeItem
import com.shelfmates.data.model.GoogleBooksResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksApi {

    @GET("volumes")
    suspend fun searchVolumes(
        @Query("q") query: String,
        @Query("startIndex") startIndex: Int = 0,
        @Query("maxResults") maxResults: Int = 20,
        @Query("orderBy") orderBy: String = "relevance",
        @Query("printType") printType: String = "books",
        @Query("filter") filter: String? = null,
        @Query("key") apiKey: String? = null
    ): GoogleBooksResponse

    @GET("volumes/{volumeId}")
    suspend fun getVolumeById(
        @Path("volumeId") volumeId: String,
        @Query("key") apiKey: String? = null
    ): GoogleBookVolumeItem
}
