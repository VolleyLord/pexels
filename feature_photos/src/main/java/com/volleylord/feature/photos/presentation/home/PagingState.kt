package com.volleylord.feature.photos.presentation.home

sealed class PagingState {
    data object Loading : PagingState()
    data class Error(val throwable: Throwable) : PagingState()
    data object Empty : PagingState()
    data object Data : PagingState()
}


