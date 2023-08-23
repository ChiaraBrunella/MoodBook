package com.example.moodbook

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity() {
    private var loginViewModel: LoginViewModel? = null
    private lateinit var db: FirebaseFirestore
    private var mAuth: FirebaseAuth? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory()).get(
            LoginViewModel::class.java
        )

        //initialize cloud firestore database and authentication
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login)
        val signUpText = findViewById<EditText>(R.id.register)
        loginViewModel!!.loginFormState.observe(this, Observer<LoginFormState?> { loginFormState ->
            if (loginFormState == null) {
                return@Observer
            }
            loginButton.isEnabled = loginFormState.isDataValid
            if (loginFormState.usernameError != null) {
                usernameEditText.error = getString(loginFormState.usernameError!!)
            }
            if (loginFormState.passwordError != null) {
                passwordEditText.error = getString(loginFormState.passwordError!!)
            }
        })
        val afterTextChangedListener: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel!!.loginDataChanged(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        loginButton.setOnClickListener {
            signIn(
                usernameEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }
        signUpText.setOnClickListener {
            val i = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(i)
        }
    }

    private fun signIn(email: String, password: String) {

        // [START sign_in_with_email]
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth!!.currentUser
                    updateUIWithUser(user)
                } else {
                    updateUIWithUser(null)
                }
            }
        // [END sign_in_with_email]
    }

    private fun updateUIWithUser(user: FirebaseUser?) {
        if (user == null) {
            Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()
            return
        }
        val uid = user!!.uid
        db!!.collection("users").document(uid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val docsnap = task.result
                val name = docsnap.getString("Name")
                Log.i("docsnap", name.toString())
                val country = docsnap.getString("Country")
                val dob = docsnap.getString("DOB")
                val gender = docsnap.getString("Gender")

                /*
                // memorizzo user-id in shared preferences per non richiedere ulteriormente il login
                val sharedPref =
                    this.getSharedPreferences("com.example.moodbook", Context.MODE_PRIVATE)
                val prefEditor = sharedPref?.edit()
                prefEditor?.putString("loggedUser", uid)
                prefEditor?.apply()
                Log.i("in Login: loggedUser", uid)

                */
                val welcome = "Welcome " + user.email + "!"
                Toast.makeText(applicationContext, welcome, Toast.LENGTH_LONG).show()
                val i = Intent(applicationContext, MainActivity::class.java)
                startActivity(i)
            }


        }

    }
    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}