package com.example.meeting_organizer.data.database.user

import com.example.meeting_organizer.data.model.User

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUser(email: String, password: String): User? {
        return userDao.getUser(email, password)
    }

    suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)
    }
}