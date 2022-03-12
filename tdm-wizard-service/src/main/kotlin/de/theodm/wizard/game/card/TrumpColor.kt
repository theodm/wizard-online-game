package de.theodm.wizard.game.card

import de.theodm.wizard.card.CardColor
import de.theodm.wizard.card.toTrumpColor

enum class TrumpColor {
    Red,
    Yellow,
    Green,
    Blue,
    None;
}

fun TrumpColor.isEqualToCardColor(other: CardColor) =
    other.toTrumpColor() == this