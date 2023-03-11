package com.example.pokeforge.ui.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.example.pokeforge.MainActivity
import com.example.pokeforge.databinding.ActivityLoginBinding

import com.example.pokeforge.R
import com.example.pokeforge.StartingGameActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        login.isEnabled = true
        val loading = binding.loading

        val usernameRegister = binding.usernameRegister
        val passwordRegister = binding.passwordRegister
        val passwordConfirm = binding.passwordConfirmRegister
        val register = binding.register
        register?.isEnabled = true


        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)


        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                val email = username.text.toString()
                val password = password.text.toString()

                Firebase.auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@LoginActivity) { task ->
                        if (task.isSuccessful) {
                            // La connexion avec email/mot de passe est réussie
                            val user = Firebase.auth.currentUser
                            // Faites ce que vous voulez avec l'utilisateur connecté
                            Toast.makeText(
                                this@LoginActivity,
                                "Connexion réussie",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Return to main activity with user
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("userUID", user?.uid)
                            startActivity(intent)
                        } else {
                            // La connexion a échoué
                            Toast.makeText(
                                this@LoginActivity,
                                "Connexion échouée",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.loading.visibility = View.GONE
                        }
                    }

            }

            binding.register?.setOnClickListener {
                loading.visibility = View.VISIBLE
                val email = usernameRegister?.text.toString()
                val password = passwordRegister?.text.toString()
                val passwordConfirm = passwordConfirm?.text.toString()

                if (password == passwordConfirm) {
                    Firebase.auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            if (task.isSuccessful) {
                                // La connexion avec email/mot de passe est réussie
                                val user = Firebase.auth.currentUser
                                // Faites ce que vous voulez avec l'utilisateur connecté
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Inscription réussie",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Return to main activity with user
                                val intent = Intent(this@LoginActivity, StartingGameActivity::class.java)
                                intent.putExtra("userUID", user?.uid)
                                println("userUID : ${user?.uid}")
                                startActivity(intent)
                            } else {
                                // La connexion a échoué
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Inscription échouée",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.loading.visibility = View.GONE
                            }
                        }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Les mots de passe ne correspondent pas",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.loading.visibility = View.GONE
                }
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}