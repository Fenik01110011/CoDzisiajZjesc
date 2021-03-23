package com.example.codzisiajzjesc

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dishes.*

class Dishes : AppCompatActivity() {
    //zmienna przechowujaca adapter odpowiedzialny za dzialanie przewijania listy dan
    private lateinit var foodAdapter: FoodAdapter

    //zmienne przechowujace aktualne ustawienia wyszukiwania
    private var breakfastData = false
    private var dinnerData = false
    private var supperData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dishes)
        //dodanie strzalki pozwalajacej cofnac sie do poczatkowego ekranu aplikacji
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //stworzenie obiektu adaptera odpowiedzialnego za przewijanie listy, zawierajacego wszystkie zapisane dania
        foodAdapter = FoodAdapter(DatabaseHandler(this).viewFoods().toMutableList())

        //inicjalizacja przewijalnej listy
        RecycleViewFoodItems.adapter = foodAdapter
        RecycleViewFoodItems.layoutManager = LinearLayoutManager(this)

        //dodanie dania po wcisnieciu przycisku
        buttonAddFood.setOnClickListener {
            addRecord()
        }

        //usuniecie zaznaczonych dan po wcisnieciu przycisku
        buttonDeleteCheckedFoods.setOnClickListener {
            removeRecords(foodAdapter.currentFoodItems)
        }

        //ustawienie wyszukiwania sniadan
        breakfastButtonDishes.setOnClickListener {
            foodAdapter = FoodAdapter(searchSetting(breakfast = true, dinner = false, supper = false))
            RecycleViewFoodItems.adapter = foodAdapter //odswierzenie widoku przewijanej listy
        }

        //ustawienie wyszukiwania obiadow
        dinnerButtonDishes.setOnClickListener {
            foodAdapter = FoodAdapter(searchSetting(breakfast = false, dinner = true, supper = false))
            RecycleViewFoodItems.adapter = foodAdapter
        }

        //ustawienie wyszukiwania kolacji
        supperButtonDishes.setOnClickListener {
            foodAdapter = FoodAdapter(searchSetting(breakfast = false, dinner = false, supper = true))
            RecycleViewFoodItems.adapter = foodAdapter
        }
    }

    //funkcja ustawiajaca preferencje wyszukiwania uzytkownika, zgodnie z podanymi wartosciami
    private fun searchSetting(breakfast: Boolean, dinner: Boolean, supper: Boolean) : MutableList<Food> {
        //pobranie ustawionych glownych kolorow
        val activeButtonColor = ResourcesCompat.getColor(resources, R.color.purple_700, null)
        val inactiveButtonColor = ResourcesCompat.getColor(resources, R.color.purple_500, null)

        //zmiana koloru wszystkich przyciskow na domyslne
        breakfastButtonDishes.setBackgroundColor(inactiveButtonColor)
        dinnerButtonDishes.setBackgroundColor(inactiveButtonColor)
        supperButtonDishes.setBackgroundColor(inactiveButtonColor)

        //pobranie aktualnej listy wszystkich zapisanych dan
        var newFoodsList = DatabaseHandler(this).viewFoods().toMutableList()

        //warunek pozwalajacy odznaczyc obecnie wybrany przycisk
        if(!breakfastData && breakfast){
            breakfastData = true
            breakfastButtonDishes.setBackgroundColor(activeButtonColor)
            //zmniejszenie listy zgodnie z zaznaczona opcja wyszukiwania
            newFoodsList = newFoodsList.filter { it.breakfast }.toMutableList()
        }
        else
            breakfastData = false

        if(!dinnerData && dinner){
            dinnerData = true
            dinnerButtonDishes.setBackgroundColor(activeButtonColor)
            newFoodsList = newFoodsList.filter { it.dinner }.toMutableList()
        }
        else
            dinnerData = false

        if(!supperData && supper){
            supperData = true
            supperButtonDishes.setBackgroundColor(activeButtonColor)
            newFoodsList = newFoodsList.filter { it.supper }.toMutableList()
        }
        else
            supperData = false

        return newFoodsList //zwrocenie przefiltrowanej listy
    }

    //funkcja zapisujaca danie w bazie danych
    private fun addRecord() {
        //pobranie wpisanej nazwy dania oraz zaznaczonych opcji
        val name = editTextFoodName.text.toString()
        val breakfast = checkBoxBreakfastAdd.isChecked
        val dinner = checkBoxDinnerAdd.isChecked
        val supper = checkBoxSupperAdd.isChecked

        //pobranie uchwytu do obslugi bazy danych
        val databaseHandler = DatabaseHandler(this)
        if (name.isNotEmpty()) { //sprawdzenie czy uzytwkonik wpisal cos w miejce nazwy
            //dodanie dania do bazy danych i pobranie przypisanego mu Id
            val rowId = databaseHandler.addFood(Food(0, name, breakfast, dinner, supper))

            if (rowId > -1) { //jesli zapisanie nowego dania przebieglo pomyslnie
                //dodanie dania takze do pamieci adaptera
                foodAdapter.addFood(Food(rowId.toInt(), name, breakfast, dinner, supper))
                RecycleViewFoodItems.adapter = foodAdapter //aktualizacja wyswietlanej listy dan

                //informacja dla uzytkownika odnosnie pomyslnego dodania dania
                Toast.makeText(applicationContext, "Danie zostało zapisane", Toast.LENGTH_LONG).show()
                editTextFoodName.text.clear() //usuniecie wczesniej wpisanej nazwy dania
            } else
                Toast.makeText(applicationContext, "Błąd połącznia z bazą danych", Toast.LENGTH_LONG).show()
        } else
            Toast.makeText(applicationContext, "Wpisz nazwę potrawy, aby ją dodać", Toast.LENGTH_LONG).show()
    }

    //funkcja usuwajaca zaznaczone dania
    private fun removeRecords(foodList: MutableList<Food>){
        //sprawdzenie czy uzytkownik zaznaczyl przynajmniej jedno danie
        if(foodList.find{it.isChecked} != null) {

            //rownoczesne usuniecie dan z bazy danych oraz z otrzymanej listy
            if (foodList.removeAll { food -> DatabaseHandler(this).deleteFood(food, food.isChecked).toBoolean() }) {
                //aktualizacja listy dan adaptera
                foodAdapter.updateDataAfterDeleteFoods(foodList)
                RecycleViewFoodItems.adapter = foodAdapter //odswierzenie widoku przewijalnej listy dan

                //informacja dla uzytkownika
                Toast.makeText(this, "Zaznaczone dania zostały usunięte", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Nie można było usunąć zaznaczonych dań", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Zaznacz dania, które chcesz usunąć", Toast.LENGTH_SHORT).show()
        }
    }
}