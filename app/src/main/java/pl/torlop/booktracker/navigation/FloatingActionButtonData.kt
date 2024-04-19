package pl.torlop.booktracker.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class FloatingActionButtonData(
    val route: String,
    val icon: ImageVector,
    val contentDescription: String
)