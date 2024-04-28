package pl.torlop.booktracker


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pl.torlop.booktracker.navigation.MainNavOption
import pl.torlop.booktracker.utils.Utils.Companion.TOKEN_ID
import pl.torlop.booktracker.utils.Utils.Companion.USER_NAME
import pl.torlop.booktracker.viewmodel.BookViewModel

import java.security.MessageDigest
import java.util.*

@Composable
fun AccountView(drawerState: DrawerState, viewModel: BookViewModel, navController: NavController, dataStore: DataStore<Preferences>) {
    val userName: State<String> = dataStore.data.map {
        it[USER_NAME] ?: "Unknown"
    }.collectAsState(initial = "Unknown")

    AccountComponent(drawerState, navController, dataStore, userName.value)

}

@Composable
fun AccountComponent(drawerState: DrawerState, navController: NavController, dataStore: DataStore<Preferences>, userName: String) {


    if (userName == "Unknown") {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginButtons(dataStore)
        }
    } else {
        LoggedInView(dataStore,
            onLogoutButtonClick = {
                navController.navigate(MainNavOption.HomeScreen.name) {
                    launchSingleTop = true
                }
            }
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
        GoogleSignInButton(dataStore)
        Button(onClick = { onFacebookSignInButtonClick() }) {
            Text("Sign in with Facebook")
        }
    }
}

@Composable
fun LoggedInView(dataStore: DataStore<Preferences>, onLogoutButtonClick: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context: Context = LocalContext.current
        val coroutineScope: CoroutineScope = rememberCoroutineScope()
        val userName: State<String> = dataStore.data.map {
            it[USER_NAME] ?: "Unknown"
        }.collectAsState(initial = "Unknown")

        val onClick: () -> Unit = {
            coroutineScope.launch {
                dataStore.edit { settings ->
                    settings.remove(USER_NAME)
                    settings.remove(TOKEN_ID)
                }

                Toast.makeText(context, "Logged out", Toast.LENGTH_LONG).show()
                onLogoutButtonClick()
            }
        }
        Text(
            text = "Logged in as ${userName.value}",
            style = MaterialTheme.typography.headlineLarge
        )
        Button(onClick = onClick) {
            Text("Logout")
        }
    }
}