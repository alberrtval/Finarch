package com.example.finarch.model

data class AlertItem(
    val mensaje: String,
    val tipo: TipoAlerta,
    val categoria: String = ""
)

enum class TipoAlerta {
    ADVERTENCIA,  // amarillo - cerca del límite (80%-99%)
    ERROR,        // rojo - presupuesto excedido (100%+)
    INFO          // azul - informativo
}

