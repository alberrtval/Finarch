package com.example.finarch.utils

import com.example.finarch.model.AlertItem
import com.example.finarch.model.Categoria
import com.example.finarch.model.Movimiento
import com.example.finarch.model.TipoAlerta
import com.example.finarch.model.TipoMovimiento

fun calcularAlertas(
    movimientos: List<Movimiento>,
    categorias: List<Categoria>
): List<AlertItem> {
    val alertas = mutableListOf<AlertItem>()

    // Agrupar gastos por categoría del mes actual
    val gastosPorCategoria = movimientos
        .filter { it.tipo == TipoMovimiento.GASTO }
        .groupBy { it.categoria }
        .mapValues { (_, movs) -> movs.sumOf { it.monto } }

    // Comparar con presupuesto de cada categoría
    categorias
        .filter { it.presupuesto > 0 }
        .forEach { categoria ->
            val gastado = gastosPorCategoria[categoria.nombre] ?: 0.0
            val porcentaje = (gastado / categoria.presupuesto) * 100

            when {
                porcentaje >= 100 -> alertas.add(
                    AlertItem(
                        mensaje = "${categoria.nombre}: presupuesto excedido (${String.format("%.0f", porcentaje)}%)",
                        tipo = TipoAlerta.ERROR,
                        categoria = categoria.nombre
                    )
                )
                porcentaje >= 80 -> alertas.add(
                    AlertItem(
                        mensaje = "${categoria.nombre}: ${String.format("%.0f", porcentaje)}% del presupuesto usado",
                        tipo = TipoAlerta.ADVERTENCIA,
                        categoria = categoria.nombre
                    )
                )
            }
        }

    return alertas
}

fun calcularResumenMes(movimientos: List<Movimiento>): Triple<Double, Double, Double> {
    val ingresos = movimientos
        .filter { it.tipo == TipoMovimiento.INGRESO }
        .sumOf { it.monto }

    val gastos = movimientos
        .filter { it.tipo == TipoMovimiento.GASTO }
        .sumOf { it.monto }

    val saldo = ingresos - gastos
    return Triple(ingresos, gastos, saldo)
}

fun calcularPorcentajeGastos(
    movimientos: List<Movimiento>,
    categorias: List<Categoria>
): Int {
    val totalPresupuesto = categorias.sumOf { it.presupuesto }
    if (totalPresupuesto == 0.0) return 0

    val totalGastos = movimientos
        .filter { it.tipo == TipoMovimiento.GASTO }
        .sumOf { it.monto }

    return ((totalGastos / totalPresupuesto) * 100).toInt().coerceAtMost(100)
}