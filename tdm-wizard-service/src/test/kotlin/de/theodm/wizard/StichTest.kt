package de.theodm.wizard

import com.google.common.truth.Truth
import de.theodm.wizard.card.*
import de.theodm.wizard.stich.PlayerMustPlayStichFarbeException
import de.theodm.wizard.stich.Stich
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@DisplayName("Stiche:")
class StichTest {

    fun Stich.assert(
            containsCardsInOrder: List<WizardCard>
    ) {
        Truth.assertThat(this.toList()).containsExactlyElementsIn(containsCardsInOrder).inOrder()
    }

    /**
     * Gibt für Testzwecke einen leeren Stich zurück. Domit ist der Startspieler.
     */
    fun emptyStich(): Stich {
        return Stich.emptyStich(
                players = Players(listOf(
                        wpSebastian,
                        wpTabea,
                        wpDomi
                )),
                startPlayer = wpDomi
        )
    }

    /**
     * Stich bei dem schon eine Karte gespielt wurde. Tabea ist nun an der Reihe.
     */
    fun stichOneCard(card: WizardCard): Stich {
        return emptyStich()
                .playCard(listOf(card), card);

    }

    /**
     * Stich bei dem schon zwei Karten gespielt wurden. Sebastian ist nun an der Reihe.
     */
    fun stichTwoCards(first: WizardCard, second: WizardCard): Stich {
        return emptyStich()
                .playCard(listOf(first), first)
                .playCard(listOf(second), second)

    }

    /**
     * Stich bei dem schon alle Karten gespielt wurden. Stich ist beendet. Domi ist Startspieler.
     */
    fun fullStich(first: WizardCard, second: WizardCard, third: WizardCard): Stich {
        return emptyStich()
                .playCard(listOf(first), first)
                .playCard(listOf(second), second)
                .playCard(listOf(third), third)
    }

    @Nested
    @DisplayName("Am Anfang des Stiches darf eine beliebige Karte gespielt werden:")
    inner class BeliebigeKarteAmAnfang {
        private val stich = emptyStich()

        // Es darf eine beliebige Karte gespielt werden, also
        // a) Farbkarte
        // b) Joker
        // c) Null

        @Test
        @DisplayName("a) Farbkarte")
        fun farbkarte() {
            val stich = stich.playCard(listOf(ccBlau13), ccBlau13)
            stich.assert(containsCardsInOrder = listOf(ccBlau13))
        }

        @Test
        @DisplayName("b) Zauberer")
        fun joker() {
            val stich = stich.playCard(listOf(Joker), Joker)
            stich.assert(containsCardsInOrder = listOf(Joker))
        }

        @Test
        @DisplayName("c) Narr")
        fun _null() {
            val stich = stich.playCard(listOf(Null), Null)
            stich.assert(containsCardsInOrder = listOf(Null))
        }
    }

    @Nested
    @DisplayName("Nachdem ein Zauberer gelegt wurde, darf eine beliebige Karte gespielt werden:")
    inner class BeliebigeKarteNachZauberer {
        private val stich = stichOneCard(Joker)

        // Es darf eine beliebige Karte gespielt werden, also
        // a) Farbkarte
        // b) Joker
        // c) Null

        @Test
        @DisplayName("a) Farbkarte")
        fun farbkarte() {
            val stich = stich.playCard(listOf(ccBlau13), ccBlau13)
            stich.assert(containsCardsInOrder = listOf(Joker, ccBlau13))
        }

        @Test
        @DisplayName("b) Zauberer")
        fun joker() {
            val stich = stich.playCard(listOf(Joker), Joker)
            stich.assert(containsCardsInOrder = listOf(Joker, Joker))
        }

        @Test
        @DisplayName("c) Narr")
        fun _null() {
            val stich = stich.playCard(listOf(Null), Null)
            stich.assert(containsCardsInOrder = listOf(Joker, Null))
        }
    }

    @Nested
    @DisplayName("Nachdem ein Narr gelegt wurde, darf eine beliebige Karte gespielt werden:")
    inner class BeliebigeKarteNachNarr {
        private val stich = stichOneCard(Null)

        // Es darf eine beliebige Karte gespielt werden, also
        // a) Farbkarte
        // b) Joker
        // c) Null

        @Test
        @DisplayName("a) Farbkarte")
        fun farbkarte() {
            val stich = stich.playCard(listOf(ccBlau13), ccBlau13)
            stich.assert(containsCardsInOrder = listOf(Null, ccBlau13))
        }

        @Test
        @DisplayName("b) Zauberer")
        fun joker() {
            val stich = stich.playCard(listOf(Joker), Joker)
            stich.assert(containsCardsInOrder = listOf(Null, Joker))
        }

        @Test
        @DisplayName("c) Narr")
        fun _null() {
            val stich = stich.playCard(listOf(Null), Null)
            stich.assert(containsCardsInOrder = listOf(Null, Null))
        }
    }

