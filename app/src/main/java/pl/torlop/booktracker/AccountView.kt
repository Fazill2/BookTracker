package pl.torlop.booktracker


import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pl.torlop.booktracker.firebase.FirebaseUtils
import pl.torlop.booktracker.navigation.MainNavOption
import pl.torlop.booktracker.utils.Utils.Companion.TOKEN_ID
import pl.torlop.booktracker.utils.Utils.Companion.USER_IMAGE_URI
import pl.torlop.booktracker.utils.Utils.Companion.USER_NAME
import pl.torlop.booktracker.viewmodel.BookViewModel
import pl.torlop.booktracker.viewmodel.SessionViewModel

import java.security.MessageDigest
import java.util.*

@Composable
fun AccountView(drawerState: DrawerState, viewModel: BookViewModel, sessionViewModel: SessionViewModel, navController: NavController, dataStore: DataStore<Preferences>) {
    val userName: State<String?> = dataStore.data.map {
        it[USER_NAME]
    }.collectAsState(initial = null)

    AccountComponent(drawerState, navController, viewModel, sessionViewModel, dataStore, userName.value)
}

@Composable
fun AccountComponent(drawerState: DrawerState, navController: NavController, viewModel: BookViewModel, sessionViewModel: SessionViewModel, dataStore: DataStore<Preferences>, userName: String?) {


    if (userName == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginButtons(dataStore)
        }
    } else {
        LoggedInView(dataStore,
            viewModel = viewModel,
            sessionViewModel = sessionViewModel
        )
    }
}


@Composable
fun GoogleSignInButton(dataStore: DataStore<Preferences>) {
    val context: Context = LocalContext.current
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val onClick: () -> Unit = {
        val credentialManager: CredentialManager = CredentialManager.create(context)

        val rawNonce: String = UUID.randomUUID().toString()
        val bytes: ByteArray = rawNonce.toByteArray()
        val md: MessageDigest = MessageDigest.getInstance("SHA-256")
        val digest: ByteArray = md.digest(bytes)
        val hashedNonce: String = digest.fold("") { str, it -> str + "%02x".format(it) }

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.resources.getString(R.string.google_client_id))
            .setNonce(hashedNonce)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                val credential = result.credential

                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                val googleIdToken = googleIdTokenCredential.idToken

                Log.i("login", googleIdToken)

                dataStore.edit { settings ->
                    settings[USER_NAME] = googleIdTokenCredential.displayName ?: "Unknown"
                    settings[TOKEN_ID] = googleIdToken
                    if (googleIdTokenCredential.profilePictureUri != null)
                        settings[USER_IMAGE_URI] = googleIdTokenCredential.profilePictureUri.toString()
                }

                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                Firebase.auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Signed in to firebase", Toast.LENGTH_LONG).show()
                        } else {
                            println(task.exception)
                            Toast.makeText(context, "Error signing in", Toast.LENGTH_LONG).show()
                        }
                    }

                Toast.makeText(context, "Signed in", Toast.LENGTH_LONG).show()
            } catch (e: GetCredentialException) {
                Log.e("login", "Error signing in", e)
                Toast.makeText(context, "No credentials available for signing", Toast.LENGTH_LONG).show()
            } catch (e: GoogleIdTokenParsingException) {
                Log.e("login", "Error parsing Google ID token", e)
                Toast.makeText(context, "Error parsing Google ID token", Toast.LENGTH_LONG).show()
            }
        }
    }

    Button(onClick = onClick) {
        Text(text = "Sign in with Google")
    }
}

@Composable
fun LoginButtons(dataStore: DataStore<Preferences>,onFacebookSignInButtonClick: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sign in to see lifetime stats",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(8.dp)
        )
        GoogleSignInButton(dataStore)
    }
}

@Composable
fun LoggedInView(dataStore: DataStore<Preferences>, viewModel: BookViewModel, sessionViewModel: SessionViewModel) {
    val context: Context = LocalContext.current
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val books = viewModel.getAllBooks().collectAsState(initial = emptyList()).value
    val sessions = sessionViewModel.getAllSessions().collectAsState(initial = emptyList()).value
    val userName: State<String> = dataStore.data.map {
        it[USER_NAME] ?: "Unknown"
    }.collectAsState(initial = "Unknown")
    val token = dataStore.data.map {
        it[TOKEN_ID]
    }.collectAsState(initial = null).value
    val userImageUri: State<String> = dataStore.data.map {
        it[USER_IMAGE_URI] ?: ""
    }.collectAsState(initial = "")

    val booksRead = viewModel.getNumberOfReadBooks().collectAsState(initial = 0)
    val totalReadingTime  = sessionViewModel.getTotalReadingTime().collectAsState(initial = 0)
    val totalPagesRead = sessionViewModel.getTotalPagesRead().collectAsState(initial = 0)

    val syncData = {
        val firebaseUtils: FirebaseUtils = FirebaseUtils(Firebase.auth, Firebase.firestore, sessionViewModel, viewModel)
        coroutineScope.launch {
            firebaseUtils.syncData(books, sessions, context)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            AsyncImage(
                modifier = Modifier.size(150.dp)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.tertiary, CircleShape),
                contentScale = ContentScale.FillHeight,
                contentDescription = "User image",
                model = ImageRequest.Builder(LocalContext.current)
                    .data(userImageUri.value)
                    .crossfade(true)
                    .build(),
            )
            Text(
                text = userName.value,
                style = MaterialTheme.typography.headlineLarge

            )
            Spacer(
                modifier = Modifier.height(32.dp)
            )
            StatsComponent(
                listOf(
                    Pair("Books read:", booksRead.value.toString() + " books"),
                    Pair("Total reading time:", totalReadingTime.value.toString() + " min"),
                    Pair("Total pages read:", totalPagesRead.value.toString() + " pages")
                )
            )
            // button for syncing data with firebase
            Button(onClick = { syncData() }) {
                Text("Sync data")
            }
        }
    }
}

@Composable
fun StatsComponent(stats: List<Pair<String, String>>){
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        stats.forEach {
            Row(
                modifier = Modifier.fillMaxWidth(0.7f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = it.first)
                Text(text = it.second)
            }
        }
    }
}

