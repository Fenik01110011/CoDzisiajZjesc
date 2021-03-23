package com.example.codzisiajzjesc

import android.graphics.Color
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.food_item.view.*

//klasa odpowiadajaca za przewijanie listy
class FoodAdapter (var currentFoodItems: MutableList<Food>) :
    RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    //klasa bedaca uchwytem do tworzenia przewijalnej listy
    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        //dostosowanie listy, aby na pewno byla typu MutableList
        currentFoodItems = currentFoodItems.toMutableList()

        //zwrocenie uchwytu do tworzenia nowych elementow wedlug wzoru z food_item.xml
        return FoodViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.food_item,
                parent,
                false
            )
        )
    }

    //funkcja dodajaca nowy element do pamieci adaptera
    fun addFood(food: Food) {
        currentFoodItems.add(food) //dodanie nowego elementu do listy adaptera
        notifyItemInserted(currentFoodItems.size - 1) //poinformowanie adaptera o dodaniu nowego elementu do listy
    }

    //funkcja aktualizujaca dane adaptera po zmianach w liscie dan
    fun updateDataAfterDeleteFoods(newList: MutableList<Food>) {
        currentFoodItems = newList //podmienienie aktualnej listy na nowa
        notifyDataSetChanged() //poinformowanie adaptera o zmianie listy
    }

    //funkcja odpowiadajaca za przekreslanie i odkreslanie dania odpowiednio do tego czy jest zaznaczone
    private fun toggleStrikeThrough(textViewFoodName: TextView, isChecked: Boolean) {
        if(isChecked) {
            textViewFoodName.paintFlags = textViewFoodName.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            textViewFoodName.paintFlags = textViewFoodName.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    //funkcja odpowiada za przewijanie listy wyswietlajac nowe elementy w miejsce starych
    //oraz usuwajac z pamieci niewyswietlane dane
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        //pobranie elementu ktory powinien sie teraz wyswietlic
        val currentFoodItem = currentFoodItems[currentFoodItems.size - (position + 1)]

        //przygotowanie szablonu widoku dania do wyswietlenia i dodanie go do widoku przewijalnej listy
        holder.itemView.apply {
            //ustawienie elementow szablonu zgodnie z danymi dania
            textViewFoodName.text = currentFoodItem.name
            checkBoxDelete.isChecked = currentFoodItem.isChecked
            checkBoxBreakfast.isChecked = currentFoodItem.breakfast
            checkBoxDinner.isChecked = currentFoodItem.dinner
            checkBoxSupper.isChecked = currentFoodItem.supper

            //przekreslenie dania jesli bylo zaznaczone
            toggleStrikeThrough(textViewFoodName, currentFoodItem.isChecked)

            //nasluchiwanie zmian zaznaczenia CheckBoxa odpowiedzialnego za dania do usuniecia
            //nasluchiwane jest zdarzenie OnClick, a nie OnCheckedChange,
            //poniewaz podczas przewijania listy OnCheckedChange uruchamial sie mimo,
            //ze CheckBox nie zostal wcisniety, co powodowalo niechciane zmiany w bazie danych
            checkBoxDelete.setOnClickListener {
                //przekreslenie dania jesli zostalo zaznaczone i odkreslenie jesli zostalo odznaczone
                toggleStrikeThrough(textViewFoodName, checkBoxDelete.isChecked)

                //zapisanie zmiany zaznaczenia elementu
                currentFoodItem.isChecked = checkBoxDelete.isChecked
            }

            //nasłuchiwanie CheckBoxa odpowiadajacego za danie na sniadanie
            checkBoxBreakfast.setOnClickListener {
                currentFoodItem.breakfast = checkBoxBreakfast.isChecked //zmiana flagi odpowiadajacej za sniadania
                DatabaseHandler(this.context).updateFood(currentFoodItem) //wprowadzenie zmian do bazy danych
            }

            //nasłuchiwanie CheckBoxa odpowiadajacego za danie na obiad
            checkBoxDinner.setOnClickListener {
                currentFoodItem.dinner = checkBoxDinner.isChecked
                DatabaseHandler(this.context).updateFood(currentFoodItem)
            }

            //nasłuchiwanie CheckBoxa odpowiadajacego za danie na kolacje
            checkBoxSupper.setOnClickListener {
                currentFoodItem.supper = checkBoxSupper.isChecked
                DatabaseHandler(this.context).updateFood(currentFoodItem)
            }

            //ustawianie naprzemiennie kolorow elementow listy
            if(position % 2 == 1)
                dishCointainer.setBackgroundColor(Color.rgb(230, 230, 255)) //ustawienie koloru
            else
                dishCointainer.setBackgroundColor(Color.rgb(255, 255, 255))
        }
    }

    //funkcja zwracajaca calkowita ilosc elementow listy
    override fun getItemCount(): Int {
        //ustawienie ilosci elementow przewijalnej listy zgodnie z dlugoscia listy do wyswietlenia
        return currentFoodItems.size
    }
}