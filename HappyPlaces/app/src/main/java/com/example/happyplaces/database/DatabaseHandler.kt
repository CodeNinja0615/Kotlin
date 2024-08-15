package com.example.happyplaces.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.happyplaces.models.HappyPlaceModel

class DatabaseHandler(context:Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Database Information
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "HappyPlacesDatabase"
        private const val TABLE_HAPPY_PLACE = "HappyPlacesTable"

        // Column Names
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_HAPPY_PLACE_TABLE = ("CREATE TABLE " + TABLE_HAPPY_PLACE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT" + ")")
        db?.execSQL(CREATE_HAPPY_PLACE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop older table if it exists
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_HAPPY_PLACE")

        // Create tables again
        onCreate(db)
    }

    fun addHappyPlace(happyPlace: HappyPlaceModel): Long {
        val db = this.writableDatabase

//        val contentValues = ContentValues().apply {
//            put(KEY_TITLE, happyPlace.title)
//            put(KEY_IMAGE, happyPlace.image)
//            put(KEY_DESCRIPTION, happyPlace.description)
//            put(KEY_DATE, happyPlace.date)
//            put(KEY_LOCATION, happyPlace.location)
//            put(KEY_LATITUDE, happyPlace.latitude)
//            put(KEY_LONGITUDE, happyPlace.longitude)
//        }
        val contentValues = ContentValues()

        contentValues.put(KEY_TITLE, happyPlace.title)
        contentValues.put(KEY_IMAGE, happyPlace.image)
        contentValues.put(KEY_DESCRIPTION, happyPlace.description)
        contentValues.put(KEY_DATE, happyPlace.date)
        contentValues.put(KEY_LOCATION, happyPlace.location)
        contentValues.put(KEY_LATITUDE, happyPlace.latitude)
        contentValues.put(KEY_LONGITUDE, happyPlace.longitude)


        // Inserting Row
        val result = db.insert(TABLE_HAPPY_PLACE, null, contentValues)

        // Closing database connection
        db.close()

        return result
    }


    fun getHappyPlaceList(): ArrayList<HappyPlaceModel> {
        val happyPlaceList = ArrayList<HappyPlaceModel>()
        val selectQuery = "SELECT * FROM $TABLE_HAPPY_PLACE"
        val db = this.readableDatabase

        try {
            val cursor: Cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    val happyPlace = HappyPlaceModel(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        title = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)),
                        image = cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)),
                        description = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                        date = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                        location = cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOCATION)),
                        latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LATITUDE)),
                        longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LONGITUDE))
                    )
                    happyPlaceList.add(happyPlace)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        return happyPlaceList
    }


    fun updateHappyPlace(happyPlace: HappyPlaceModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_TITLE, happyPlace.title)
            put(KEY_IMAGE, happyPlace.image)
            put(KEY_DESCRIPTION, happyPlace.description)
            put(KEY_DATE, happyPlace.date)
            put(KEY_LOCATION, happyPlace.location)
            put(KEY_LATITUDE, happyPlace.latitude)
            put(KEY_LONGITUDE, happyPlace.longitude)
        }

        // Update record with the given ID

//        val result = db.update(TABLE_HAPPY_PLACE, contentValues, "$KEY_ID = ?", arrayOf(happyPlace.id.toString()))
        val success = db.update(TABLE_HAPPY_PLACE, contentValues, KEY_ID + "=" +  happyPlace.id , null)

        db.close()
        return success
    }

    fun deleteHappyPlace(happyPlace: HappyPlaceModel): Int {
        val db = this.writableDatabase

        // Delete record with the given ID
        val success = db.delete(TABLE_HAPPY_PLACE, KEY_ID + "=" +  happyPlace.id , null)

        db.close()
        return success
    }



}