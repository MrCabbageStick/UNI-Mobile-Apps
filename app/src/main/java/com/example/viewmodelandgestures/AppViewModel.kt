package com.example.viewmodelandgestures

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val _phoneNumber = MutableStateFlow("12345")
    val phoneNumber = _phoneNumber.asStateFlow()
    fun setPhoneNumber(newValue: String){ _phoneNumber.value = newValue }

    private val _message = MutableStateFlow("Żółwik")
    val message = _message.asStateFlow()
    fun setMessage(newValue: String){ _message.value = newValue }
    
    private val _hasSmsPermission = MutableStateFlow(false)
    val hasSmsPermission = _hasSmsPermission.asStateFlow()
    fun setSmsPermission(hasPermission: Boolean){ _hasSmsPermission.value = hasPermission }
}
