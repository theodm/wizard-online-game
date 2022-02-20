package de.theodm.wizard.stich

import de.theodm.wizard.card.CardColor

enum class StichColor {
    /**
     * Eine beliebige Karte kann gespielt werden,
     * weil bereits ein Joker gespielt wurde, oder die
     * Farbe noch nicht ausgewählt wurde. Beispielsweise wenn
     * noch keine Karte im Stich liegt oder bisher nur Narren
     * gespielt wurden.
     */
    Any,

    /**
     * Es muss mit Rot bekannt werden.
     */
    Red,

    /**
     * Es muss mit Gelb bekannt werden.
     */
    Yellow,

    /**
     * Es muss mit Grün bekannt werden.
     */
    Green,

    /**
     * Es muss mit Blau bekannt werden.
     */
    Blue;

    companion object {
        fun fromCardColor(
                cardColor: CardColor
        ): StichColor {
            return when (cardColor) {
                CardColor.Green -> Green
                CardColor.Red -> Red
                CardColor.Yellow -> Yellow
                CardColor.Blue -> Blue
            }
        }
    }
}