package com.example.animatedvisibilty

data class ImageItem(
    val id: Int,
    val imageRes: Int,
    var isEntryVisible: Boolean = false
)