    @DisplayName("Nachdem eine Farbkarte gelegt wurde, muss der nachfolgende Spieler bekennen.")
    @Nested
    inner class BekennenNachFarbkarte {
        private val stich = stichOneCard(ccBlau13)

        // Der nachfolgende Spieler kann
        // a) eine Farbkarte der gleichen Farbe spielen
        // b) eine beliebige Karte Spielen, wenn er die Stichfarbe nicht mehr besitzt
        // c) einen Joker spielen, wenn er die Stichfarbe besitzt
        // d) einen Joker spielen, wenn er die Stichfarbe nicht besitzt
        // e) einen Narren spielen, wenn er die Stichfarbe besitzt
        // f) einen Narren spielen, wenn er die Stichfarbe nicht besitzt
        // g) Nicht eine Farbkarte einer anderen Farbe spielen, wenn er die Stichfarbe besitzt

        @Test
        @DisplayName("a) Farbkarte der gleichen Farbe")
        fun colorCardSameColor() {
            val stich = stich.playCard(listOf(ccBlau12), ccBlau12)
            stich.assert(containsCardsInOrder = listOf(ccBlau13, ccBlau12))
        }

        @Test
        @DisplayName("b) Eine beliebige Karte, wenn er die Stichfarbe nicht mehr besitzt")
        fun colorCardOtherColor() {
            val stich = stich.playCard(listOf(ccGruen3), ccGruen3)
            stich.assert(containsCardsInOrder = listOf(ccBlau13, ccGruen3))
        }

        @Test
        @DisplayName("c) Einen Joker, wenn er die Stichfarbe besitzt")
        fun jokerColorCardAvailable() {
            val stich = stich.playCard(listOf(ccBlau12, Joker), Joker)
            stich.assert(containsCardsInOrder = listOf(ccBlau13, Joker))
        }

        @Test
        @DisplayName("d) Einen Joker, wenn er die Stichfarbe nicht besitzt")
        fun jokerColorCardNotAvailable() {
            val stich = stich.playCard(listOf(ccGruen3, Joker), Joker)
            stich.assert(containsCardsInOrder = listOf(ccBlau13, Joker))
        }

        @Test
        @DisplayName("e) Einen Narren, wenn er die Stichfarbe besitzt")
        fun nullColorCardAvailable() {
            val stich = stich.playCard(listOf(ccBlau12, Null), Null)
            stich.assert(containsCardsInOrder = listOf(ccBlau13, Null))
        }

        @Test
        @DisplayName("f) Einen Narren, wenn er die Stichfarbe nicht besitzt")
        fun nullColorCardNotAvailable() {
            val stich = stich.playCard(listOf(ccGruen3, Null), Null)
            stich.assert(containsCardsInOrder = listOf(ccBlau13, Null))
        }

        @Test
        @DisplayName("g) Nicht eine Farbkarte einer anderen Farbe spielen, wenn er die Stichfarbe besitzt")
        fun notColorCardIfStichFarbe() {
            Assertions.assertThrows(PlayerMustPlayStichFarbeException::class.java) {
                stich.playCard(listOf(ccGruen3, ccBlau12), ccGruen3)
            }
        }
    }

