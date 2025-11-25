package info.remzi.madlevel5_task2.ui.navigation

sealed class Screens(val route: String) {
    data object Overview : Screens("overview")
    data object Detail : Screens("detail")
    data object Favorites : Screens("favorites")
}