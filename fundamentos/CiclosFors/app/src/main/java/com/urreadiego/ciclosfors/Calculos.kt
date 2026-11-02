package com.urreadiego.ciclosfors

import kotlin.time.measureTime

class Resultado(
    val conteo: Long,
    val tiempoMicro: Long
)

/*
 - Dos bucles anidados donde ambos dependen totalmente de 'n'.
 - Produce n * n iteraciones. */
fun analizarCicloA(n: Int): Resultado {
    var contador = 0L
    val tiempo = measureTime {
        for (i in 0 until n) {
            for (j in 0 until n) {
                contador++ // Operación de costo unitario
            }
        }
    }
    return Resultado(contador, tiempo.inWholeMicroseconds)
}

/*
 - Aunque hay anidamiento, el bucle interno es constante.
 - El crecimiento solo depende del bucle externo 'n'. */
fun analizarCicloB(n: Int): Resultado {
    var contador = 0L
    val tiempo = measureTime {
        for (i in 0 until n) {
            for (j in 0 until 2) {
                contador++
            }
        }
    }
    return Resultado(contador, tiempo.inWholeMicroseconds)
}

/*
 - El bucle interno disminuye su tamaño según avanza 'i'. */
fun analizarCicloC(n: Int): Resultado {
    var contador = 0L
    val tiempo = measureTime {
        for (i in 0 until n) {
            for (j in n - 1 downTo i) {
                contador++
            }
        }
    }
    return Resultado(contador, tiempo.inWholeMicroseconds)
}

/*
 - CICLO D: Complejidad Cuadrática O(n²) en procesamiento
 - Los bucles recorren n²/2 aproximadamente. Aunque el contador solo sube, cuando j % i == 0, el procesador debe realizar todas las comparaciones. */
fun analizarCicloD(n: Int): Resultado {
    var contador = 0L
    val tiempo = measureTime {
        for (i in 1 until n) {
            for (j in 0 until i) {
                // Solo contamos si se cumple la condición (simula el output)
                if (j % i == 0) {
                    contador++
                }
            }
        }
    }
    return Resultado(contador, tiempo.inWholeMicroseconds)
}
