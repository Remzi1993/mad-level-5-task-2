package info.remzi.madlevel5_task2.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import info.remzi.madlevel5_task2.data.model.Movie
import info.remzi.madlevel5_task2.viewmodel.MoviesViewModel

@Composable
fun FavoritesScreen(
    viewModel: MoviesViewModel,
    onMovieClick: (Movie) -> Unit
) {
    val favorites = viewModel.favoriteMovies.observeAsState(emptyList()).value

    if (favorites.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No favorites yet.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    } else {
        MovieList(
            movies = favorites,
            viewModel = viewModel,
            onMovieClick = onMovieClick
        )
    }
}