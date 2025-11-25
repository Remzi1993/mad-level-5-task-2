package info.remzi.madlevel5_task2.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import info.remzi.madlevel5_task2.R
import info.remzi.madlevel5_task2.data.Resource
import info.remzi.madlevel5_task2.data.model.Movie
import info.remzi.madlevel5_task2.viewmodel.MoviesViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

@Composable
fun MovieOverviewScreen(
    viewModel: MoviesViewModel,
    onMovieClick: (Movie) -> Unit
) {
    val moviesResource by viewModel.moviesResource.observeAsState(Resource.Empty())
    val showFavorites by viewModel.showFavorites.observeAsState(false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchView(
                modifier = Modifier.weight(1f),
                searchTMDB = { query -> viewModel.searchMovies(query) }
            )

            IconButton(onClick = { viewModel.toggleShowFavorites() }) {
                Icon(
                    imageVector = if (showFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Toggle favorites",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (moviesResource) {
            is Resource.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = stringResource(R.string.initial_text),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.loading_text),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.error_text),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            is Resource.Success -> {
                val movies = (moviesResource as Resource.Success<List<Movie>>).data
                MovieList(
                    movies = movies,
                    viewModel = viewModel,
                    onMovieClick = onMovieClick
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    searchTMDB: (String) -> Unit
) {
    val searchQueryState = rememberSaveable(stateSaver = androidx.compose.ui.text.input.TextFieldValue.Saver) {
        mutableStateOf(androidx.compose.ui.text.input.TextFieldValue(""))
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = searchQueryState.value,
        onValueChange = { value ->
            searchQueryState.value = value
        },
        modifier = modifier,
        textStyle = TextStyle(fontSize = 18.sp),
        leadingIcon = {
            if (searchQueryState.value.text.isNotEmpty()) {
                IconButton(
                    onClick = {
                        searchQueryState.value = androidx.compose.ui.text.input.TextFieldValue("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove search argument"
                    )
                }
            }
        },
        trailingIcon = {
            IconButton(onClick = {
                val query = searchQueryState.value.text
                searchTMDB(query)
                keyboardController?.hide()
            }) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search for movies in TMDB based on search argument provided"
                )
            }
        },
        placeholder = {
            Text(
                text = stringResource(R.string.search_movie_hint)
            )
        },
        singleLine = true,
        shape = RectangleShape,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                val query = searchQueryState.value.text
                searchTMDB(query)
                keyboardController?.hide()
            }
        ),
        colors = TextFieldDefaults.colors()
    )
}

@Composable
private fun MovieList(
    movies: List<Movie>,
    viewModel: MoviesViewModel,
    onMovieClick: (Movie) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(movies) { movie ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMovieClick(movie) }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val posterUrl = movie.posterPath?.let { IMAGE_BASE_URL + it }

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
                        maxLines = 3,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                IconButton(onClick = { viewModel.toggleFavorite(movie) }) {
                    val isFavorite = viewModel.isFavorite(movie)
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle favorite",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}