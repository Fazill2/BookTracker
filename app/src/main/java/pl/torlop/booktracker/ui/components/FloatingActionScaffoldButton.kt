package pl.torlop.booktracker.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun FloatingActionScaffoldButton(
    navController: NavController,
    route: String,
    icon: ImageVector,
    contentDescription: String
) {
    FloatingActionButton(
        onClick = {
            navController.navigate(route)
        },
        modifier = Modifier
            .padding(16.dp)

    ) {
        Icon(icon, contentDescription)
    }
}