package com.example.moodbook

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.moodbook.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.FirebaseDatabase.*
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var registerButton: Button
    private lateinit var countryEditText: EditText
    private lateinit var DOBEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var conf_passworEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var gender: Spinner
    private lateinit var binding: ActivityRegisterBinding
    private var registerViewModel: RegisterViewModel? = null
    private var mAuth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerViewModel = ViewModelProvider(this, RegisterViewModelFactory()).get(
            RegisterViewModel::class.java
        )

        //initialize cloud firestore database and authentication
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        // Setup dropdown list for gender
        gender = binding.gender
        val genderList = arrayOf(getString(R.string.male), getString(R.string.female), getString(R.string.prefer_not_to_specify))
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderList)
        this.gender.setAdapter(adapter)

        // Get access to all user input components on UI
        nameEditText = binding.name
        usernameEditText = binding.username
        passwordEditText = binding.password
        conf_passworEditText =binding.confPassword
        DOBEditText = binding.DOB
        countryEditText = binding.country
        registerButton = binding.register
        registerViewModel!!.registerFormState.observe(
            this,
            Observer<RegisterFormState?> { registerFormState ->
                if (registerFormState == null) {
                    return@Observer
                }
                registerButton.isEnabled = registerFormState.isDataValid
                if (registerFormState.nameError != null) {
                    nameEditText.setError(getString(registerFormState.nameError!!))
                }
                if (registerFormState.usernameError != null) {
                    usernameEditText.setError(getString(registerFormState.usernameError!!))
                }
                if (registerFormState.passwordError != null) {
                    passwordEditText.setError(getString(registerFormState.passwordError!!))
                }
                if (registerFormState.conf_passwError != null) {
                    conf_passworEditText.setError(getString(registerFormState.conf_passwError!!))
                }
                if (registerFormState.dOBError != null) {
                    DOBEditText.setError(getString(registerFormState.dOBError!!))
                }
                if (registerFormState.countryError != null) {
                    countryEditText.setError(getString(registerFormState.countryError!!))
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
                registerViewModel!!.registerDataChanged(
                    usernameEditText.getText().toString(),
                    passwordEditText.getText().toString(),
                    conf_passworEditText.text.toString(), nameEditText.getText().toString(),
                    DOBEditText.getText().toString(), gender.getSelectedItem().toString(),
                    countryEditText.getText().toString()
                )
            }
        }

        // Setup listeners for input field changes
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        conf_passworEditText.addTextChangedListener(afterTextChangedListener)
        DOBEditText.addTextChangedListener(afterTextChangedListener)
        registerButton.setOnClickListener(this)
        gender.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                registerViewModel!!.registerDataChanged(
                    usernameEditText.getText().toString(),
                    passwordEditText.getText().toString(), conf_passworEditText.text.toString(),
                    nameEditText.getText().toString(),
                    DOBEditText.getText().toString(), gender.getSelectedItem().toString(),
                    countryEditText.getText().toString()
                )
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                registerViewModel!!.registerDataChanged(
                    usernameEditText.getText().toString(),
                    passwordEditText.getText().toString(),
                    conf_passworEditText.text.toString(), nameEditText.getText().toString(),
                    DOBEditText.getText().toString(), gender.getSelectedItem().toString(),
                    countryEditText.getText().toString()
                )
            }
        })
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.register) {
            createAccount(usernameEditText!!.text.toString(), passwordEditText!!.text.toString())
        }
    }

    fun addUserData(UID: String?, name: String, dob: String, country: String, gender: String) {
        val user: MutableMap<String, String> = HashMap()
        user["Name"] = name
        user["DOB"] = dob
        user["Country"] = country
        user["Gender"] = gender
        db!!.collection("users").document(UID!!).set(user)
            .addOnSuccessListener(this) { Log.w("SUCCESS", "DocumentSnapshot added with ID:") }
            .addOnFailureListener(this) { e ->
                Log.w(
                    "Registration Failed",
                    "Error adding document",
                    e
                )
            }
    }

    fun createAccount(email: String?, password: String?) {
        // START create_user_with_email

        mAuth!!.createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth!!.currentUser
                    addUserData(
                        user!!.uid, nameEditText!!.text.toString(), DOBEditText!!.text.toString(),
                        countryEditText!!.text.toString(), gender!!.selectedItem.toString()
                    )
                    updateUIWithUser(user)
                } else {
                    updateUIWithUser(null)
                }
            }
        // END create_user_with_email
    }

    private fun updateUIWithUser(user: FirebaseUser?) {
        if (user == null) {
            Toast.makeText(this@RegisterActivity, "Registration Failed", Toast.LENGTH_SHORT).show()
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

                val welcome = "Welcome " + name + "!"
                Toast.makeText(applicationContext, welcome, Toast.LENGTH_LONG).show()
                val i = Intent(applicationContext, MainActivity::class.java)
                startActivity(i)
            }

        }
    }
}