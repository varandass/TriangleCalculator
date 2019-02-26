package ua.varandas.trianglecalculator.firebase

import android.content.Context
import android.content.SharedPreferences


class PreferencesManager(mContext: Context) {

    companion object {
        private var mSPref: SharedPreferences? = null
    }

    init {
        mSPref = mContext.getSharedPreferences(ConstantHolder.APP_PREF, Context.MODE_PRIVATE)
    }

    // получаем значение состояния рекламы из SharedPreferences
    var isAdsDisabled: Boolean
        get() = mSPref!!.getBoolean(ConstantHolder.APP_PREF_DISABLE_ADS, false)
        set(value) = mSPref!!.edit().putBoolean(ConstantHolder.APP_PREF_DISABLE_ADS, value).apply()

    // получаем дату в миллисекундах, когда нужно включить рекламу
    var estimatedAdsTime: Long
        get() = mSPref!!.getLong(ConstantHolder.APP_DISABLE_ADS_PERIOD, 0)
        set(value) = mSPref!!.edit().putLong(ConstantHolder.APP_DISABLE_ADS_PERIOD, value).apply()
}