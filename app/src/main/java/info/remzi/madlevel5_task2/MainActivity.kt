package info.remzi.madlevel5_task2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import info.remzi.madlevel5_task2.data.local.AppDatabase
import info.remzi.madlevel5_task2.repository.MoviesRepository
import info.remzi.madlevel5_task2.ui.navigation.Screens
import info.remzi.madlevel5_task2.ui.screens.FavoritesScreen
import info.remzi.madlevel5_task2.ui.screens.MovieDetailScreen
import info.remzi.madlevel5_task2.ui.screens.MovieOverviewScreen
import info.remzi.madlevel5_task2.ui.theme.MADLevel5Task2Theme
import info.remzi.madlevel5_task2.viewmodel.MoviesViewModel
import info.remzi.madlevel5_task2.viewmodel.MoviesViewModelFactory

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MADLevel5Task2Theme {
                val context = androidx.compose.ui.platform.LocalContext.current
                val db = androidx.compose.runtime.remember { AppDatabase.getInstance(context) }
                val repository = androidx.compose.runtime.remember { MoviesRepository(db.movieDao()) }
                val moviesViewModel: MoviesViewModel = viewModel(
                    factory = MoviesViewModelFactory(repository)
                )

                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                val selectedMovie by moviesViewModel.selectedMovie.observeAsState()

                Surface(color = MaterialTheme.colorScheme.background) {
                    androidx.compose.material3.Scaffold(
                        topBar = {
                            when (currentRoute) {
                                Screens.Favorites.route -> {
                                    TopAppBar(
                                        title = { Text(stringResource(R.string.favorites)) },
                                        colors = TopAppBarDefaults.topAppBarColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }
                                Screens.Detail.route -> {
                                    val isFavorite = selectedMovie
                                        ?.let { moviesViewModel.isFavorite(it) }
                                        ?: false

                                    TopAppBar(
                                        title = {
                                            Text(
                                                selectedMovie?.title.orEmpty(),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        navigationIcon = {
                                            IconButton(onClick = { navController.popBackStack() }) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = "Back"
                                                )
                                            }
                                        },
                                        actions = {
                                            IconButton(
                                                onClick = {
                                                    selectedMovie?.let { moviesViewModel.toggleFavorite(it) }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                                    contentDescription = "Toggle favorite"
                                                )
                                            }
                                        },
                                        colors = TopAppBarDefaults.topAppBarColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }
                                else -> {
                                    // Overview: no top app bar, search bar lives in the screen itself
                                }
                            }
                        },
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == Screens.Overview.route || currentRoute == null,
                                    onClick = {
                                        navController.navigate(Screens.Overview.route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search movies"
                                        )
                                    },
                                    label = { Text("Search") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screens.Favorites.route,
                                    onClick = {
                                        navController.navigate(Screens.Favorites.route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = "Favorites"
                                        )
                                    },
                                    label = { Text("Favorites") }
                                )
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screens.Overview.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Screens.Overview.route) {
                                MovieOverviewScreen(
                                    viewModel = moviesViewModel,
                                    onMovieClick = { movie ->
                                        moviesViewModel.selectMovie(movie)
                                        navController.navigate(Screens.Detail.route)
                                    }
                                )
                            }
                            composable(Screens.Favorites.route) {
                                FavoritesScreen(
                                    viewModel = moviesViewModel,
                                    onMovieClick = { movie ->
                                        moviesViewModel.selectMovie(movie)
                                        navController.navigate(Screens.Detail.route)
                                    }
                                )
                            }
                            composable(Screens.Detail.route) {
                                MovieDetailScreen(
                                    viewModel = moviesViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}