    @DisplayName("Nachdem ein Narr gespielt worden ist und durch eine Farbkarte die Stichfarbe festgelegt wurde, muss der Spieler bekennen:")
    @Nested
    inner class BekennenNachNarrUndFarbkarte {
        private val stich = stichTwoCards(Null, ccBlau13)

        // Der nachfolgende Spieler kann
        // a) eine Farbkarte der gleichen Farbe spielen
        // b) eine beliebige Karte Spielen, wenn er die Stichfarbe nicht mehr besitzt
        // c) einen Joker spielen, wenn er die Stichfarbe besitzt
        // d) einen Joker spielen, wenn er die Stichfarbe nicht besitzt
        // e) einen Narren spielen, wenn er die Stichfarbe besitzt
        // f) einen Narren spielen, wenn er die Stichfarbe nicht besitzt
        // g) Nicht eine Farbkarte einer anderen Farbe spielen, wenn er die Stichfarbe besitzt

        @Test
        @DisplayName("a) Farbkarte der gleichen Farbe")
        fun colorCardSameColor() {
            val stich = stich.playCard(listOf(ccBlau12), ccBlau12)
            stich.assert(containsCardsInOrder = listOf(Null, ccBlau13, ccBlau12))
        }

        @Test
        @DisplayName("b) Eine beliebige Karte, wenn er die Stichfarbe nicht mehr besitzt")
        fun colorCardOtherColor() {
            val stich = stich.playCard(listOf(ccGruen3), ccGruen3)
            stich.assert(containsCardsInOrder = listOf(Null, ccBlau13, ccGruen3))
        }

        @Test
        @DisplayName("c) Einen Joker, wenn er die Stichfarbe besitzt")
        fun jokerColorCardAvailable() {
            val stich = stich.playCard(listOf(ccBlau12, Joker), Joker)
            stich.assert(containsCardsInOrder = listOf(Null, ccBlau13, Joker))
        }

        @Test
        @DisplayName("d) Einen Joker, wenn er die Stichfarbe nicht besitzt")
        fun jokerColorCardNotAvailable() {
            val stich = stich.playCard(listOf(ccGruen3, Joker), Joker)
            stich.assert(containsCardsInOrder = listOf(Null, ccBlau13, Joker))
        }

        @Test
        @DisplayName("e) Einen Narren, wenn er die Stichfarbe besitzt")
        fun nullColorCardAvailable() {
            val stich = stich.playCard(listOf(ccBlau12, Null), Null)
            stich.assert(containsCardsInOrder = listOf(Null, ccBlau13, Null))
        }

        @Test
        @DisplayName("f) Einen Narren, wenn er die Stichfarbe nicht besitzt")
        fun nullColorCardNotAvailable() {
            val stich = stich.playCard(listOf(ccGruen3, Null), Null)
            stich.assert(containsCardsInOrder = listOf(Null, ccBlau13, Null))
        }

        @Test
        @DisplayName("g) Nicht eine Farbkarte einer anderen Farbe spielen, wenn er die Stichfarbe besitzt")
        fun notColorCardIfStichFarbe() {
            Assertions.assertThrows(PlayerMustPlayStichFarbeException::class.java) {
                stich.playCard(listOf(ccGruen3, ccBlau12), ccGruen3)
            }
        }
    }

    @DisplayName("Nachdem ein Zauberer und dann eine Farbkarte gespielt worden ist muss der Spieler nicht mehr bekennen:")
    @Nested
    inner class NichtBekennenNachZaubererUndFarbkarte {
        private val stich = stichTwoCards(Joker, Null)

        @Test
        @DisplayName("a) Farbkarte")
        fun farbkarte() {
            val stich = stich.playCard(listOf(ccBlau13), ccBlau13)
            stich.assert(containsCardsInOrder = listOf(Joker, Null, ccBlau13))
        }

        @Test
        @DisplayName("b) Zauberer")
        fun joker() {
            val stich = stich.playCard(listOf(Joker), Joker)
            stich.assert(containsCardsInOrder = listOf(Joker, Null, Joker))
        }

        @Test
        @DisplayName("c) Narr")
        fun _null() {
            val stich = stich.playCard(listOf(Null), Null)
            stich.assert(containsCardsInOrder = listOf(Joker, Null, Null))
        }
    }

    @DisplayName("4 Spieler: Nach Narr, Zauberer, Narr muss der Spieler nicht mehr bekennen:")
    @Nested
    inner class NarrZaubererNarr {
        val stich = Stich.emptyStich(
                players = Players(listOf(
                        wpSebastian,
                        wpTabea,
                        wpDomi,
                        wpMarcel
                )),
                startPlayer = wpDomi
        ).playCard(listOf(Null), Null)
                .playCard(listOf(Joker), Joker)
                .playCard(listOf(Null), Null)

        @Test
        @DisplayName("a) Farbkarte")
        fun farbkarte() {
            val stich = stich.playCard(listOf(ccBlau13), ccBlau13)
            stich.assert(containsCardsInOrder = listOf(Null, Joker, Null, ccBlau13))
        }

        @Test
        @DisplayName("b) Zauberer")
        fun joker() {
            val stich = stich.playCard(listOf(Joker), Joker)
            stich.assert(containsCardsInOrder = listOf(Null, Joker, Null, Joker))
        }

        @Test
        @DisplayName("c) Narr")
        fun _null() {
            val stich = stich.playCard(listOf(Null), Null)
            stich.assert(containsCardsInOrder = listOf(Null, Joker, Null, Null))
        }
    }

