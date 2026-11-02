# Ejercicios de Android con Jetpack Compose

> Trece mini apps hechas durante la materia de Estructura de Datos. Cada una
> resuelve un ejercicio distinto: desde el "Hola Mundo" hasta un Dijkstra
> animado paso a paso. Todas con Kotlin y Jetpack Compose.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-API%2024%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Licencia](https://img.shields.io/badge/Licencia-MIT-green.svg)](LICENSE)

---

## Qué hay aquí

Están agrupadas por tema, no por número de unidad. Cada carpeta es un proyecto
de Android Studio independiente: se abre sola y se corre sola.

### `fundamentos/` — lo primero

| App | Qué hace |
|---|---|
| `HelloWorld` | El clásico primer proyecto en Compose. Puro texto y tema. |
| `CalculadoraApp` | Pide un número y calcula algo con él. Práctica de estado y `TextField`. |
| `CiclosFors` | Mide cuántas veces corre un ciclo y cuánto tarda, para comparar complejidad de algoritmos. |
| `Persona` | Lista de personas usando una `data class` y estado de Compose. |

### `estructuras-datos/` — árboles y grafos

| App | Qué hace |
|---|---|
| `Arboles` | Recorridos **inorden, preorden y postorden** de una expresión aritmética, mostrados en pantalla. |
| `CaminoCorto` | **Dijkstra** visualizado paso a paso sobre un grafo. |
| `OrdenArbol` | Árbol binario de búsqueda: agregar números, ordenarlos y limpiar. |

### `persistencia/` — guardar datos de verdad

| App | Qué hace |
|---|---|
| `RoomDemo12` | CRUD con **Room**: agregar, buscar, borrar y limpiar registros. |
| `RoomPersona` | Room + **huella digital**: guarda y busca personas, con login biométrico antes de escribir. |

### `ordenamiento/` — algoritmos visualizados

| App | Qué hace |
|---|---|
| `MetodoBurbuja` | Ordenamiento burbuja con el arreglo a la vista. |
| `QuickSort` | QuickSort visualizado. |
| `RadixSort` | RadixSort mostrando las **cubetas del 0 al 9** en cada pasada. |
| `ShellSort` | ShellSort paso a paso. |

Todas las de ordenamiento tienen un botón para mezclar el arreglo y ver el
proceso desde el inicio.

---

## Cómo abrir cualquiera

Cada carpeta es un proyecto completo con su propio `gradlew` y su
`settings.gradle.kts`. No hay proyecto "raíz" que las agrupe, así que se abren
una por una:

```bash
# 1. Clona el repo
git clone https://github.com/urreadiego0518-code/android-ejercicios.git
cd android-ejercicios

# 2. Abre la que quieras en Android Studio
#    File > Open > elige la carpeta, por ejemplo ordenamiento/QuickSort

# 3. O compílala desde la terminal
cd ordenamiento/QuickSort
./gradlew assembleDebug
```

**Necesitas:** Android Studio (Ladybug o superior), JDK 17 y Android SDK 36.

> Las apps de `persistencia/RoomPersona` piden huella digital. Si tu emulador no
> tiene sensor, regístrala en la configuración del emulador
> (*Settings > Security > Fingerprint*) o pruébalo en un teléfono físico.

---

## Estructura

```
android-ejercicios/
├── fundamentos/
│   ├── HelloWorld/
│   ├── CalculadoraApp/
│   ├── CiclosFors/
│   └── Persona/
├── estructuras-datos/
│   ├── Arboles/
│   ├── CaminoCorto/
│   └── OrdenArbol/
├── persistencia/
│   ├── RoomDemo12/
│   └── RoomPersona/
├── ordenamiento/
│   ├── MetodoBurbuja/
│   ├── QuickSort/
│   ├── RadixSort/
│   └── ShellSort/
├── LICENSE
└── README.md
```

---

## Detalles técnicos

- Paquetes renombrados a `com.urreadiego.<app>`.
- **Jetpack Compose** con Material 3 en todas.
- **Room** en las de persistencia (`persistencia/`).
- **BiometricPrompt** en `RoomPersona`.
- Algoritmos escritos a mano, sin librerías de terceros.
- Gradle Kotlin DSL y version catalog (`gradle/libs.versions.toml`).

---

## Lo que le falta

- Las apps de `fundamentos/` son ejercicios de clase, así que la interfaz es
  mínima: lo importante ahí es la lógica.
- No hay pruebas unitarias. Varias de estas apps se prestan para agregarlas
  (los ordenamientos y el Dijkstra, por ejemplo).
- Algunos textos y colores están hardcodeados en la pantalla en vez de vivir en
  un archivo de recursos.

Si quieres mejorar alguna, abre un issue o mándame el PR.

---

## Licencia

MIT. Úsalo, modifícalo y compártelo. Ver [LICENSE](LICENSE).

---

<div align="center">
  <sub>Hecho por <a href="https://github.com/urreadiego0518-code">Diego Urrea</a></sub>
</div>
