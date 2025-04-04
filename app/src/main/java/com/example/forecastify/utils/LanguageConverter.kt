package com.example.forecastify.utils

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.text.NumberFormat
import java.util.Locale

object LanguageConverter {
    fun changeLanguage(context: Context, languageCode: String) {
        //version >= 13
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(languageCode)
        } else {
            //version < 13
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        }
    }

    fun formatNumber(value: Int): String {
        val formatter = NumberFormat.getInstance(Locale.getDefault())
        return formatter.format(value)
    }
}