    @DisplayName("Wenn der Stich noch nicht zu Ende ist, gibt winningPlayer() null zurück.")
    @Test
    fun stichNochNichtZuEnde() {
        Truth.assertThat(emptyStich().winningPlayer(TrumpColor.Blue)).isNull()
        Truth.assertThat(stichOneCard(ccBlau11).winningPlayer(TrumpColor.Blue)).isNull()
        Truth.assertThat(stichTwoCards(ccBlau11, ccBlau12).winningPlayer(TrumpColor.Blue)).isNull()
        Truth.assertThat(fullStich(ccBlau11, ccBlau12, ccBlau13).winningPlayer(TrumpColor.Blue)).isNotNull()
    }

    companion object {
        private val wpSebastian = TestWizardPlayer("Sebastian")
        private val wpTabea = TestWizardPlayer("Tabea")
        private val wpDomi = TestWizardPlayer("Domi")
        private val wpMarcel = TestWizardPlayer("Marcel")

        val ccBlau13 = NumberColorCard(CardColor.Blue, 13)
        val ccBlau12 = NumberColorCard(CardColor.Blue, 12)
        val ccBlau11 = NumberColorCard(CardColor.Blue, 11)

        val ccGruen3 = NumberColorCard(CardColor.Green, 3)
        val ccGruen5 = NumberColorCard(CardColor.Green, 5)

        data class ThreePlayersCase(
                val name: String,
                val trumpColor: TrumpColor,
                val stich: List<WizardCard>,
                val expectedWinner: TestWizardPlayer
        )

        @JvmStatic
        // Reihenfolge: Domi, Sebastian, Tabea
        fun winners3Players() = listOf(
                ThreePlayersCase("Farbstich: Höchste Farbe (zuerst gespielt) gewinnt", TrumpColor.Yellow, listOf(ccBlau13, ccBlau12, ccGruen3), wpDomi),
                ThreePlayersCase("Farbstich: Höchste Farbe (überstochen) gewinnt", TrumpColor.Yellow, listOf(ccBlau12, ccBlau13, ccGruen3), wpSebastian),
                ThreePlayersCase("Farbstich abgestochen: Einziger Trumpf gewinnt", TrumpColor.Green, listOf(ccBlau12, ccBlau13, ccGruen3), wpTabea),
                ThreePlayersCase("Farbstich abgestochen: Trumpf wird übertrumpft", TrumpColor.Green, listOf(ccBlau12, ccGruen3, ccGruen5), wpTabea),
                ThreePlayersCase("Farbstich abgestochen: Trumpf bleibt unter erstem Trumpf", TrumpColor.Green, listOf(ccBlau12, ccGruen5, ccGruen3), wpSebastian),
                ThreePlayersCase("Farbstich abgestochen: Zauberer übersticht Trumpf", TrumpColor.Green, listOf(ccBlau12, ccGruen5, Joker), wpTabea),
                ThreePlayersCase("Farbstich abgestochen: Null bleibt unter Trumpf", TrumpColor.Green, listOf(ccBlau12, ccGruen5, Null), wpSebastian),

                ThreePlayersCase("Trumpfstich: Alle werfen ab", TrumpColor.Blue, listOf(ccBlau12, ccGruen5, Null), wpDomi),
                ThreePlayersCase("Trumpfstich: Höherer Trumpf gewinnt", TrumpColor.Blue, listOf(ccBlau12, ccBlau13, Null), wpSebastian),
                ThreePlayersCase("Trumpfstich: Joker gewinnt", TrumpColor.Blue, listOf(ccBlau12, ccBlau13, Joker), wpTabea),

                ThreePlayersCase("Joker: Erster Joker gewinnt", TrumpColor.Blue, listOf(Joker, Joker, Joker), wpDomi),
                ThreePlayersCase("Joker: Farbe nach Joker verliert", TrumpColor.Blue, listOf(ccGruen5, Joker, ccBlau13), wpSebastian),

                ThreePlayersCase("Narren: Nur Narren: Erster Narr gewinnt", TrumpColor.Blue, listOf(Null, Null, Null), wpDomi),
                ThreePlayersCase("Narren: Nur Narren: Erste Farbe gewinnt", TrumpColor.Blue, listOf(Null, Null, ccGruen5), wpTabea),
        )

    }

    @ParameterizedTest
    @DisplayName("")
    @MethodSource("winners3Players")
    fun testWinners3Players(testCase: ThreePlayersCase) {
        val stich = fullStich(testCase.stich[0], testCase.stich[1], testCase.stich[2])

        Truth.assertThat(stich.winningPlayer(testCase.trumpColor)).isEqualTo(testCase.expectedWinner)
    }


}