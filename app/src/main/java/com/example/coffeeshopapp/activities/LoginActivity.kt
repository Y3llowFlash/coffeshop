package com.example.coffeeshopapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                val account = task.getResult(ApiException::class.java)
                handleGoogleAccount(account)
            } catch (exception: ApiException) {
                Toast.makeText(
                    this,
                    exception.localizedMessage ?: "Google sign-in failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        configureGoogleSignIn()

        if (authViewModel.getCurrentUser() != null) {
            navigateAfterLogin()
            return
        }

        setContentView(createContentView())
    }

    private fun configureGoogleSignIn() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun createContentView(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(48, 48, 48, 48)

            addView(SignInButton(context).apply {
                setSize(SignInButton.SIZE_WIDE)
                setOnClickListener {
                    googleSignInClient.signOut().addOnCompleteListener {
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    }
                }
            })
        }
    }

    private fun handleGoogleAccount(account: GoogleSignInAccount?) {
        val idToken = account?.idToken
        if (idToken.isNullOrBlank()) {
            Toast.makeText(this, "Missing Google ID token.", Toast.LENGTH_SHORT).show()
            return
        }

        authViewModel.signInWithGoogle(idToken) { result ->
            runOnUiThread {
                if (result.isSuccess) {
                    navigateAfterLogin()
                } else {
                    Toast.makeText(
                        this,
                        result.exceptionOrNull()?.localizedMessage ?: "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun navigateAfterLogin() {
        val destinationClassName = intent.getStringExtra(EXTRA_DESTINATION)
        val destinationIntent = destinationClassName
            ?.takeIf { it.isNotBlank() }
            ?.let { className ->
                runCatching {
                    Intent(this, Class.forName(className))
                }.getOrNull()
            }
            ?: Intent(this, MainActivity::class.java)

        startActivity(destinationIntent)
        finish()
    }

    companion object {
        const val EXTRA_DESTINATION = "extra_destination"
    }
}
