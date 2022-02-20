package de.theodm.wizard

import de.theodm.wizard.card.CardColor

enum class TrumpColor {
    Red,
    Yellow,
    Green,
    Blue,
    None;

    fun isEqualToCardColor(other: CardColor): Boolean {
        return other.trumpColor == this
    }
}