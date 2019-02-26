package ua.varandas.trianglecalculator.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

const val DATABASE_NAME = "TriangleDatabase"
const val VERSION = 1
const val TABLE_NAME_TRIANGLE = "Triangle"
const val ID = "id"
const val COL_A = "A"
const val COL_B = "B"
const val COL_C = "C"

class MyDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, DATABASE_NAME, null, VERSION) {
    companion object {
        private var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MyDatabaseOpenHelper {
            if (instance == null) {
                instance = MyDatabaseOpenHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable(TABLE_NAME_TRIANGLE, true,
                ID to INTEGER + PRIMARY_KEY + UNIQUE,
                COL_A to REAL,
                COL_B to REAL,
                COL_C to REAL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        //db.dropTable("User", true)
    }

}

// Access property for Context
val Context.database: MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstance(applicationContext)