package com.example.codzisiajzjesc

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.util.Log.d

//klasa do tworzenia obiektow odpowiedzialnych za polaczenie z baza danych
class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    //obiekt przechowujacy ustawienia bazy danych
    companion object {
        //wersja bazy danych (powinna byc zwiekszana przy kazdej zmianie struktury dazy danych)
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "FoodsDatabase"

        private val TABLE_FOODS = "FoodsTable"

        private val KEY_ID = "_id"
        private val KEY_NAME = "name"
        private val KEY_BREAKFAST = "breakfast"
        private val KEY_DINNER = "dinner"
        private val KEY_SUPPER = "supper"

        private var tableCreatedFlag = false
    }

    //funkcja uruchamiana podczas tworzenia obiektu do obslugi bazy danych
    override fun onCreate(db: SQLiteDatabase?) {
        //stworzenie zapytania tworzacego nowa tabele w bazie danych na podstawie obecnych ustawien
        val CREATE_FOODS_TABLE = ("CREATE TABLE " + TABLE_FOODS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_BREAKFAST + " INTEGER," + KEY_DINNER + " INTEGER,"
                + KEY_SUPPER + " INTEGER" + ")")
        db?.execSQL(CREATE_FOODS_TABLE) //wykonanie wczesniej utworzonego zapytania

        //zmiana flagi utworzenia nowej tabeli, odpowiadajacej za dodania poczatkowych danych do tabeli
        tableCreatedFlag = true
    }

    //funckaj wywolywana jesli zostala zmieniona wersja tabeli
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_FOODS")
        onCreate(db)
    }

    //funkcja dodajaca danie do bazy danych
    fun addFood(food: Food): Long {
        //utworznie polaczenia z baza danych i pobranie uchwytu do niego
        val db = this.writableDatabase //polaczenie do zapisu danych

        //przygotowanie specjalnego pojemnika przechowyjacego dane do dodania do tabeli
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, food.name) //dodanie wartosci do pojemnika
        contentValues.put(KEY_BREAKFAST, food.breakfast.toInt())
        contentValues.put(KEY_DINNER, food.dinner.toInt())
        contentValues.put(KEY_SUPPER, food.supper.toInt())

        //dodanie rekordu z daniem do tabeli w bazie danych
        val success = db.insert(TABLE_FOODS, null, contentValues)

        db.close() //zamkniecie polaczenia z baza
        return success
    }

    //funkcja zwracajaca liste wszystkich dan znajdujacych sie w bazie danych
    fun viewFoods(): ArrayList<Food> {

        //deklaracja pustej tablizy na dane
        val foodsList: ArrayList<Food> = ArrayList<Food>()

        //utworzenie zmiennej przechowujacej zapytanie pobierajace wszystkie rekordy z tabeli z daniami
        val selectQuery = "SELECT  * FROM $TABLE_FOODS"

        //utworznie polaczenia z baza danych i pobranie uchwytu do niego
        val db = this.readableDatabase //polaczenie do odczytu danych

        //kursor sluzacy do odczytywania rekordow jeden po drugim
        var cursor: Cursor? = null

        try {
            //utworzenie kursora przechowujacego wszystkie pobrane rekordy
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery) //wykonanie zapytania
            return ArrayList() //zwrocenie pustej tablicy
        }

        //deklaracja zmiennych sluzacych do przechowania pobranych danych z wiersza tabeli
        var id: Int
        var name: String
        var breakfast: Boolean
        var dinner: Boolean
        var supper: Boolean

        //jesli pobrano przynajmniej jeden rekord z tabeli
        if (cursor.moveToFirst()) {
            do {
                //pobranie danych dania
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                breakfast = cursor.getInt(cursor.getColumnIndex(KEY_BREAKFAST)).toBoolean()
                dinner = cursor.getInt(cursor.getColumnIndex(KEY_DINNER)).toBoolean()
                supper = cursor.getInt(cursor.getColumnIndex(KEY_SUPPER)).toBoolean()

                //dodanie dania do listy
                foodsList.add(Food(id, name, breakfast, dinner, supper))

            } while (cursor.moveToNext()) //przejscie do kolejnego pobranego rekordu, jesli istnieje
        }

        return foodsList //zwrocenie przygotowanej listy wszystkich dan z bazy danych
    }

    //funckaj sluzaca do edycji danych okreslonego dania
    fun updateFood(food: Food): Int {
        //utworznie polaczenia z baza danych i pobranie uchwytu do niego
        val db = this.writableDatabase //polaczenie do zapisu danych

        //przygotowanie specjalnego pojemnika przechowyjacego dane do dodania do tabeli
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, food.name)
        contentValues.put(KEY_BREAKFAST, food.breakfast.toInt())
        contentValues.put(KEY_DINNER, food.dinner.toInt())
        contentValues.put(KEY_SUPPER, food.supper.toInt())

        //aktualizacja danych dania o okreslonym id
        val success = db.update(TABLE_FOODS, contentValues, KEY_ID + "=" + food.id, null)

        db.close() //zamkniecie polaczenia z baza
        return success //zwrocenie informacji czy edycja danego rekordu przebiegla pomyslnie
    }

    //funkcja sluzaca do usuwania dania z bazy
    fun deleteFood(food: Food, delete: Boolean = true): Int {
        //pominiecie usuniecia danego dania zgodnie z ustawiona flaga;
        //to ustawienie jest pomocne przy usuwaniu danych rownoczesnie z bazy danych i z listy
        //przy pomocy funkcji lista.removeAll{}
        if(!delete)
            return 0

        //utworznie polaczenia z baza danych i pobranie uchwytu do niego
        val db = this.writableDatabase //polaczenie do zapisu danych

        //usuniecie z bazy danych dania o okreslonym id
        val success = db.delete(TABLE_FOODS, KEY_ID + "=" + food.id, null)

        db.close() //zamkniecie polaczenia z baza
        return success //zwrocenie informacji czy usuniecie danego rekordu przebieglo pomyslnie
    }

    //funkcja dodajaca do bazy danych domyslnych dan, jesli jest to pierwsze uruchomienie aplikacji po instalacji
    fun addBasicFoodsListToNewCreatedTable () {
        //nawiazanie polaczenia z baza, aby uruchomic funckje onCreate, jesli nie zostala utworzona jeszcze tabela
        this.readableDatabase
        //jesli jest to pierwsze uruchomienie aplikacji po instalacji
        if(tableCreatedFlag){
            DefaultData().basicFoodsList().forEach { food -> addFood(food) } //dodanie rekordow do bazy danych
            tableCreatedFlag = false
        }
    }
}