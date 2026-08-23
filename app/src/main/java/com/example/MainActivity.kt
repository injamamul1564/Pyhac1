package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberTopHeader
import com.example.ui.screens.AiBattleScreen
import com.example.ui.screens.AiTutorChatScreen
import com.example.ui.screens.SandboxScreen
import com.example.ui.screens.SecurityLabScreen
import com.example.ui.screens.TracksScreen
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.PyHackerViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PyHackerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PyHackerApp(viewModel = viewModel)
            }
        }
    }
}

sealed class NavigationTab(
    val index: Int,
    val title: String,
    val icon: ImageVector,
    val activeColor: Color
) {
    object Tracks : NavigationTab(0, "Tracks", Icons.Default.School, CyberGreen)
    object Sandbox : NavigationTab(1, "Sandbox", Icons.Default.Code, CyberCyan)
    object Battle : NavigationTab(2, "AI Battle", Icons.Default.SportsEsports, CyberAmber)
    object Tutor : NavigationTab(3, "AI Tutor", Icons.Default.AutoAwesome, CyberPurple)
    object Security : NavigationTab(4, "Labs", Icons.Default.Security, CyberRed)

    companion object {
        val allTabs = listOf(Tracks, Sandbox, Battle, Tutor, Security)
    }
}

@Composable
fun PyHackerApp(viewModel: PyHackerViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()
    val userProgress by viewModel.userProgress.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            CyberTopHeader(
                currentXp = userProgress?.xp ?: 50,
                currentStreak = userProgress?.streakDays ?: 1,
                levelName = userProgress?.currentLevel ?: "BEGINNER",
                onLevelClick = { viewModel.setActiveTab(0) }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                containerColor = DarkSurface,
                tonalElevation = 8.dp
            ) {
                NavigationTab.allTabs.forEach { tab ->
                    val isSelected = activeTab == tab.index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setActiveTab(tab.index) },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = tab.activeColor,
                            selectedTextColor = tab.activeColor,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = tab.activeColor.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                0 -> TracksScreen(viewModel = viewModel)
                1 -> SandboxScreen(viewModel = viewModel)
                2 -> AiBattleScreen(viewModel = viewModel)
                3 -> AiTutorChatScreen(viewModel = viewModel)
                4 -> SecurityLabScreen(viewModel = viewModel)
            }
        }
    }
}

