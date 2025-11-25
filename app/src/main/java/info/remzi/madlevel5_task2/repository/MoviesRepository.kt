package info.remzi.madlevel5_task2.repository

import info.remzi.madlevel5_task2.data.Resource
import info.remzi.madlevel5_task2.data.api.MovieApi
import info.remzi.madlevel5_task2.data.local.FavoriteMovieDao
import info.remzi.madlevel5_task2.data.local.FavoriteMovieEntity
import info.remzi.madlevel5_task2.data.model.Movie
import kotlinx.coroutines.flow.Flow

class MoviesRepository(
    private val favoriteMovieDao: FavoriteMovieDao
) {
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

    fun getFavoriteMovies(): Flow<List<FavoriteMovieEntity>> {
        return favoriteMovieDao.getFavoriteMovies()
    }

    suspend fun toggleFavorite(movie: Movie) {
        val existing = favoriteMovieDao.getById(movie.id)
        if (existing != null) {
            favoriteMovieDao.delete(existing)
        } else {
            favoriteMovieDao.insert(movie.toEntity())
        }
    }
}

private fun Movie.toEntity(): FavoriteMovieEntity {
    return FavoriteMovieEntity(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage
    )
}

fun FavoriteMovieEntity.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage
    )
}