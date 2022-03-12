package de.theodm.wizard.card

import de.theodm.wizard.game.card.TrumpColor
import de.theodm.wizard.game.card.isEqualToCardColor

fun wizardCardOf(str: String) = when (str) {
    "J1" -> Joker("J1")
    "J2" -> Joker("J2")
    "J3" -> Joker("J3")
    "J4" -> Joker("J4")
    "N1" -> Null("N1")
    "N2" -> Null("N2")
    "N3" -> Null("N3")
    "N4" -> Null("N4")
    "Y1" -> NumberColorCard(CardColor.Yellow, 1)
    "Y2" -> NumberColorCard(CardColor.Yellow, 2)
    "Y3" -> NumberColorCard(CardColor.Yellow, 3)
    "Y4" -> NumberColorCard(CardColor.Yellow, 4)
    "Y5" -> NumberColorCard(CardColor.Yellow, 5)
    "Y6" -> NumberColorCard(CardColor.Yellow, 6)
    "Y7" -> NumberColorCard(CardColor.Yellow, 7)
    "Y8" -> NumberColorCard(CardColor.Yellow, 8)
    "Y9" -> NumberColorCard(CardColor.Yellow, 9)
    "Y10" -> NumberColorCard(CardColor.Yellow, 10)
    "Y11" -> NumberColorCard(CardColor.Yellow, 11)
    "Y12" -> NumberColorCard(CardColor.Yellow, 12)
    "Y13" -> NumberColorCard(CardColor.Yellow, 13)
    "R1" -> NumberColorCard(CardColor.Red, 1)
    "R2" -> NumberColorCard(CardColor.Red, 2)
    "R3" -> NumberColorCard(CardColor.Red, 3)
    "R4" -> NumberColorCard(CardColor.Red, 4)
    "R5" -> NumberColorCard(CardColor.Red, 5)
    "R6" -> NumberColorCard(CardColor.Red, 6)
    "R7" -> NumberColorCard(CardColor.Red, 7)
    "R8" -> NumberColorCard(CardColor.Red, 8)
    "R9" -> NumberColorCard(CardColor.Red, 9)
    "R10" -> NumberColorCard(CardColor.Red, 10)
    "R11" -> NumberColorCard(CardColor.Red, 11)
    "R12" -> NumberColorCard(CardColor.Red, 12)
    "R13" -> NumberColorCard(CardColor.Red, 13)
    "G1" -> NumberColorCard(CardColor.Green, 1)
    "G2" -> NumberColorCard(CardColor.Green, 2)
    "G3" -> NumberColorCard(CardColor.Green, 3)
    "G4" -> NumberColorCard(CardColor.Green, 4)
    "G5" -> NumberColorCard(CardColor.Green, 5)
    "G6" -> NumberColorCard(CardColor.Green, 6)
    "G7" -> NumberColorCard(CardColor.Green, 7)
    "G8" -> NumberColorCard(CardColor.Green, 8)
    "G9" -> NumberColorCard(CardColor.Green, 9)
    "G10" -> NumberColorCard(CardColor.Green, 10)
    "G11" -> NumberColorCard(CardColor.Green, 11)
    "G12" -> NumberColorCard(CardColor.Green, 12)
    "G13" -> NumberColorCard(CardColor.Green, 13)
    "B1" -> NumberColorCard(CardColor.Blue, 1)
    "B2" -> NumberColorCard(CardColor.Blue, 2)
    "B3" -> NumberColorCard(CardColor.Blue, 3)
    "B4" -> NumberColorCard(CardColor.Blue, 4)
    "B5" -> NumberColorCard(CardColor.Blue, 5)
    "B6" -> NumberColorCard(CardColor.Blue, 6)
    "B7" -> NumberColorCard(CardColor.Blue, 7)
    "B8" -> NumberColorCard(CardColor.Blue, 8)
    "B9" -> NumberColorCard(CardColor.Blue, 9)
    "B10" -> NumberColorCard(CardColor.Blue, 10)
    "B11" -> NumberColorCard(CardColor.Blue, 11)
    "B12" -> NumberColorCard(CardColor.Blue, 12)
    "B13" -> NumberColorCard(CardColor.Blue, 13)
    else -> throw IllegalStateException("$str ist keine gültige Karten-ID.")
}

