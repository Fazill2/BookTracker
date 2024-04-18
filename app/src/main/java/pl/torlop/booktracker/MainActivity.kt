package pl.torlop.booktracker

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import pl.torlop.booktracker.ui.theme.BookTrackerTheme

class MainActivity : ComponentActivity() {
    val items = listOf(
        NavigationItem(
            route = MainNavOption.HomeScreen.name,
            title = "Welcome",
            selectedIcon = Icons.Filled.Menu,
            unselectedIcon = Icons.Filled.Menu
        ),
        NavigationItem(
            route = MainNavOption.BooksScreen.name,
            title = "Books",
            selectedIcon = Icons.Filled.Menu,
            unselectedIcon = Icons.Filled.Menu
        ),
        NavigationItem(
            route = "settings",
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        ),
        NavigationItem(
            route = "account",
            title = "Account",
            selectedIcon = Icons.Filled.AccountBox,
            unselectedIcon = Icons.Outlined.AccountBox
        )
    )

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val drawerState = rememberDrawerState(DrawerValue.Closed)
                    val navController = rememberNavController()
                    val scope = rememberCoroutineScope()
                    val selectedItemIndex = rememberSaveable() { mutableStateOf(0) }
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet {
                                Column {
                                    items.forEachIndexed() { index, item  ->
                                        NavigationDrawerItem(
                                            label = { Text(item.title) },
                                            selected = index == selectedItemIndex.value,
                                            icon = {
                                                Icon(
                                                    imageVector = if (index == selectedItemIndex.value) {
                                                        item.selectedIcon
                                                    } else {
                                                        item.unselectedIcon
                                                    },
                                                    contentDescription = null
                                                )
                                            },
                                            badge = {
                                                if (item.badgeCount != null) {
                                                    Badge(content = { Text(item.badgeCount.toString()) })
                                                }
                                            },
                                            onClick = {
                                                selectedItemIndex.value = index
                                                scope.launch {
                                                    navController.navigate(item.route)
                                                    drawerState.close()
                                                }
                                            },
                                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                        )
                                    }
                                }
                            }
                        },
                    ) {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = { Text("Book Tracker") },
                                    navigationIcon = {
                                        IconButton(onClick = {
                                            scope.launch {
                                                if (drawerState.isOpen) {
                                                    drawerState.close()
                                                } else {
                                                    drawerState.open()
                                                }
                                            }
                                        }) {
                                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                                        }
                                    }
                                )
                            }
                        ) {
                            NavHost(
                                navController,
                                startDestination = NavRoutes.MainRoute.name
                            ) {
                                mainGraph(drawerState)
                            }
                        }
                    }

                }
            }
        }
    }
}

data class NavigationItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null
)

fun NavGraphBuilder.mainGraph(drawerState: DrawerState) {
    navigation(startDestination = MainNavOption.HomeScreen.name, route = NavRoutes.MainRoute.name) {
        composable(MainNavOption.HomeScreen.name){
            HomeScreen(drawerState)
        }
        composable(MainNavOption.BooksScreen.name){
            BookListView(drawerState)
        }

    }
}

// available routes for the main route
enum class MainNavOption {
    HomeScreen,
    BooksScreen,
    AboutScreen
}

enum class NavRoutes {
    MainRoute,
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BookTrackerTheme {
        HomeScreen(drawerState = rememberDrawerState(DrawerValue.Closed))
    }
}




