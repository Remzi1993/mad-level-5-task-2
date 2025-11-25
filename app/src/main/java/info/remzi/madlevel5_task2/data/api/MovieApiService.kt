package info.remzi.madlevel5_task2.data.api

import info.remzi.madlevel5_task2.data.model.MovieSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String
    ): MovieSearchResponse
}