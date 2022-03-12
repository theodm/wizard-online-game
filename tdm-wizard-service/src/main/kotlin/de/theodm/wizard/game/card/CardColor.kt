package de.theodm.wizard.card

import de.theodm.wizard.game.card.TrumpColor

enum class CardColor {
    Red,
    Yellow,
    Green,
    Blue
}

fun CardColor.toTrumpColor() = when (this) {
    CardColor.Red -> TrumpColor.Red
    CardColor.Yellow -> TrumpColor.Yellow
    CardColor.Green -> TrumpColor.Green
    CardColor.Blue -> TrumpColor.Blue
}