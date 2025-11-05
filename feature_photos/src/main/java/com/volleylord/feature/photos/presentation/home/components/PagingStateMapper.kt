package com.volleylord.feature.photos.presentation.home.components

import com.volleylord.feature.photos.presentation.home.PagingState

fun toPagingTag(state: PagingState): String = when (state) {
    is PagingState.Error -> "Error"
    is PagingState.Empty -> "Empty"
    is PagingState.Data -> "Data"
    is PagingState.Loading -> "Loading"
}


