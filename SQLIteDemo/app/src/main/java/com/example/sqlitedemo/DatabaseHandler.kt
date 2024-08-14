package com.example.sqlitedemo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Database Information
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "EmployeeDatabase"
        private const val TABLE_CONTACTS = "EmployeeTable"

        // Column Names
        private const val KEY_ID = "_id"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop older table if it exists
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")

        // Create tables again
        onCreate(db)
    }

    fun addEmployee(emp: EmpModelClass): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()

        //contentValues.put(KEY_ID, emp.id) //-------------It is primary Key it will be automatically incremented
        contentValues.put(KEY_NAME, emp.name)
        contentValues.put(KEY_EMAIL, emp.email)


        // Inserting Row
        val success = db.insert(TABLE_CONTACTS, null, contentValues)

        // Closing database connection
        db.close()

        return success
    }


    fun viewEmployee(): ArrayList<EmpModelClass> {
        val empList = ArrayList<EmpModelClass>()
        val selectQuery = "SELECT * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase

        try {
            val cursor: Cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    val employee = EmpModelClass(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                        email = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL))
                    )
                    empList.add(employee)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        return empList
    }


    fun updateEmployee(emp: EmpModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            //put(KEY_ID, empModelClass.id)
            put(KEY_NAME, emp.name)
            put(KEY_EMAIL, emp.email)
        }

        // Update record with the given ID
        //val result = db.update(TABLE_CONTACTS, contentValues, "$KEY_ID = ${emp.id}", arrayOf(emp.id.toString()))
        val success = db.update(TABLE_CONTACTS, contentValues, KEY_ID + "=" + emp.id, null)

        db.close()
        return success
    }

    fun deleteEmployee(emp: EmpModelClass): Int {
        val db = this.writableDatabase

//        val contentValues = ContentValues()
//        contentValues.put(KEY_ID, emp.id)

        // Delete record with the given ID
        val success = db.delete(TABLE_CONTACTS, KEY_ID + "=" + emp.id, null)

        db.close()
        return success
    }



}