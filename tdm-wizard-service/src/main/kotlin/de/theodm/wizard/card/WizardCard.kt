package de.theodm.wizard.card

import de.theodm.wizard.TrumpColor

sealed class WizardCard {
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

    abstract fun id(): String

    open fun isJoker(): Boolean = false
    open fun isNull(): Boolean = false
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

    override fun isJoker() = true

    override fun isHigherThan(previousCard: WizardCard, trumpColor: TrumpColor): Boolean {
        // Ein Joker ist nur dann nicht höher, wenn bereits ein Joker gespielt wurde.
        if (previousCard is Joker) {
            return false
        }

        // Ansonsten schlägt er alles!
        return true;
    }

    override fun trumpColor() = null
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

    override fun isNull(): Boolean = true

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
 * Farbkarte mit der  Farbe [color]
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

    override fun trumpColor() = color.trumpColor
    override fun id() = "" + color.name.first() + number
}