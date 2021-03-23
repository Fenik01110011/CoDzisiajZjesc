package com.example.codzisiajzjesc

//klasa sluzaca do tworzenia obiektow przechowujacych informacje o daniu
data class Food(
    var id: Int,
    val name: String,
    var breakfast: Boolean = false,
    var dinner: Boolean = false,
    var supper: Boolean = false,
    var isChecked: Boolean = false
)