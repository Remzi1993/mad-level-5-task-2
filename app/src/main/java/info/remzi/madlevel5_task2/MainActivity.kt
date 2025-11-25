package info.remzi.madlevel5_task2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import info.remzi.madlevel5_task2.data.local.AppDatabase
import info.remzi.madlevel5_task2.repository.MoviesRepository
import info.remzi.madlevel5_task2.ui.navigation.MovieScreens
import info.remzi.madlevel5_task2.ui.screens.FavoritesScreen
import info.remzi.madlevel5_task2.ui.screens.MovieDetailScreen
import info.remzi.madlevel5_task2.ui.screens.MovieOverviewScreen
import info.remzi.madlevel5_task2.ui.theme.MADLevel5Task2Theme
import info.remzi.madlevel5_task2.viewmodel.MoviesViewModel
import info.remzi.madlevel5_task2.viewmodel.MoviesViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MADLevel5Task2Theme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val context = LocalContext.current
                    val db = remember { AppDatabase.getInstance(context) }
                    val repository = remember { MoviesRepository(db.favoriteMovieDao()) }
                    val moviesViewModel: MoviesViewModel = viewModel(
                        factory = MoviesViewModelFactory(repository)
                    )

                    val navController = rememberNavController()
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = backStackEntry?.destination?.route

                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == MovieScreens.Overview.route,
                                    onClick = {
                                        navController.navigate(MovieScreens.Overview.route) {
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
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text("Search") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == MovieScreens.Favorites.route,
                                    onClick = {
                                        navController.navigate(MovieScreens.Favorites.route) {
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
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text("Favorites") }
                                )
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = MovieScreens.Overview.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(MovieScreens.Overview.route) {
                                MovieOverviewScreen(
                                    viewModel = moviesViewModel,
                                    onMovieClick = { movie ->
                                        moviesViewModel.selectMovie(movie)
                                        navController.navigate(MovieScreens.Detail.route)
                                    }
                                )
                            }
                            composable(MovieScreens.Favorites.route) {
                                FavoritesScreen(
                                    viewModel = moviesViewModel,
                                    onMovieClick = { movie ->
                                        moviesViewModel.selectMovie(movie)
                                        navController.navigate(MovieScreens.Detail.route)
                                    }
                                )
                            }
                            composable(MovieScreens.Detail.route) {
                                MovieDetailScreen(
                                    viewModel = moviesViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}