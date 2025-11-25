package info.remzi.madlevel5_task2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import info.remzi.madlevel5_task2.R
import info.remzi.madlevel5_task2.viewmodel.MoviesViewModel
import androidx.compose.ui.res.stringResource

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

@Composable
fun MovieDetailScreen(
    viewModel: MoviesViewModel,
    onBack: () -> Unit
) {
    val movie = viewModel.selectedMovie.observeAsState().value
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = movie?.title.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(end = 16.dp)
            )
        }

        if (movie == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.error_text),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            return
        }

        val backdropUrl = movie.backdropPath?.let { IMAGE_BASE_URL + it }
        val posterUrl = movie.posterPath?.let { IMAGE_BASE_URL + it }

        if (backdropUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(backdropUrl)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = "Backdrop",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            if (posterUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(posterUrl)
                        .crossfade(true)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = "Poster",
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.title.orEmpty(),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.details_release_date,
                        movie.releaseDate.orEmpty()
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.details_rating,
                        movie.voteAverage ?: 0f
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.details_overview),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = movie.overview.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}