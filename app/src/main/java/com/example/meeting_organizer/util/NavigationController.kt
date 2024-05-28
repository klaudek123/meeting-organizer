package com.example.meeting_organizer.util

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.meeting_organizer.data.database.meeting.MeetingRepository
import com.example.meeting_organizer.data.database.user.UserRepository
import com.example.meeting_organizer.data.model.Meeting
import com.example.meeting_organizer.data.model.User
import com.example.meeting_organizer.ui.login.LoginScreen
import com.example.meeting_organizer.ui.main.MainScreen
import com.example.meeting_organizer.ui.main.MeetingListScreen
import com.example.meeting_organizer.ui.register.RegisterScreen
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavigationController(
    userRepository: UserRepository,
    meetingRepository: MeetingRepository,
    activity: ComponentActivity
) {
    val navController = rememberNavController()

    val registerUser: (String, String, String, String, String) -> Unit = { firstName, lastName, email, phoneNumber, password ->
        activity.lifecycleScope.launch {
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
        activity.lifecycleScope.launch {
            val user = userRepository.getUser(email, password)
            if (user != null && user.email == email && user.password == password) {
                withContext(Dispatchers.Main) {
                    navController.navigate("meetings/${user.id} ")
                }
            } else {
                // Obsługa błędnych danych logowania
                println("error")
            }
        }
    }

    val scheduleMeeting: (Int, String, String, String) -> Unit = { userId, title, time, location ->
        activity.lifecycleScope.launch {
            meetingRepository.insertMeeting( Meeting(
                title = title,
                userId = userId,
                startTime = time,
                endTime = time,
                address = location.toString() // You might want to get the real address here
            )
            )
            navController.navigate("meetings/$userId")
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = {
            NavHost(navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(
                        onLoginClick = loginUser,
                        onRegisterClick = { navController.navigate("register") },
                        activity = activity
                    )
                }
                composable("register") {
                    RegisterScreen(
                        onRegisterClick = registerUser,
                        activity = activity
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
                            MainScreen(
                                it,
                                onScheduleMeeting = scheduleMeeting,
                                activity = activity
                            )
                        } ?: run {
                            // Obsługa braku użytkownika w bazie danych
                        }
                    }

                }

                composable("meetings/{userId}") {
                    backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
                    MeetingListScreen(userId = userId, meetingRepository = meetingRepository)
                }
            }
        }
    )
}