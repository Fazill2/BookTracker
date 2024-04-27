package pl.torlop.booktracker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings

class NavigationRoutes {
    companion object {
        val mainNavigationItems = listOf(
            NavigationItem(
                route = MainNavOption.HomeScreen.name,
                title = "Home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            ),
            NavigationItem(
                route = MainNavOption.BooksScreen.name,
                title = "Books",
                selectedIcon = Icons.Filled.AutoStories,
                unselectedIcon = Icons.Outlined.AutoStories
            ),
            NavigationItem(
                route = MainNavOption.AccountsScreen.name,
                title = "Account",
                selectedIcon = Icons.Filled.AccountBox,
                unselectedIcon = Icons.Outlined.AccountBox
            )
        )

        val floatingActionButtons = mapOf(
            MainNavOption.BooksScreen.name to FloatingActionButtonData(
                route = MainNavOption.AddBookScreen.name,
                icon = Icons.Filled.Add,
                contentDescription = "Add a new book"
            ),
            MainNavOption.HomeScreen.name to FloatingActionButtonData(
                route = MainNavOption.NewSessionScreen.name,
                icon = Icons.Filled.MoreTime,
                contentDescription = "Start a new session"
            )
        )
    }
}

enum class MainNavOption {
    HomeScreen,
    BooksScreen,
    AccountsScreen,
    AddBookScreen,
    BookDetailsScreen,
    NewSessionScreen,
    AddSessionManuallyScreen
}

enum class NavRoutes {
    MainRoute,
}