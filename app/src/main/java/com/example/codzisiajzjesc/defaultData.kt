package com.example.codzisiajzjesc

//klasa przechowujaca domyslne dane programu
class DefaultData {
    //funkcja zwracajaca domyslna liste dan
    fun basicFoodsList(): List<Food> {
        return listOf(
                //tworzenie obiektow klasy Food
                Food(1, "Pizza", breakfast = false, dinner = true, supper = false),
                Food(2, "Sushi", breakfast = true, dinner = true, supper = true),
                Food(3, "Spagetti", breakfast = false, dinner = true, supper = false),
                Food(4, "Szpinak", breakfast = false, dinner = true, supper = false),
                Food(5, "Kebab", breakfast = false, dinner = true, supper = false),
                Food(6, "Zapiekanka", breakfast = false, dinner = true, supper = true),
                Food(7, "Kanapka z serem", breakfast = true, dinner = false, supper = true),
                Food(8, "Jajecznica", breakfast = true, dinner = true, supper = true),
                Food(9, "Burger", breakfast = false, dinner = true, supper = false),
                Food(10, "Naleśniki", breakfast = true, dinner = false, supper = true),
                Food(11, "Krokiety", breakfast = false, dinner = true, supper = true),
                Food(12, "Tosty", breakfast = true, dinner = false, supper = true),
                Food(13, "Płatki z mlekiem", breakfast = true, dinner = false, supper = true),
                Food(14, "Zupa", breakfast = false, dinner = true, supper = true),
                Food(15, "Gofry", breakfast = true, dinner = false, supper = true)
        )
    }
}