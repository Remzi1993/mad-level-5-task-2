package info.remzi.madlevel5_task2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.remzi.madlevel5_task2.data.Resource
import info.remzi.madlevel5_task2.data.local.toDomain
import info.remzi.madlevel5_task2.data.model.Movie
import info.remzi.madlevel5_task2.repository.MoviesRepository
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val repository: MoviesRepository
) : ViewModel() {

    private val _moviesResource = MutableLiveData<Resource<List<Movie>>>(Resource.Empty())
    val moviesResource: LiveData<Resource<List<Movie>>> = _moviesResource

    private val _selectedMovie = MutableLiveData<Movie?>()
    val selectedMovie: LiveData<Movie?> = _selectedMovie

    private val _favoriteMovies = MutableLiveData<List<Movie>>(emptyList())
    val favoriteMovies: LiveData<List<Movie>> = _favoriteMovies

    init {
        viewModelScope.launch {
            repository.getFavoriteMovies().collect { entities ->
                _favoriteMovies.value = entities.map { it.toDomain() }
            }
        }
    }

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
        viewModelScope.launch {
            repository.toggleFavorite(movie)
        }
    }

    fun isFavorite(movie: Movie): Boolean {
        return _favoriteMovies.value?.any { it.id == movie.id } == true
    }
}