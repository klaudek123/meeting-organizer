package com.example.meeting_organizer.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.meeting_organizer.util.SessionManager
import com.example.meeting_organizer.util.SessionManager.isLoggedIn
import com.example.meeting_organizer.util.SessionManager.userId
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    isDarkTheme: MutableState<Boolean>,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController = navController)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Meeting Organizer") },
                    navigationIcon = {
                        IconButton(onClick = {
                            if(isLoggedIn && userId != 0){
                                coroutineScope.launch { drawerState.open() }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isDarkTheme.value = !isDarkTheme.value }) {
                            Icon(
                                imageVector = if (isDarkTheme.value) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = "Toggle Theme"
                            )
                        }
                        IconButton(onClick = {
                            if(isLoggedIn){
                                isLoggedIn = false
                                userId = 0
                                navController.navigate("login")
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout"
                            )
                        }
                    }
                )
            },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    content()
                }
            }
        )
    }
}

@Composable
fun DrawerContent(navController: NavController) {
    Column(
        modifier = Modifier
            .background(Color(0xFFD7D2E4))
            .fillMaxHeight()
            .width(IntrinsicSize.Min)
    ) {
        Text(text = "Navigation", modifier = Modifier.padding(8.dp), fontSize = 16.sp)
        Divider()
        Text(
            text = "Scheduler",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    navController.navigate("scheduler/${userId}")
                }
        )
        Text(
            text = "Meetings",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    navController.navigate("meetings/${SessionManager.userId}")
                }
        )
    }
}


