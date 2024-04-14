package com.example.pdf_tts_android

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class FormState {
    var startingPage by mutableStateOf("1")
    val startingPageOnChange:(String)->Unit={
        startingPage = it
    }
    var endPage by mutableStateOf("1")
    val endPageOnChange:(String)->Unit={
        endPage = it
    }
    var fileName by mutableStateOf("")
    val fileNameOnChanged:(String)->Unit = {
        fileName = it
    }
    var voice by mutableStateOf("")
    val voiceOnChanged:(String)->Unit = {
        voice = it
    }
}