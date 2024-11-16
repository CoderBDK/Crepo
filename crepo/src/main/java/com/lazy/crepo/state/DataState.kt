package com.lazy.crepo.state

sealed class DataState<out T> {
    data object Loading : DataState<Nothing>()
    data class Success<out R>(val data: R) : DataState<R>()
    data class Error(val exception: Exception) : DataState<Nothing>()
}