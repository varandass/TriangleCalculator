package ua.varandas.trianglecalculator.ext

import android.widget.CheckBox
import android.widget.EditText
import ua.varandas.trianglecalculator.MyAppClass
import ua.varandas.trianglecalculator.firebase.PreferencesManager

val prefs: PreferencesManager by lazy { MyAppClass.sPref!! }

fun EditText.notEmptyToFloat() = if (text.isEmpty()) 0f else text.toString().toFloat()

fun Float.positive() = this > 0

fun CheckBox.checkEditText(editText: EditText) {
    setOnCheckedChangeListener { buttonView, isChecked ->
        editText.isEnabled = isChecked
        if (!editText.isEnabled) editText.setText("")
    }
}