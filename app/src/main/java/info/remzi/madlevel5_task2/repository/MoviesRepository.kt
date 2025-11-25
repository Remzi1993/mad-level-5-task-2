package info.remzi.madlevel5_task2.repository

import info.remzi.madlevel5_task2.data.Resource
import info.remzi.madlevel5_task2.data.api.MovieApi
import info.remzi.madlevel5_task2.data.model.Movie

class MoviesRepository {

    private val api = MovieApi.service

    suspend fun searchMovies(query: String): Resource<List<Movie>> {
        return try {
            val trimmed = query.trim()
            if (trimmed.isEmpty()) {
                Resource.Empty()
            } else {
                val response = api.searchMovies(trimmed)
                Resource.Success(response.results)
            }
        } catch (e: Exception) {
            Resource.Error("Failed to load movies", e)
        }
    }
}