package ua.varandas.trianglecalculator

import android.app.Application
import android.content.Context
import ua.varandas.trianglecalculator.firebase.PreferencesManager



class MyAppClass : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: MyAppClass? = null
        var sPref :PreferencesManager? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        // initialize for any

        // Use ApplicationContext.
        // example: SharedPreferences etc...
        val context: Context = applicationContext()
        sPref = PreferencesManager(context)
    }
}