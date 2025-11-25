package info.remzi.madlevel5_task2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.remzi.madlevel5_task2.data.Resource
import info.remzi.madlevel5_task2.data.model.Movie
import info.remzi.madlevel5_task2.repository.MoviesRepository
import kotlinx.coroutines.launch

class MoviesViewModel : ViewModel() {

    private val repository = MoviesRepository()

    private val _moviesResource = MutableLiveData<Resource<List<Movie>>>(Resource.Empty())
    val moviesResource: LiveData<Resource<List<Movie>>> = _moviesResource

    private val _selectedMovie = MutableLiveData<Movie?>()
    val selectedMovie: LiveData<Movie?> = _selectedMovie

    // simple in-memory favorites (no Firestore to keep it simple)
    private val favorites = mutableSetOf<Int>()
    private val _showFavorites = MutableLiveData(false)
    val showFavorites: LiveData<Boolean> = _showFavorites

    fun searchMovies(query: String) {
        _moviesResource.value = Resource.Loading()
        viewModelScope.launch {
            _moviesResource.value = repository.searchMovies(query)
        }
    }

    fun selectMovie(movie: Movie) {
        _selectedMovie.value = movie
    }

    fun toggleFavorite(movie: Movie) {
        if (favorites.contains(movie.id)) {
            favorites.remove(movie.id)
        } else {
            favorites.add(movie.id)
        }
        filterFavoritesIfNeeded()
    }

    fun toggleShowFavorites() {
        _showFavorites.value = !(_showFavorites.value ?: false)
        filterFavoritesIfNeeded()
    }

    fun isFavorite(movie: Movie): Boolean = favorites.contains(movie.id)

    private fun filterFavoritesIfNeeded() {
        val current = _moviesResource.value
        val showFav = _showFavorites.value ?: false
        if (current is Resource.Success) {
            _moviesResource.value = if (showFav) {
                Resource.Success(current.data.filter { favorites.contains(it.id) })
            } else {
                current
            }
        }
    }
}