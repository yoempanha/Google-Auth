package com.example.googleauth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.googleauth.ui.theme.GoogleAuthTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoogleAuthTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen()
                }
            }
        }
    }
}


@Composable
fun MainScreen() {
    val context = LocalContext.current
    val googleSignInClient = remember {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        )
    }

    var username by remember {
        mutableStateOf(User())
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)
            username = username.copy(
                username = account?.givenName.orEmpty(),
                id = account?.id.orEmpty(),
                email = account?.email.orEmpty(),
                firstName = account?.familyName.orEmpty(),
                lastName = account?.givenName.orEmpty()
            )
            errorMessage = null
        } catch (e: ApiException) {
            errorMessage =
                "Can not get access from google Account." + "\nCause: ${e.message}" + "\nStatus Code: ${e.statusCode}" + "\nStatus: ${e.status.statusMessage}"
        }
        isLoading = false
    }

    fun onGoogleSignIn() {
        isLoading = true
        launcher.launch(googleSignInClient.signInIntent)
    }

    fun onFacebookSignIn() {
        isLoading = true
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = if (errorMessage == null) "$username" else errorMessage!!,
                    fontSize = 16.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(onClick = ::onGoogleSignIn) {
                    Text(text = "Google Sign Up")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(onClick = ::onFacebookSignIn) {
                    Text(text = "Facebook Sing Up")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Preview() {
    MainScreen()
}


data class User(
    val username: String = "",
    val email: String = "",
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
)
