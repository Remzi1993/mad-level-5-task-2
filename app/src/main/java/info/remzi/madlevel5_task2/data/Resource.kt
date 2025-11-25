package info.remzi.madlevel5_task2.data

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
    data class Loading<out T>(val data: T? = null) : Resource<T>()
    class Empty<out T> : Resource<T>()
}