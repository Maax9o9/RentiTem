package com.rentitem.core.storage

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("rentitem_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "user_token"
        private const val USER_UID = "user_uid"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(USER_TOKEN, token).apply()
    }

    fun saveUid(uid: String) {
        prefs.edit().putString(USER_UID, uid).apply()
    }

    fun getUid(): String? {
        return prefs.getString(USER_UID, null)
    }

    fun getToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun clearSession() {
        prefs.edit().remove(USER_TOKEN).remove(USER_UID).apply()
    }
}