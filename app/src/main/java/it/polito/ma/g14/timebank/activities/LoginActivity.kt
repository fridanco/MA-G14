package it.polito.ma.g14.timebank.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.fragments.FirebaseVM
import it.polito.ma.g14.timebank.fragments.User


class LoginActivity : AppCompatActivity() {

//    private lateinit var oneTapClient: SignInClient
//    private lateinit var signInRequest: BeginSignInRequest
//    private lateinit var auth: FirebaseAuth
//    private lateinit var googleSignInClient: GoogleSignInClient
//
//    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
//    private var showOneTapUI = true
//    See: https://developer.android.com/training/basics/intents/result


    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    // Choose authentication providers
    val providers = listOf(AuthUI.IdpConfig.GoogleBuilder().build())

    // Create and launch sign-in intent
    val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val auth = Firebase.auth
        val currentUser = auth.currentUser
        if(currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }

        setContentView(R.layout.activity_login)
        findViewById<Button>(R.id.button4).setOnClickListener{
            signInLauncher.launch(signInIntent)
        }

      /*  oneTapClient = Identity.getSignInClient(this)


        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = Firebase.auth //Initialize auth*/



    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.uid)
                    .get()
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            val document: DocumentSnapshot = it.result
                            //User profile exists -> redirect to mainpage
                            if (document.exists()) {
                                startActivity(Intent(this, MainActivity::class.java))
                                this.finish()
                            }
                            //User profile does not exist -> create user object and redirect to mainpage
                            else {
                                FirebaseFirestore.getInstance().collection("users")
                                    .document(user.uid)
                                    .set(
                                        User().apply {
                                            fullname = user.displayName ?: "Sample fullname"
                                            nickname = user.displayName ?: "Sample nickname"
                                            email = user.email ?: "example@gmail.com"
                                            location = "Sample location"
                                            description = ""
                                            skills = emptyList()
                                        }
                                    )
                                    .addOnSuccessListener {
                                        startActivity(Intent(this, MainActivity::class.java))
                                        this.finish()
                                    }
                            }
                        }
                        else{
                            //TODO: Firebase create user profile failed
                        }
                    }
            }

            // ...
        }
        else {
            // TODO: Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

}