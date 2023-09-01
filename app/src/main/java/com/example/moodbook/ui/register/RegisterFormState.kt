package com.example.moodbook

/**
 * Data validation state of the register form.
 */
class RegisterFormState {
    var usernameError: Int?
        private set
    var passwordError: Int?
        private set
    var conf_passwError: Int?
        private set
    var nameError: Int?
        private set
    var dOBError: Int?
        private set
    var genderError: Int?
        private set
    var countryError: Int?
        private set
    var isDataValid: Boolean
        private set

    constructor(
        usernameError: Int?, passwordError: Int?,conf_passwError: Int?,
        nameError: Int?, DOBError: Int?,
        genderError: Int?, countryError: Int?
    ) {
        this.usernameError = usernameError
        this.passwordError = passwordError
        this.conf_passwError = conf_passwError
        this.nameError = nameError
        this.countryError = countryError
        dOBError = DOBError
        this.genderError = genderError
        isDataValid = false
    }

    constructor(isDataValid: Boolean) {
        usernameError = null
        passwordError = null
        conf_passwError = null
        nameError = null
        countryError = null
        dOBError = null
        genderError = null
        this.isDataValid = isDataValid
    }
}