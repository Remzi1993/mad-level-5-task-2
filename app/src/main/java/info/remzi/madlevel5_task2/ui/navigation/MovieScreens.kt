package info.remzi.madlevel5_task2.ui.navigation

sealed class MovieScreens(val route: String) {
    data object Overview : MovieScreens("overview")
    data object Detail : MovieScreens("detail")
    data object Favorites : MovieScreens("favorites")
}