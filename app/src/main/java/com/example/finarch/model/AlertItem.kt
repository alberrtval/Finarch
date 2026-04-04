package com.example.finarch.model

data class AlertItem(
    val mensaje: String,
    val tipo: TipoAlerta
)

enum class TipoAlerta {
    ADVERTENCIA,  // amarillo
    INFO          // azul
}