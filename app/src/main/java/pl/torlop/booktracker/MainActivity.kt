package pl.torlop.booktracker

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.room.Room
import pl.torlop.booktracker.navigation.MainNavOption
import pl.torlop.booktracker.navigation.NavRoutes
import pl.torlop.booktracker.navigation.NavigationRoutes.Companion.floatingActionButtons
import pl.torlop.booktracker.navigation.NavigationRoutes.Companion.mainNavigationItems
import pl.torlop.booktracker.ui.theme.BookTrackerTheme
import pl.torlop.booktracker.viewmodel.BookViewModel

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            context = applicationContext,
            klass = AppDatabase::class.java,
            name = "database.db"
        ).fallbackToDestructiveMigration().build()
    }
    private val viewModel by viewModels<BookViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BookViewModel(db.bookDao()) as T
                }
            }
        }
    )


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
                    val selectedItemIndex = rememberSaveable() { mutableIntStateOf(0) }
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet {
                                Spacer(modifier = Modifier.height(16.dp))
                                Column {
                                    mainNavigationItems.forEachIndexed() { index, item  ->
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
                                    },
                                )
                            },
                            floatingActionButtonPosition = FabPosition.End,
                            floatingActionButton = {
                                if (floatingActionButtons.containsKey(navBackStackEntry?.destination?.route)) {
                                    val floatingActionButtonData = floatingActionButtons[navBackStackEntry?.destination?.route]!!
                                    FloatingActionScaffoldButton(
                                        navController = navController,
                                        route = floatingActionButtonData.route,
                                        icon = floatingActionButtonData.icon,
                                        contentDescription = floatingActionButtonData.contentDescription
                                    )
                                }
                            }
                        ) {
                            padding ->
                            NavHost(
                                navController,
                                startDestination = NavRoutes.MainRoute.name,
                                modifier = Modifier.padding(padding).fillMaxSize()
                            ) {
                                mainGraph(drawerState, viewModel, navController)
                            }
                        }
                    }

                }

            }
        }
    }
}

fun NavGraphBuilder.mainGraph(drawerState: DrawerState, viewModel: BookViewModel, navController: NavController) {
    navigation(startDestination = MainNavOption.HomeScreen.name, route = NavRoutes.MainRoute.name) {
        composable(MainNavOption.HomeScreen.name){
            HomeScreen(drawerState, viewModel, navController)
        }
        composable(MainNavOption.BooksScreen.name){
            BookListView(drawerState, viewModel, navController)
        }
        composable(MainNavOption.AccountsScreen.name){
            AccountScreen(drawerState, viewModel, navController)
        }
        composable(MainNavOption.AddBookScreen.name){
            AddBookView(drawerState, viewModel, navController)
        }
        composable(
            route ="bookDetails/{isbn}",
            arguments = listOf(navArgument("isbn") { type = NavType.StringType })
            ){
            entry ->
            BookDetailsView(drawerState, viewModel, navController, entry.arguments?.getString("isbn")!!)
        }
    }
}

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


