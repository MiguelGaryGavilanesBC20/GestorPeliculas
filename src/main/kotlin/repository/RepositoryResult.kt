package repository

sealed class RepositoryResult<out T> {
    data class Success<out T>(val data: T) : RepositoryResult<T>()
    data class Error(val exception: Exception, val message: String? = null) : RepositoryResult<Nothing>()
}