package com.example.codzisiajzjesc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.*

//deklaracja nowej klasy MainActivity rodzaju AppCompatActivity
class MainActivity : AppCompatActivity() {
    //zmienne przechowujace aktualne ustawienia wyszukiwania
    private var breakfastData = false
    private var dinnerData = false
    private var supperData = false

    override fun onCreate(savedInstanceState: Bundle?) { //nadpisanie funkcji onCreate z pobraniem informacji o obecnej instancji
        super.onCreate(savedInstanceState) //wywolanie funkcji onCreate z klasy z ktorej dziedziczy ta klasa
        setContentView(R.layout.activity_main) //ustawinie widoku na activity_main.xml

        //dodanie do bazy danych domyslnych dan, jesli jest to pierwsze uruchomienie aplikacji po instalacji
        DatabaseHandler(this).addBasicFoodsListToNewCreatedTable()

        //ustawienie wyszukiwania śniadań
        breakfastButton.setOnClickListener {
            setRandomFoodSettings(breakfast = true, dinner = false, supper = false) //funkcja wprowadzajaca ustawienia
        }

        //ustawienie wyszukiwania obiadów
        dinnerButton.setOnClickListener {
            setRandomFoodSettings(breakfast = false, dinner = true, supper = false)
        }

        //ustawienie wyszukiwania kolacji
        supperButton.setOnClickListener {
            setRandomFoodSettings(breakfast = false, dinner = false, supper = true)
        }

        //po wcisnieciu losuje danie zgodnie z ustawionym wyszukiwaniem
        ButtonRandomFood.setOnClickListener {
            textViewWhatEatToday.visibility = View.INVISIBLE //ukrycie napisu zachecajacego

            var selectedFoodList = selectedFoodList()
            if(selectedFoodList.size > 0){ //sprawdzenie czy otrzymana lista nie jest pusta
                textViewRandomFood.text = selectedFoodList().random().name //losowanie dania i podmiana tekstu do wyswietlenia
            } else {
                Toast.makeText(this, "Dodaj dania, aby móc je wylosować", Toast.LENGTH_SHORT).show() //informacja dla uzytkownika
            }

            textViewRandomFood.visibility = View.VISIBLE //wyswietlenie wylosowanego dania
        }

        //po kliknieciu przechodzi do listy wszystkich dan
        allDishesButton.setOnClickListener {
            startActivity(Intent(this, Dishes::class.java))
        }
    }

    //funkcja ustawiajaca preferencje wyszukiwania uzytkownika, zgodnie z podanymi wartosciami
    private fun setRandomFoodSettings (breakfast: Boolean, dinner: Boolean, supper: Boolean) {
        //pobranie ustawionych glownych kolorow
        val activeButtonColor = ResourcesCompat.getColor(resources, R.color.purple_700, null)
        val inactiveButtonColor = ResourcesCompat.getColor(resources, R.color.purple_500, null)

        //zmiana koloru wszystkich przyciskow na domyslne
        breakfastButton.setBackgroundColor(inactiveButtonColor)
        dinnerButton.setBackgroundColor(inactiveButtonColor)
        supperButton.setBackgroundColor(inactiveButtonColor)

        //warunek pozwalajacy odznaczyc obecnie wybrany przycisk
        if(!breakfastData && breakfast){
            breakfastData = true
            breakfastButton.setBackgroundColor(activeButtonColor)
        }
        else
            breakfastData = false

        if(!dinnerData && dinner){
            dinnerData = true
            dinnerButton.setBackgroundColor(activeButtonColor)
        }
        else
            dinnerData = false

        if(!supperData && supper){
            supperData = true
            supperButton.setBackgroundColor(activeButtonColor)
        }
        else
            supperData = false
    }

    //funckaj zwraca liste dan zgodnie z ustawionymi preferencjami
    private fun selectedFoodList () : MutableList<Food> {
        //pobranie aktualnej listy wszystkich zapisanych dan
        var newFoodsList = DatabaseHandler(this).viewFoods().toMutableList()

        //zmniejszenie listy zgodnie z zaznaczona opcja wyszukiwania
        if(breakfastData)
            newFoodsList = newFoodsList.filter { it.breakfast }.toMutableList()

        if(dinnerData)
            newFoodsList = newFoodsList.filter { it.dinner }.toMutableList()

        if(supperData)
            newFoodsList = newFoodsList.filter { it.supper }.toMutableList()

        return newFoodsList //zwrocenie przefiltrowanej listy
    }
}

//pomocnicze funkcje rozszerzajace podstawowe klasy danych o potrzebne funkcjonalnosci
fun Boolean.toInt() = if (this) 1 else 0
fun Int.toBoolean() = this != 0