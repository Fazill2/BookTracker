package pl.torlop.booktracker.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null
)