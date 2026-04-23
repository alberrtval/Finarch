package com.example.finarch.utils

//Enum utilizado para validar campo que da error en formularios
enum class CampoError{
    NOMBRE, EMAIL, CONTRASENA, CONFIRMAR, CONTRASENAYCONF
}

//Definicion de data class para devolver mensaje de error variado segun campo
data class clasificacionError(val campo: CampoError, val mensaje: String)