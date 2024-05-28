package com.example.meeting_organizer.data.database.meeting

import androidx.room.TypeConverter
import com.example.meeting_organizer.data.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListConverter {
    private val gson = Gson()

    @TypeConverter
    fun userListToString(userList: List<User>): String {
        return gson.toJson(userList)
    }

    @TypeConverter
    fun stringToUserList(data: String): List<User> {
        val listType = object : TypeToken<List<User>>() {}.type
        return gson.fromJson(data, listType)
    }
}
