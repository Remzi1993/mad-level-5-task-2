package info.remzi.madlevel5_task2.repository

import info.remzi.madlevel5_task2.data.Resource
import info.remzi.madlevel5_task2.data.api.MovieApi
import info.remzi.madlevel5_task2.data.local.MovieDao
import info.remzi.madlevel5_task2.data.local.MovieEntity
import info.remzi.madlevel5_task2.data.local.toEntity
import info.remzi.madlevel5_task2.data.model.Movie
import kotlinx.coroutines.flow.Flow

class MoviesRepository(
    private val movieDao: MovieDao
) {
    private val api = MovieApi.service

    fun favoriteMovies(): Flow<List<MovieEntity>> {
        return movieDao.getFavoriteMovies()
    }

    suspend fun searchMovies(query: String): Resource<List<Movie>> {
        return try {
            val trimmed = query.trim()
            if (trimmed.isEmpty()) {
                Resource.Empty()
            } else {
                val response = api.searchMovies(trimmed)
                val movies = response.results

                // Cache results locally too, while preserving favorites
                val entities = movies.map { movie ->
                    val existing = movieDao.getById(movie.id)
                    movie.toEntity(isFavorite = existing?.isFavorite ?: false)
                }
                movieDao.insertAll(entities)

                Resource.Success(movies)
            }
        } catch (e: Exception) {
            Resource.Error("Failed to load movies", e)
        }
    }

    suspend fun toggleFavorite(movie: Movie) {
        val existing = movieDao.getById(movie.id)
        if (existing != null) {
            movieDao.insert(existing.copy(isFavorite = !existing.isFavorite))
        } else {
            movieDao.insert(movie.toEntity(isFavorite = true))
        }
    }
}