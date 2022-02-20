package de.theodm.wizard

import com.google.common.truth.Truth
import de.theodm.wizard.card.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class DeckTest {
    @DisplayName("In einem vollen Deck sind alle Wizard-Karten enthalten: ")
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class FullDeck {
        private val deck = Deck.shuffled()

        private fun deckContains(
                card: WizardCard
        ) = deckContains(card, 1)

        private fun deckContains(
                card: WizardCard,
                numberOfTimes: Int
        ) {
            val (remainingDeck, drawnCards) = deck.drawCards(60)

            Truth.assertThat(drawnCards.filter { it == card }.size).isEqualTo(numberOfTimes)
        }

        @Test
        @DisplayName("Enthält genau 60 Karten.")
        fun enthaeltGenau60Karten() {
            // Ein Deck enthält genau 60 Karten
            val (remainingDeck, drawnCards) = deck.drawCards(60)

            Truth.assertThat(drawnCards).hasSize(60)

            // Danach ist es leer
            Assertions.assertThrows(IllegalArgumentException::class.java) {
                remainingDeck.drawCards(1)
            }
        }

        @Test
        @DisplayName("Enthält genau 4 Zauberer")
        fun enthaeltGenau4Joker() = deckContains(Joker, 4)

        @Test
        @DisplayName("Enthält genau 4 Narren")
        fun enthaeltGenau4Narren() = deckContains(Null, 4)


        @ParameterizedTest()
        @MethodSource("alleFarbkarten")
        @DisplayName("Enthält die Farbkarte: ")
        fun enthaeltFarbkarte(card: WizardCard) = deckContains(card)

        fun alleFarbkarten() = listOf(
                NumberColorCard(CardColor.Green, 1),
                NumberColorCard(CardColor.Green, 2),
                NumberColorCard(CardColor.Green, 3),
                NumberColorCard(CardColor.Green, 4),
                NumberColorCard(CardColor.Green, 5),
                NumberColorCard(CardColor.Green, 6),
                NumberColorCard(CardColor.Green, 7),
                NumberColorCard(CardColor.Green, 8),
                NumberColorCard(CardColor.Green, 9),
                NumberColorCard(CardColor.Green, 10),
                NumberColorCard(CardColor.Green, 11),
                NumberColorCard(CardColor.Green, 12),
                NumberColorCard(CardColor.Green, 13),
                NumberColorCard(CardColor.Yellow, 1),
                NumberColorCard(CardColor.Yellow, 2),
                NumberColorCard(CardColor.Yellow, 3),
                NumberColorCard(CardColor.Yellow, 4),
                NumberColorCard(CardColor.Yellow, 5),
                NumberColorCard(CardColor.Yellow, 6),
                NumberColorCard(CardColor.Yellow, 7),
                NumberColorCard(CardColor.Yellow, 8),
                NumberColorCard(CardColor.Yellow, 9),
                NumberColorCard(CardColor.Yellow, 10),
                NumberColorCard(CardColor.Yellow, 11),
                NumberColorCard(CardColor.Yellow, 12),
                NumberColorCard(CardColor.Yellow, 13),
                NumberColorCard(CardColor.Red, 1),
                NumberColorCard(CardColor.Red, 2),
                NumberColorCard(CardColor.Red, 3),
                NumberColorCard(CardColor.Red, 4),
                NumberColorCard(CardColor.Red, 5),
                NumberColorCard(CardColor.Red, 6),
                NumberColorCard(CardColor.Red, 7),
                NumberColorCard(CardColor.Red, 8),
                NumberColorCard(CardColor.Red, 9),
                NumberColorCard(CardColor.Red, 10),
                NumberColorCard(CardColor.Red, 11),
                NumberColorCard(CardColor.Red, 12),
                NumberColorCard(CardColor.Red, 13),
                NumberColorCard(CardColor.Blue, 1),
                NumberColorCard(CardColor.Blue, 2),
                NumberColorCard(CardColor.Blue, 3),
                NumberColorCard(CardColor.Blue, 4),
                NumberColorCard(CardColor.Blue, 5),
                NumberColorCard(CardColor.Blue, 6),
                NumberColorCard(CardColor.Blue, 7),
                NumberColorCard(CardColor.Blue, 8),
                NumberColorCard(CardColor.Blue, 9),
                NumberColorCard(CardColor.Blue, 10),
                NumberColorCard(CardColor.Blue, 11),
                NumberColorCard(CardColor.Blue, 12),
                NumberColorCard(CardColor.Blue, 13),

                )
    }

    @Test
    @DisplayName("Nachdem 4 Karten entfernt wurden, sind noch 56 Karten vorhanden.")
    fun test4Block() {
        val deck = Deck.shuffled()

        // Ein Deck enthält genau 60 Karten
        val (remainingDeck, drawnCards) = deck.drawCards(4)

        Truth.assertThat(drawnCards).hasSize(4)

        val (_remainingDeck, _) = remainingDeck.drawCards(56)

        // Jetzt ist es leer
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            _remainingDeck.drawCards(1)
        }
    }

}