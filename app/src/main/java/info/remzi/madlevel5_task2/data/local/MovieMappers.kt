package info.remzi.madlevel5_task2.data.local

import info.remzi.madlevel5_task2.data.model.Movie

fun MovieEntity.toDomain(): Movie {
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

fun Movie.toEntity(isFavorite: Boolean = false): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        isFavorite = isFavorite
    )
}