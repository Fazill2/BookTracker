package pl.torlop.booktracker

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch



import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.room.Room
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.map
import pl.torlop.booktracker.navigation.MainNavOption
import pl.torlop.booktracker.navigation.NavRoutes
import pl.torlop.booktracker.navigation.NavigationRoutes.Companion.floatingActionButtons
import pl.torlop.booktracker.navigation.NavigationRoutes.Companion.mainNavigationItems
import pl.torlop.booktracker.session.AddSessionManuallyView
import pl.torlop.booktracker.session.StartReadingSessionView
import pl.torlop.booktracker.ui.components.FloatingActionScaffoldButton
import pl.torlop.booktracker.ui.theme.BookTrackerTheme
import pl.torlop.booktracker.utils.Utils.Companion.TOKEN_ID
import pl.torlop.booktracker.utils.Utils.Companion.USER_IMAGE_URI
import pl.torlop.booktracker.utils.Utils.Companion.USER_NAME
import pl.torlop.booktracker.viewmodel.BookViewModel
import pl.torlop.booktracker.viewmodel.SessionViewModel

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

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
    private val sessionViewModel by viewModels<SessionViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SessionViewModel(db.readingSessionDao()) as T
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
                    val userName: State<String?> = dataStore.data.map {
                        it[USER_NAME]
                    }.collectAsState(initial = null)
                    val onLogoutClick: () -> Unit = {
                        scope.launch {
                            dataStore.edit { settings ->
                                settings.remove(USER_NAME)
                                settings.remove(TOKEN_ID)
                                settings.remove(USER_IMAGE_URI)
                            }
                            FirebaseAuth.getInstance().signOut()
                            drawerState.close()
                        }
                    }
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(
                                modifier = Modifier.fillMaxHeight(),
                            ) {
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
                                if (userName.value != null) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    NavigationDrawerItem(
                                        label = { Text("Logout") },
                                        selected = false,
                                        icon = {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                                contentDescription = "Logout"
                                            )
                                        },
                                        onClick = onLogoutClick
                                    )
                                }
                            }
                        },
                    ) {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = {
                                            Text("Book Tracker")
                                            },
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
                                mainGraph(drawerState, viewModel, sessionViewModel, navController, dataStore)
                            }
                        }
                    }

                }

            }
        }
    }
}

fun NavGraphBuilder.mainGraph(drawerState: DrawerState, viewModel: BookViewModel,
                              sessionViewModel: SessionViewModel, navController: NavController,
                              dataStore: DataStore<Preferences>) {
    navigation(startDestination = MainNavOption.HomeScreen.name, route = NavRoutes.MainRoute.name) {
        composable(MainNavOption.HomeScreen.name){
            HomeScreen(drawerState, viewModel, sessionViewModel, navController)
        }
        composable(MainNavOption.BooksScreen.name){
            BookListView(drawerState, viewModel, navController)
        }
        composable(MainNavOption.AccountsScreen.name){
            AccountView(drawerState, viewModel, sessionViewModel, navController, dataStore)
        }
        composable(MainNavOption.AddBookScreen.name){
            AddBookView(drawerState, viewModel, navController)
        }
        composable(
            route ="bookDetails/{isbn}",
            arguments = listOf(navArgument("isbn") { type = NavType.StringType })
            ){
            entry ->
            BookDetailsView(drawerState, viewModel, sessionViewModel, navController, entry.arguments?.getString("isbn")!!)
        }
        composable(
            route = MainNavOption.NewSessionScreen.name,
        ){
            NewSessionView(drawerState, viewModel, sessionViewModel, navController)
        }
        composable(
            route = "newSession/{isbn}",
            arguments = listOf(navArgument("isbn") {
                nullable = true
                defaultValue = null
                type = NavType.StringType })
        ){
            NewSessionView(drawerState, viewModel, sessionViewModel, navController, it.arguments?.getString("isbn"))
        }
        composable("addSessionManually/{isbn}",
            arguments = listOf(navArgument("isbn") { type = NavType.StringType })
        ){
            entry ->
            AddSessionManuallyView(drawerState, viewModel, sessionViewModel,
                navController, entry.arguments?.getString("isbn")!!)
        }
        composable(
            route = "startReadingSession/{isbn}",
            arguments = listOf(navArgument("isbn") { type = NavType.StringType })
        ){
            entry ->
            StartReadingSessionView(drawerState, viewModel, sessionViewModel, navController, entry.arguments?.getString("isbn")!!)
        }
    }
}

