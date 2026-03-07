package com.example.finarch

data class AlertItem(
    val mensaje: String,
    val tipo: TipoAlerta
)

enum class TipoAlerta {
    ADVERTENCIA,  // amarillo
    INFO          // azul
}