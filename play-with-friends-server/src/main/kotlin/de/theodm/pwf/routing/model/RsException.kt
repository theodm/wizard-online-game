package de.theodm.pwf.routing.model

data class RsException(
    val type: String,
    val message: String
)

fun Exception.toRsException() = RsException(
    this.javaClass.simpleName,
    this.message ?: "Keine Fehlermeldung hinterlegt :("
)