package com.example.moodbook.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    private val _first_name_txt = MutableLiveData<String>().apply {
        value = "first name"
    }
    val first_name_txt: LiveData<String> = _first_name_txt

    private val _country_txt = MutableLiveData<String>().apply {
        value = "country"
    }
    val country_txt: LiveData<String> = _country_txt

    private val _dob_txt = MutableLiveData<String>().apply {
        value = "dob"
    }
    val dob_txt: LiveData<String> = _dob_txt

    private val _gender_txt = MutableLiveData<String>().apply {
        value = "gender"
    }
    val gender_txt: LiveData<String> = _gender_txt


}