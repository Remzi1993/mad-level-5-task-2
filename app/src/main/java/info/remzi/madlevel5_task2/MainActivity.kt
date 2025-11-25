package info.remzi.madlevel5_task2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import info.remzi.madlevel5_task2.ui.navigation.MovieScreens
import info.remzi.madlevel5_task2.ui.screens.MovieDetailScreen
import info.remzi.madlevel5_task2.ui.screens.MovieOverviewScreen
import info.remzi.madlevel5_task2.ui.theme.MADLevel5Task2Theme
import info.remzi.madlevel5_task2.viewmodel.MoviesViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MADLevel5Task2Theme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val moviesViewModel: MoviesViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = MovieScreens.Overview.route
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