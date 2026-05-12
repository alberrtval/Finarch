package com.example.finarch.model

data class Movimiento(
    val id: String = "",
    val uid: String = "",
    val tipo: TipoMovimiento = TipoMovimiento.INGRESO,
    val monto: Double = 0.0,
    val categoria: String = "",
    val fecha: String = "",
    val metodoPago: String = "",
    val descripcion: String = "",
    val etiquetas: List<String> = emptyList()
)