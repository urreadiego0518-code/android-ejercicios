package com.urreadiego.calculadoraapp.Unidad_1

class Calculadora {
    fun suma(a: Double, b: Double): Double {
        return a + b
    }

    fun resta(a: Double, b: Double): Double {
        return a - b
    }

    fun multiplicacion(a: Double, b: Double): Double {
        return a * b
    }

    fun division(a: Double, b: Double): Double {
        return if (b != 0.0) {
            a / b
        } else {
            println("Error: No se permite la división entre cero")
            Double.NaN
        }
    }
}