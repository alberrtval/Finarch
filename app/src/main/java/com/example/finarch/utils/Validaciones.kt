package com.example.finarch.utils

import android.util.Patterns


// Validacion para el correo
fun String.validacionCorreo(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

// validacion para campos con texto como nombre completo entre otros
fun String.validacionSoloTexto(): Boolean{
    val regex = Regex("^[\\p{L}\\s]+\$")
    return regex.matches(this) && this.length <= 75
}

//Validacion para el campo de la contraseña
fun String.validacionContraseña(): Boolean{
    val numeros = arrayOf(1,2,3,4,5,6,7,8,9,0)
    return this.length >= 6 && this.length <=20 && this.any { it.isDigit()}
}

//validacion para los montos de la aplicación
fun String.validacionMonto(): Boolean{
    val regex = Regex("^(0\\.\\d*[1-9]\\d*|[1-9]\\d*(\\.\\d+)?)\$")
    return regex.matches(this)
}