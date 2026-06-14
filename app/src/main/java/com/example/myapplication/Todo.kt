package com.example.myapplication

data class Todo(
    val id: Int,
    val title: String,
    var isDone: Boolean = false
)
