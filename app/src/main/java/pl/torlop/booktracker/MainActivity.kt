package pl.torlop.booktracker

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Create
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.room.Room
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
            route = MainNavOption.AccountsScreen.name,
            title = "Account",
            selectedIcon = Icons.Filled.AccountBox,
            unselectedIcon = Icons.Outlined.AccountBox
        )
    )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()
        setContent {
            BookTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val drawerState = rememberDrawerState(DrawerValue.Closed)
                    val navController = rememberNavController()
                    val scope = rememberCoroutineScope()
                    val selectedItemIndex = rememberSaveable() { mutableIntStateOf(0) }
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet {
                                Spacer(modifier = Modifier.height(16.dp))
                                Column {
                                    items.forEachIndexed() { index, item  ->
                                        NavigationDrawerItem(
                                            label = { Text(item.title) },
                                            selected = index == selectedItemIndex.intValue,
                                            icon = {
                                                Icon(
                                                    imageVector = if (index == selectedItemIndex.intValue) {
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
                                                selectedItemIndex.intValue = index
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
                            },
                        ) {
                            padding ->
                            NavHost(
                                navController,
                                startDestination = NavRoutes.MainRoute.name,
                                modifier = Modifier.padding(padding).fillMaxSize()
                            ) {
                                mainGraph(drawerState, db)
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

fun NavGraphBuilder.mainGraph(drawerState: DrawerState, db: AppDatabase? = null) {
    navigation(startDestination = MainNavOption.HomeScreen.name, route = NavRoutes.MainRoute.name) {
        composable(MainNavOption.HomeScreen.name){
            HomeScreen(drawerState, db)
        }
        composable(MainNavOption.BooksScreen.name){
            BookListView(drawerState, db)
        }
        composable(MainNavOption.AccountsScreen.name){
            AccountScreen(drawerState, db)
        }
    }
}

// available routes for the main route
enum class MainNavOption {
    HomeScreen,
    BooksScreen,
    AccountsScreen,
}

enum class NavRoutes {
    MainRoute,
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BookTrackerTheme {
        HomeScreen(drawerState = rememberDrawerState(DrawerValue.Closed), db = null)
    }
}




