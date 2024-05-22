package com.example.meeting_organizer.util

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.meeting_organizer.data.database.user.UserRepository
import com.example.meeting_organizer.data.model.User
import com.example.meeting_organizer.ui.login.LoginScreen
import com.example.meeting_organizer.ui.main.MainScreen
import com.example.meeting_organizer.ui.register.RegisterScreen
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavigationController(userRepository: UserRepository, lifecycleOwner: LifecycleOwner) {
    val navController = rememberNavController()

    val registerUser: (String, String, String, String, String) -> Unit = { firstName, lastName, email, phoneNumber, password ->
        lifecycleOwner.lifecycleScope.launch {
            userRepository.insertUser(User(
                firstName = firstName,
                lastName = lastName,
                email = email,
                phoneNumber = phoneNumber,
                password = password
            ))
            withContext(Dispatchers.Main) {
                navController.navigate("login")
            }
        }
    }

    val loginUser: (String, String) -> Unit = { email, password ->
        lifecycleOwner.lifecycleScope.launch {
            val user = userRepository.getUser(email, password)
            if (user != null && user.email == email && user.password == password) {
                withContext(Dispatchers.Main) {
                    navController.navigate("main/${user.id}")
                }
            } else {
                // Obsługa błędnych danych logowania
            }
        }
    }

    val scheduleMeeting: (String, String, LatLng) -> Unit = { title, time, location ->
        // Logika planowania spotkania (np. zapis do bazy danych)
        // Wysłanie powiadomienia
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = {
            NavHost(navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(
                        onLoginClick = loginUser,
                        onRegisterClick = { navController.navigate("register") }
                    )
                }
                composable("register") {
                    RegisterScreen(
                        onRegisterClick = registerUser
                    )
                }
                composable("main/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")
                    if (userId != null) {
                        val userIdInt = userId.toInt()
                        var user by remember { mutableStateOf<User?>(null) }

                        LaunchedEffect(userIdInt) {
                            user = userRepository.getUserById(userIdInt)
                        }

                        user?.let {
                            MainScreen(it, onScheduleMeeting = scheduleMeeting)
                        } ?: run {
                            // Obsługa braku użytkownika w bazie danych
                        }
                    }

                }
                composable("meetingDetails/{meetingId}") { backStackEntry ->
                    val meetingId = backStackEntry.arguments?.getString("meetingId")
                    if (meetingId != null) {
                        // Pobierz szczegóły spotkania z bazy danych i wyświetl MeetingDetailsScreen
                    }
                }
            }
        }
    )
}