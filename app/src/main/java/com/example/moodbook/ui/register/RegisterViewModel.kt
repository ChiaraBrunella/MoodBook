package com.example.moodbook

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.ParseException
import java.text.SimpleDateFormat

class RegisterViewModel internal constructor() : ViewModel() {
    val registerFormState = MutableLiveData<RegisterFormState?>()
    fun getRegisterFormState(): LiveData<RegisterFormState?> {
        return registerFormState
    }

    fun registerDataChanged(
        username: String?, password: String?, name: String?,
        DOB: String, gender: String, country: String?
    ) {
        if (!isUserNameValid(username)) {
            registerFormState.setValue(
                RegisterFormState(
                    R.string.invalid_username,
                    null,
                    null,
                    null,
                    null,
                    null
                )
            )
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(
                RegisterFormState(
                    null,
                    R.string.invalid_password,
                    null,
                    null,
                    null,
                    null
                )
            )
        } else if (!isDOBValid(DOB)) {
            registerFormState.setValue(
                RegisterFormState(
                    null,
                    null,
                    null,
                    R.string.invalid_DOB,
                    null,
                    null
                )
            )
        } else if (!isGenderValid(gender)) {
            registerFormState.setValue(
                RegisterFormState(
                    null,
                    null,
                    null,
                    null,
                    R.string.invalid_gender,
                    null
                )
            )
        } else {
            registerFormState.setValue(RegisterFormState(true))
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String?): Boolean {
        if (username == null) {
            return false
        }
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            !username.trim { it <= ' ' }.isEmpty()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String?): Boolean {
        return password != null && password.trim { it <= ' ' }.length > 5
    }

    // A placeholder DOB validation check
    private fun isDOBValid(DOB: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        dateFormat.isLenient = false
        try {
            dateFormat.parse(DOB.trim { it <= ' ' })
        } catch (pe: ParseException) {
            return false
        }
        return true
    }

    private fun isGenderValid(gender: String): Boolean {
        return if (gender == "Select") {
            false
        } else true
    }
}