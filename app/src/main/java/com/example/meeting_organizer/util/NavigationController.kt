package com.example.meeting_organizer.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.meeting_organizer.data.database.meeting.MeetingRepository
import com.example.meeting_organizer.data.database.user.UserRepository
import com.example.meeting_organizer.data.model.Meeting
import com.example.meeting_organizer.data.model.MeetingUserCrossRef
import com.example.meeting_organizer.data.model.User
import com.example.meeting_organizer.ui.login.ForgotPasswordScreen
import com.example.meeting_organizer.ui.login.LoginScreen
import com.example.meeting_organizer.ui.meetingList.MeetingListScreen
import com.example.meeting_organizer.ui.meetingScheduler.MeetingSchedulerScreen
import com.example.meeting_organizer.ui.register.RegisterScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavigationController(
    navController: NavHostController,
    userRepository: UserRepository,
    meetingRepository: MeetingRepository,
    activity: ComponentActivity,
    isDarkTheme: MutableState<Boolean>
) {
    var loginError by rememberSaveable { mutableStateOf(false) }
    var resetPasswordError by rememberSaveable { mutableStateOf(false) }

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
            if (user != null) {

                withContext(Dispatchers.Main) {
                    navController.navigate("meetings/${user.id}")
                }
                loginError = false
            } else {
                // Obsługa błędnych danych logowania
                loginError = true
            }
        }
    }

    val goToScheduler: (Int) -> Unit = { userId ->
        navController.navigate("scheduler/$userId")
    }

    val scheduleMeeting: (Context, Int, String, String, String, String, List<User>) -> Unit = { context, ownerId, title, startTime, endTime, location, selectedUsers ->
        activity.lifecycleScope.launch {
             val owner = userRepository.getUserById(ownerId)

            // Utwórz nowe spotkanie
            val meetingId = meetingRepository.insertMeeting(Meeting(
                ownerId = ownerId,
                title = title,
                startTime = startTime,
                endTime = endTime,
                address = location,
                participants = selectedUsers
            ))

            // Dodaj uczestników do spotkania
            selectedUsers.forEach { user ->
                // Twórz wpisy do tabeli łączącej (wiele do wielu)
                meetingRepository.insertMeetingUserCrossRef(MeetingUserCrossRef(meetingId.toInt(), user.id))
            }
            // Wysyłanie e-maili
            val emailSender = EmailSender()
                val emailContent = """
                Dear Friends,
                
                You have been invited to the following meeting:
                
                Title: $title
                Start Time: $startTime
                End Time: $endTime
                Address: $location
                Organized by: ${owner?.firstName} ${owner?.lastName}
                
                Best regards,
                Meeting Organizer
            """.trimIndent()

                val recipientEmails = selectedUsers.filterNot { it.id == ownerId }.map { it.email }.toTypedArray()
                emailSender.sendEmail(context, recipientEmails, "New Meeting Scheduled", emailContent)

            navController.navigate("meetings/$ownerId")
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = {
            NavHost(navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(
                        onLoginClick = loginUser,
                        onForgotPasswordClick = { navController.navigate("forgotPassword") },
                        onRegisterClick = { navController.navigate("register") },
                        loginError = loginError
                    )
                }
                composable("forgotPassword") {
                    ForgotPasswordScreen(
                        onPasswordResetClick = { email, password ->
                            activity.lifecycleScope.launch {
                                val rowsUpdated = userRepository.updatePassword(email, password)
                                if (rowsUpdated > 0) {
                                    withContext(Dispatchers.Main) {
                                        navController.navigate("login")
                                    }
                                } else {
                                    resetPasswordError = true
                                }
                            }
                        },
                        resetPasswordError = resetPasswordError
                    )
                }
                composable("register") {
                    RegisterScreen(
                        onRegisterClick = registerUser,
                        activity = activity
                    )
                }
                composable("scheduler/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
                    var user by remember { mutableStateOf<User?>(null) }

                    LaunchedEffect(userId) {
                        user = userRepository.getUserById(userId)
                    }

                    user?.let {
                        MeetingSchedulerScreen(
                            it,
                            onScheduleMeeting = scheduleMeeting,
                            userRepository = userRepository,
                            navController = navController,
                            isDarkTheme = isDarkTheme
                        )
                    } ?: run {
                        // Obsługa braku użytkownika w bazie danych

                    }
                }

                composable("meetings/{userId}") {
                    backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0

                    var user by remember { mutableStateOf<User?>(null) }

                    LaunchedEffect(userId) {
                        user = userRepository.getUserById(userId)
                    }
                    user?.let {
                        SessionManager.userId = it.id
                        SessionManager.isLoggedIn = true
                        MeetingListScreen(
                            it,
                            onButtonClick = goToScheduler,
                            userRepository = userRepository,
                            meetingRepository = meetingRepository,
                            navController = navController,
                            isDarkTheme = isDarkTheme
                        )
                    } ?: run {
                        // Obsługa braku użytkownika w bazie danych
                    }

                }
            }
        }
    )
}