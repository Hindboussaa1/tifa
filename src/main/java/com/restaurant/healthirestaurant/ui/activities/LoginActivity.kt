package com.restaurant.healthirestaurant.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.lifecycle.ViewModelProvider
import com.restaurant.healthirestaurant.databinding.ActivityLoginBinding
import com.restaurant.healthirestaurant.repositories.UserRepository
import com.restaurant.healthirestaurant.utils.Utils.showToast
import com.restaurant.healthirestaurant.viewmodelfactory.RegisterViewModelFactory
import com.restaurant.healthirestaurant.viewmodels.RegisterViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.restaurant.healthirestaurant.R
import kotlinx.coroutines.handleCoroutineException


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Check if the user is already logged in
        if (FirebaseAuth.getInstance().currentUser != null) {
            navigateToMainActivity()
            return
        }
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialization()
        setupListeners() }

        private fun initialization() {
        viewModel = ViewModelProvider(this, RegisterViewModelFactory(UserRepository()))[RegisterViewModel::class.java]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // Initialize the ActivityResultLauncher
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleGoogleSignInResult(result.resultCode, result.data)
        }
        //trigger that loginResult has found some values
        viewModel.loginResult.observe(this) { handleLoginResult(it) }
        }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            handleLoginInput()
        }

        binding.signup.setOnClickListener {
           startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnGoogleSignIn.setOnClickListener {
            googleSignIn()
        }
    }



    private fun googleSignIn() {
        binding.progressBar.visibility = View.VISIBLE
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }


    private fun handleGoogleSignInResult(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
                    firebaseAuthWithGoogle(credential)
                } else {
                    showToast(this, "Google Sign In Failed: Account is null")
                }
            } catch (e: ApiException) {
                showToast(this, "Google Sign In Failed: ${e.message}")
            }
        }
    }



    private fun firebaseAuthWithGoogle(credential: AuthCredential) {
        viewModel.firebaseAuthWithGoogle(credential)
    }


    //Passing info from Class A -> Class VM -> Class R
    private fun handleLoginInput() {
        val email = binding.editTextEmail.text.toString().trim() //editTextView
        val password = binding.editTextPassword.text.toString().trim()

        //Validation
        if (isValidInput(email, password)) {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.loginUser(email, password)
        } else {
            showToast(this, "Invalid input. Please check your credentials.")
        }
    }

    private fun handleLoginResult(result: Pair<Boolean, String>) {
        val (success, message) = result
        if (success) {
            showToast(this, "Login successful")
            navigateToMainActivity()
        } else {
            showToast(this, "Login failed: $message")
        }
        binding.progressBar.visibility = View.GONE
    }


    private fun navigateToMainActivity() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }


    private fun isValidInput(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }

}