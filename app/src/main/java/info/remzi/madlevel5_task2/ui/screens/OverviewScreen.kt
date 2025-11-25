package info.remzi.madlevel5_task2.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import info.remzi.madlevel5_task2.R
import info.remzi.madlevel5_task2.data.Resource
import info.remzi.madlevel5_task2.data.model.Movie
import info.remzi.madlevel5_task2.viewmodel.MoviesViewModel

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

@Composable
fun MovieOverviewScreen(
    viewModel: MoviesViewModel,
    onMovieClick: (Movie) -> Unit
) {
    val moviesResource by viewModel.moviesResource.observeAsState(Resource.Empty())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        SearchBarMovies(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            onSearch = { query -> viewModel.searchMovies(query) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (moviesResource) {
            is Resource.Empty -> CenterMessage(stringResource(R.string.initial_text))
            is Resource.Loading -> CenterMessage(stringResource(R.string.loading_text))
            is Resource.Error -> CenterMessage(stringResource(R.string.error_text))
            is Resource.Success -> {
                val movies = (moviesResource as Resource.Success<List<Movie>>).data
                MovieList(
                    movies = movies,
                    onMovieClick = onMovieClick
                )
            }
        }
    }
}

@Composable
private fun CenterMessage(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarMovies(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit
) {
    val textState: TextFieldState = rememberTextFieldState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // We intentionally never expand this into the full-screen suggestions mode.
    val expanded = false

    SearchBar(
        modifier = modifier,
        inputField = {
            SearchBarDefaults.InputField(
                query = textState.text.toString(),
                onQueryChange = { newText ->
                    textState.edit { replace(0, length, newText) }
                },
                onSearch = {
                    val q = textState.text.toString().trim()
                    if (q.isNotEmpty()) {
                        onSearch(q)
                    }
                    keyboardController?.hide()
                },
                expanded = expanded,
                onExpandedChange = { /* no-op: always collapsed */ },
                placeholder = { Text(stringResource(R.string.search_movie_hint)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (textState.text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                textState.edit { replace(0, length, "") }
                                keyboardController?.hide()
                            }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        },
        expanded = expanded,
        onExpandedChange = {},
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {}
}

@Composable
fun MovieList(
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(movies) { movie ->
            MovieCard(movie = movie, onClick = { onMovieClick(movie) })
        }
    }
}

@Composable
fun MovieCard(
    movie: Movie,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val posterUrl = movie.posterPath?.let { IMAGE_BASE_URL + it }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            if (posterUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(posterUrl)
                        .crossfade(true)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = movie.title,
                    modifier = Modifier
                        .width(80.dp)
                        .height(120.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.title.orEmpty(),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.overview.orEmpty(),
                    maxLines = 4,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}