sealed class WizardCard {
    abstract fun id(): String

    /**
     * Gibt an, ob diese Karte höherwertig ist,
     * als [previousCard] unter Berücksichtigung
     * der Trumpfkarte [trumpCard]. Die Stichfarbe wird implizit
     * durch Vergleich mit der letzthöheren Karte berücksichtigt.
     *
     * Dabei wird davon ausgegangen, dass [previousCard] vor dieser Karte
     * gespielt wurde. Dies ist relevant, bei
     */
    abstract fun isHigherThan(
            previousCard: WizardCard,
            trumpColor: TrumpColor
    ): Boolean

    /**
     * Gibt an, welche Trumpffarbe diese Karte auslöst,
     * wenn sie als Trumpfkarte verwendet wird.
     *
     * null bedeutet, dass die Farbe
     */
    abstract fun trumpColor(): TrumpColor?
}

/**
 * Joker-Karte
 */
data class Joker(
    private val id: String
) : WizardCard(

) {
    init {
        require(id in listOf("J1", "J2", "J3", "J4")) { "Die ID eines Jokers muss J1, J2, J3 oder J4 sein. War: $id." }
    }

    override fun isHigherThan(previousCard: WizardCard, trumpColor: TrumpColor): Boolean {
        // Ein Joker ist nur dann nicht höher, wenn bereits ein Joker gespielt wurde.
        if (previousCard is Joker) {
            return false
        }

        // Ansonsten schlägt er alles!
        return true;
    }

    override fun trumpColor(): TrumpColor? = null
    override fun id() = id

    companion object {
        val J1 = Joker("J1")
        val J2 = Joker("J2")
        val J3 = Joker("J3")
        val J4 = Joker("J4")
    }
}

/**
 * Narr-Karte
 */
data class Null(
    private val id: String
) : WizardCard() {
    init {
        require(id in listOf("N1", "N2", "N3", "N4")) { "Die ID eines Narren muss N1, N2, N3 oder N4 sein." }
    }

    // Ein Narr schlägt keine anderen Karten.
    override fun isHigherThan(previousCard: WizardCard, trumpColor: TrumpColor) =
            false

    override fun trumpColor() = TrumpColor.None
    override fun id() = id

    companion object {
        val N1 = Null("N1")
        val N2 = Null("N2")
        val N3 = Null("N3")
        val N4 = Null("N4")
    }
}


/**
 * Farbkarte mit der Farbe [color]
 * und dem Zahlenwert [number].
 */
data class NumberColorCard(
        val color: CardColor,
        val number: Int
) : WizardCard() {
    init {
        require(number in 1..13) { "Eine Wizard-Karte kann nur einen Wert zwischen 1 und 13 haben. (erhalten: $number)" }
    }

    override fun isHigherThan(
            previousCard: WizardCard,
            trumpColor: TrumpColor
    ): Boolean {
        val currentCard = this

        // Alle Farbkarten sind immer niederwertiger als ein bereits gespielter Joker.
        if (previousCard is Joker) {
            return false
        }

        // Alle Farbkarten sind höherwertiger als Narren.
        if (previousCard is Null) {
            return true;
        }

        require(previousCard is NumberColorCard)

        val previousIsTrump = trumpColor.isEqualToCardColor(previousCard.color)
        val currentIsTrump = trumpColor.isEqualToCardColor(currentCard.color)

        val currentNumberIsHigher = currentCard.number > previousCard.number
        val currentColorIsSameAsPrevious = currentCard.color == previousCard.color

        // Eine Farbkarte ist höherwertig als ein Trumpf, nur dann, wenn sie
        // selber ein höherer Trumpf ist
        if (previousIsTrump && currentIsTrump && currentNumberIsHigher) {
            return true
        }

        // Trumpf ist höher als eine Farbkarte.
        if (!previousIsTrump && currentIsTrump) {
            return true
        }

        // Eine Farbkarte ist höherwertig, als eine andere, wenn sie von der gleichen
        // Farbe ist und eine höhere Nummer enthält.
        if (!previousIsTrump && currentColorIsSameAsPrevious && currentNumberIsHigher) {
            return true;
        }

        return false;
    }

    override fun trumpColor() = color.toTrumpColor()
    override fun id() = "" + color.name.first() + number
}