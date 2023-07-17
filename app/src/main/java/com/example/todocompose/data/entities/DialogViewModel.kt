package com.example.todocompose.data.entities

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


class DialogViewModel(): ViewModel()
{
    var showDialog = mutableStateOf(true)
        private set

    fun hide() {
        showDialog.value = false
    }

    fun show() {
        showDialog.value = true
    }
}