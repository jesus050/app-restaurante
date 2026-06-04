package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.screens.BookingScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppShell(viewModel)
            }
        }
    }
}

@Composable
fun MainAppShell(viewModel: MainViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()

    if (!isLoggedIn) {
        LoginScreen(viewModel = viewModel)
    } else {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Crossfade(targetState = currentTab, label = "tab_fade") { targetTab ->
                    when (targetTab) {
                        AppTab.HOME -> HomeScreen(viewModel = viewModel)
                        AppTab.MENU -> MenuScreen(viewModel = viewModel)
                        AppTab.RESERVAS -> BookingScreen(viewModel = viewModel)
                        AppTab.CHAT -> ChatScreen(viewModel = viewModel)
                        AppTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    // Elegant M3 Navigation Bar with correct safe drawing
    NavigationBar(
        containerColor = Color(0xFF0D0D11),
        tonalElevation = 8.dp,
        modifier = Modifier
            .height(72.dp)
            .navigationBarsPadding() // Ensures no overlapping with safe system gestures
            .testTag("main_bottom_nav")
    ) {
        val tabs = listOf(
            NavigationItem(
                tab = AppTab.HOME,
                label = "Inicio",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                tag = "tab_home"
            ),
            NavigationItem(
                tab = AppTab.MENU,
                label = "Menú",
                selectedIcon = Icons.Filled.List,
                unselectedIcon = Icons.Outlined.List,
                tag = "tab_menu"
            ),
            NavigationItem(
                tab = AppTab.RESERVAS,
                label = "Reservas",
                selectedIcon = Icons.Filled.CalendarToday,
                unselectedIcon = Icons.Outlined.CalendarToday,
                tag = "tab_reservas"
            ),
            NavigationItem(
                tab = AppTab.CHAT,
                label = "Chat",
                selectedIcon = Icons.Filled.ChatBubble,
                unselectedIcon = Icons.Outlined.ChatBubbleOutline,
                tag = "tab_chat"
            ),
            NavigationItem(
                tab = AppTab.PROFILE,
                label = "Perfil",
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                tag = "tab_profile"
            )
        )

        tabs.forEach { item ->
            val isSelected = currentTab == item.tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color(0xFF8E8E93),
                    selectedTextColor = Color(0xFFD36113),
                    unselectedTextColor = Color(0xFF8E8E93),
                    indicatorColor = Color(0xFFD36113) // Highlight indicator bubble matches organic Saboré orange
                ),
                modifier = Modifier.testTag(item.tag)
            )
        }
    }
}

data class NavigationItem(
    val tab: AppTab,
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val tag: String
)
