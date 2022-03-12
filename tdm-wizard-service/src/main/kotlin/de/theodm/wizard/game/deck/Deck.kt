package de.theodm.wizard.game.deck

import de.theodm.wizard.ImmutableResult1
import de.theodm.wizard.card.*
import java.util.Random

/**
 * Abstraktion für einen Kartenstapel.
 */
data class Deck private constructor (
        val origin: List<WizardCard>
) {
    companion object {
        /**
         * Konstante, die ein komplettes Wizard-Deck (alle 60 Karten)
         * in einer Liste angibt.
         */
        private val wizardDeck = listOf(
                Joker.J1,
                Joker.J2,
                Joker.J3,
                Joker.J4,
                Null.N1,
                Null.N2,
                Null.N3,
                Null.N4,
                *listOf(
                        CardColor.Blue,
                        CardColor.Yellow,
                        CardColor.Green,
                        CardColor.Red
                ).flatMap { color ->
                    (1..13).map { nr -> NumberColorCard(color, nr) }
                }.toTypedArray()
        )

        /**
         * Nur für Testzwecke aufrufen.
         */
        fun from(vararg cards: WizardCard): Deck {
            return Deck(cards.toList())
        }

        /**
         * Gibt ein neues Deck in beliebiger Reihenfolge zurück.
         */
        fun shuffled(random: Random = Random()): Deck {
            val shuffledCards: List<WizardCard> = wizardDeck.shuffled(random)

            return Deck(shuffledCards)
        }
    }

    fun hasCards() = origin.isNotEmpty()

    /**
     * Zieht [numberOfCards] vom Stapel herunter.
     */
    fun drawCards(numberOfCards: Int): ImmutableResult1<Deck, List<WizardCard>> {
        require(numberOfCards > 0) { "Es kann keine negative Anzahl von Karten ($numberOfCards) vom Deck gezogen werden." }
        require(numberOfCards <= origin.size) { "Es sollen $numberOfCards Karten vom Deck gezogen werden. Das Deck hat jedoch nur noch ${origin.size} verbleibende Karten." }

        val drawnCards = origin
                .takeLast(numberOfCards)

        return ImmutableResult1(
            thisObject = copy(
                origin = origin.dropLast(numberOfCards)
            ),
            returnValue1 = drawnCards
        )
    }
}