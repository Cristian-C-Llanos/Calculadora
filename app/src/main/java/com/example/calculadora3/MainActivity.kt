package com.example.calculadora3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Calculadora()
        }
    }

    @Composable
    fun Calculadora() {
        var inputText by remember { mutableStateOf("") }
        var resultText by remember { mutableStateOf("") }

        Column(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = "$inputText\n\n$resultText",  // La segunda línea está vacía
                onValueChange = { inputText = it },  // Mantiene la entrada del usuario
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),  // Ajuste en la altura
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = LocalTextStyle.current.copy(fontSize = 50.sp, textAlign = TextAlign.End),  // Texto más grande y alineado a la derecha
                singleLine = false  // Permitir múltiples líneas
            )

            // Botones de números y operadores
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BotonCalculadora(texto = "7") { inputText += "7" }
                BotonCalculadora(texto = "8") { inputText += "8" }
                BotonCalculadora(texto = "9") { inputText += "9" }
                BotonCalculadora(texto = "/") { inputText += "/" }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BotonCalculadora(texto = "4") { inputText += "4" }
                BotonCalculadora(texto = "5") { inputText += "5" }
                BotonCalculadora(texto = "6") { inputText += "6" }
                BotonCalculadora(texto = "*") { inputText += "*" }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BotonCalculadora(texto = "1") { inputText += "1" }
                BotonCalculadora(texto = "2") { inputText += "2" }
                BotonCalculadora(texto = "3") { inputText += "3" }
                BotonCalculadora(texto = "-") { inputText += "-" }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BotonCalculadora(texto = "0") { inputText += "0" }
                BotonCalculadora(texto = ".") { inputText += "." }
                BotonCalculadora(texto = "=") {
                    resultText = try {
                        evaluarExpresion(inputText).toString()
                    } catch (e: Exception) {
                        "Error"
                    }
                    inputText = resultText
                }
                BotonCalculadora(texto = "+") { inputText += "+" }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BotonCalculadora(texto = "AC") {
                    inputText = ""
                    resultText = ""
                }
                BotonCalculadora(texto = "(") { inputText += "(" }
                BotonCalculadora(texto = ")") { inputText += ")" }
                BotonCalculadora(texto = "←") {
                    if (inputText.isNotEmpty()) {  // Verifica que haya algo que eliminar
                        inputText = inputText.dropLast(1)  // Eliminar el último carácter
                    }
                }
            }
        }
    }

    // Función para evaluar la expresión aritmética
    fun evaluarExpresion(expresion: String): Double {
        val operadores = Stack<Char>()
        val valores = Stack<Double>()

        var i = 0
        while (i < expresion.length) {
            val c = expresion[i]

            when {
                c.isWhitespace() -> i++ // Ignorar espacios
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < expresion.length && (expresion[i].isDigit() || expresion[i] == '.')) {
                        sb.append(expresion[i])
                        i++
                    }
                    valores.push(sb.toString().toDouble())
                    continue
                }
                c == '(' -> operadores.push(c)
                c == ')' -> {
                    while (operadores.peek() != '(') {
                        valores.push(operar(operadores.pop(), valores.pop(), valores.pop()))
                    }
                    operadores.pop()
                }
                c == '+' || c == '-' || c == '*' || c == '/' -> {
                    while (!operadores.isEmpty() && precedencia(c) <= precedencia(operadores.peek())) {
                        valores.push(operar(operadores.pop(), valores.pop(), valores.pop()))
                    }
                    operadores.push(c)
                }
            }
            i++
        }

        while (!operadores.isEmpty()) {
            valores.push(operar(operadores.pop(), valores.pop(), valores.pop()))
        }

        return valores.pop()
    }

    // Función que determina la precedencia de los operadores
    fun precedencia(operador: Char): Int {
        return when (operador) {
            '+', '-' -> 1
            '*', '/' -> 2
            else -> 0
        }
    }

    // Función que aplica una operación
    fun operar(operador: Char, b: Double, a: Double): Double {
        return when (operador) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> a / b
            else -> 0.0
        }
    }
}

@Composable
fun BotonCalculadora(texto: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors( Color(0xFF0028EE)), // Cambiar el color del botón
        modifier = Modifier
            .size(100.dp) // Tamaño del botón cuadrado
            .padding(5.dp), // Espaciado entre botones
        shape = RoundedCornerShape(16.dp) // Borde redondeado
    ) {
        Text(text = texto, fontSize = 30.sp) // Ajustar el tamaño de la fuente aquí
    }
}
