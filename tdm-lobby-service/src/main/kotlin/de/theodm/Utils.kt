package de.theodm

import kotlin.random.Random

/**
 * Erstellt eine zufällige Zeichenfolge
 * aus den verfügbaren Zeichensatz [availableChars]
 * mit der Länge [length];
 */
fun randomString(
    availableChars: String,
    length: Int
): String {
    require(availableChars.length >= 2) { "availableChars.length >= 2" }
    require(length > 0) { "length > 0" }

    var resultStr = ""
    while (resultStr.length < length) {
        val charIndex = Random
            .Default
            .nextInt(0, availableChars.length - 1)

        resultStr += availableChars[charIndex]
    }

    return resultStr
}