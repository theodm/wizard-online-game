package de.theodm.wizard.game.stich

import com.google.common.truth.Truth
import de.theodm.wizard.game.TestWizardPlayer
import de.theodm.wizard.card.*
import de.theodm.wizard.game.card.TrumpColor
import de.theodm.wizard.game.players.Players
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@DisplayName("Stiche:")
class StichTest {
    val players = Players(
        listOf(
            wpSebastian,
            wpTabea,
            wpDomi
        )
    )

    fun Stich.assert(
        containsCardsInOrder: List<WizardCard>
    ) {
        Truth.assertThat(this.cards).containsExactlyElementsIn(containsCardsInOrder).inOrder()
    }

    /**
     * Gibt für Testzwecke einen leeren Stich zurück. Domi ist der Startspieler.
     */
    fun emptyStich(): Stich {
        return Stich.emptyStich(
            startPlayer = wpDomi
        )
    }

    /**
     * Stich bei dem schon eine Karte gespielt wurde. Tabea ist nun an der Reihe.
     */
    fun stichOneCard(card: WizardCard): Stich {
        return emptyStich()
            .playCard(players, listOf(card), card)

    }

    /**
     * Stich bei dem schon zwei Karten gespielt wurden. Sebastian ist nun an der Reihe.
     */
    fun stichTwoCards(first: WizardCard, second: WizardCard): Stich {
        return emptyStich()
            .playCard(players, listOf(first), first)
            .playCard(players, listOf(second), second)

    }

    /**
     * Stich bei dem schon alle Karten gespielt wurden. Stich ist beendet. Domi ist Startspieler.
     */
    private fun fullStich(first: WizardCard, second: WizardCard, third: WizardCard): Stich {
        return emptyStich()
            .playCard(players, listOf(first), first)
            .playCard(players, listOf(second), second)
            .playCard(players, listOf(third), third)
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
            val stich = stich.playCard(players, listOf(ccBlau13), ccBlau13)
            stich.assert(containsCardsInOrder = listOf(ccBlau13))
        }

        @Test
        @DisplayName("b) Zauberer")
        fun joker() {
            val stich = stich.playCard(players, listOf(Joker.J1), Joker.J1)
            stich.assert(containsCardsInOrder = listOf(Joker.J1))
        }

        @Test
        @DisplayName("c) Narr")
        fun _null() {
            val stich = stich.playCard(players, listOf(Null.N1), Null.N1)
            stich.assert(containsCardsInOrder = listOf(Null.N1))
        }
    }

    @Nested
    @DisplayName("Nachdem ein Zauberer gelegt wurde, darf eine beliebige Karte gespielt werden:")
    inner class BeliebigeKarteNachZauberer {
        private val stich = stichOneCard(Joker.J1)

        // Es darf eine beliebige Karte gespielt werden, also
        // a) Farbkarte
        // b) Joker
        // c) Null

        @Test
        @DisplayName("a) Farbkarte")
        fun farbkarte() {
            val stich = stich.playCard(players, listOf(ccBlau13), ccBlau13)
            stich.assert(containsCardsInOrder = listOf(Joker.J1, ccBlau13))
        }

        @Test
        @DisplayName("b) Zauberer")
        fun joker() {
            val stich = stich.playCard(players, listOf(Joker.J2), Joker.J2)
            stich.assert(containsCardsInOrder = listOf(Joker.J1, Joker.J2))
        }

        @Test
        @DisplayName("c) Narr")
        fun _null() {
            val stich = stich.playCard(players, listOf(Null.N1), Null.N1)
            stich.assert(containsCardsInOrder = listOf(Joker.J1, Null.N1))
        }
    }

    @Nested
    @DisplayName("Nachdem ein Narr gelegt wurde, darf eine beliebige Karte gespielt werden:")
    inner class BeliebigeKarteNachNarr {
        private val stich = stichOneCard(Null.N1)

        // Es darf eine beliebige Karte gespielt werden, also
        // a) Farbkarte
        // b) Joker
        // c) Null

        @Test
        @DisplayName("a) Farbkarte")
        fun farbkarte() {
            val stich = stich.playCard(players, listOf(ccBlau13), ccBlau13)
            stich.assert(containsCardsInOrder = listOf(Null.N1, ccBlau13))
        }

        @Test
        @DisplayName("b) Zauberer")
        fun joker() {
            val stich = stich.playCard(players, listOf(Joker.J1), Joker.J1)
            stich.assert(containsCardsInOrder = listOf(Null.N1, Joker.J1))
        }

        @Test
        @DisplayName("c) Narr")
        fun _null() {
            val stich = stich.playCard(players, listOf(Null.N2), Null.N2)
            stich.assert(containsCardsInOrder = listOf(Null.N1, Null.N2))
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
            val stich = stich.playCard(players, listOf(ccBlau12), ccBlau12)
            stich.assert(containsCardsInOrder = listOf(ccBlau13, ccBlau12))
        }

        @Test
        @DisplayName("b) Eine beliebige Karte, wenn er die Stichfarbe nicht mehr besitzt")
        fun colorCardOtherColor() {
            val stich = stich.playCard(players, listOf(ccGruen3), ccGruen3)
            stich.assert(containsCardsInOrder = listOf(ccBlau13, ccGruen3))
        }

        @Test
        @DisplayName("c) Einen Joker, wenn er die Stichfarbe besitzt")
        fun jokerColorCardAvailable() {
            val stich = stich.playCard(players, listOf(ccBlau12, Joker.J1), Joker.J1)
            stich.assert(containsCardsInOrder = listOf(ccBlau13, Joker.J1))
        }

        @Test
        @DisplayName("d) Einen Joker, wenn er die Stichfarbe nicht besitzt")
        fun jokerColorCardNotAvailable() {
            val stich = stich.playCard(players, listOf(ccGruen3, Joker.J1), Joker.J1)
            stich.assert(containsCardsInOrder = listOf(ccBlau13, Joker.J1))
        }

        @Test
        @DisplayName("e) Einen Narren, wenn er die Stichfarbe besitzt")
        fun nullColorCardAvailable() {
            val stich = stich.playCard(players, listOf(ccBlau12, Null.N1), Null.N1)
            stich.assert(containsCardsInOrder = listOf(ccBlau13, Null.N1))
        }

        @Test
        @DisplayName("f) Einen Narren, wenn er die Stichfarbe nicht besitzt")
        fun nullColorCardNotAvailable() {
            val stich = stich.playCard(players, listOf(ccGruen3, Null.N1), Null.N1)
            stich.assert(containsCardsInOrder = listOf(ccBlau13, Null.N1))
        }

        @Test
        @DisplayName("g) Nicht eine Farbkarte einer anderen Farbe spielen, wenn er die Stichfarbe besitzt")
        fun notColorCardIfStichFarbe() {
            Assertions.assertThrows(PlayerMustPlayStichFarbeException::class.java) {
                stich.playCard(players, listOf(ccGruen3, ccBlau12), ccGruen3)
            }
        }
    }

    @DisplayName("Nachdem ein Narr gespielt worden ist und durch eine Farbkarte die Stichfarbe festgelegt wurde, muss der Spieler bekennen:")
    @Nested
    inner class BekennenNachNarrUndFarbkarte {
        private val stich = stichTwoCards(Null.N1, ccBlau13)

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
            val stich = stich.playCard(players, listOf(ccBlau12), ccBlau12)
            stich.assert(containsCardsInOrder = listOf(Null.N1, ccBlau13, ccBlau12))
        }

        @Test
        @DisplayName("b) Eine beliebige Karte, wenn er die Stichfarbe nicht mehr besitzt")
        fun colorCardOtherColor() {
            val stich = stich.playCard(players, listOf(ccGruen3), ccGruen3)
            stich.assert(containsCardsInOrder = listOf(Null.N1, ccBlau13, ccGruen3))
        }

        @Test
        @DisplayName("c) Einen Joker, wenn er die Stichfarbe besitzt")
        fun jokerColorCardAvailable() {
            val stich = stich.playCard(players, listOf(ccBlau12, Joker.J1), Joker.J1)
            stich.assert(containsCardsInOrder = listOf(Null.N1, ccBlau13, Joker.J1))
        }

        @Test
        @DisplayName("d) Einen Joker, wenn er die Stichfarbe nicht besitzt")
        fun jokerColorCardNotAvailable() {
            val stich = stich.playCard(players, listOf(ccGruen3, Joker.J1), Joker.J1)
            stich.assert(containsCardsInOrder = listOf(Null.N1, ccBlau13, Joker.J1))
        }

        @Test
        @DisplayName("e) Einen Narren, wenn er die Stichfarbe besitzt")
        fun nullColorCardAvailable() {
            val stich = stich.playCard(players, listOf(ccBlau12, Null.N2), Null.N2)
            stich.assert(containsCardsInOrder = listOf(Null.N1, ccBlau13, Null.N2))
        }

        @Test
        @DisplayName("f) Einen Narren, wenn er die Stichfarbe nicht besitzt")
        fun nullColorCardNotAvailable() {
            val stich = stich.playCard(players, listOf(ccGruen3, Null.N2), Null.N2)
            stich.assert(containsCardsInOrder = listOf(Null.N1, ccBlau13, Null.N2))
        }

        @Test
        @DisplayName("g) Nicht eine Farbkarte einer anderen Farbe spielen, wenn er die Stichfarbe besitzt")
        fun notColorCardIfStichFarbe() {
            Assertions.assertThrows(PlayerMustPlayStichFarbeException::class.java) {
                stich.playCard(players, listOf(ccGruen3, ccBlau12), ccGruen3)
            }
        }
    }

    @DisplayName("Nachdem ein Zauberer und dann eine Farbkarte gespielt worden ist muss der Spieler nicht mehr bekennen:")
    @Nested
    inner class NichtBekennenNachZaubererUndFarbkarte {
        private val stich = stichTwoCards(Joker.J1, Null.N1)

        @Test
        @DisplayName("a) Farbkarte")
        fun farbkarte() {
            val stich = stich.playCard(players, listOf(ccBlau13), ccBlau13)
            stich.assert(containsCardsInOrder = listOf(Joker.J1, Null.N1, ccBlau13))
        }

        @Test
        @DisplayName("b) Zauberer")
        fun joker() {
            val stich = stich.playCard(players, listOf(Joker.J2), Joker.J2)
            stich.assert(containsCardsInOrder = listOf(Joker.J1, Null.N1, Joker.J2))
        }

        @Test
        @DisplayName("c) Narr")
        fun _null() {
            val stich = stich.playCard(players, listOf(Null.N2), Null.N2)
            stich.assert(containsCardsInOrder = listOf(Joker.J1, Null.N1, Null.N2))
        }
    }

    @DisplayName("4 Spieler: Nach Narr, Zauberer, Narr muss der Spieler nicht mehr bekennen:")
    @Nested
    inner class NarrZaubererNarr {
        private val stich = Stich.emptyStich(
            startPlayer = wpDomi
        ).playCard(players, listOf(Null.N1), Null.N1)
            .playCard(players, listOf(Joker.J1), Joker.J1)
            .playCard(players, listOf(Null.N2), Null.N2)

        @Test
        @DisplayName("a) Farbkarte")
        fun farbkarte() {
            val stich = stich.playCard(players, listOf(ccBlau13), ccBlau13)
            stich.assert(containsCardsInOrder = listOf(Null.N1, Joker.J1, Null.N2, ccBlau13))
        }

        @Test
        @DisplayName("b) Zauberer")
        fun joker() {
            val stich = stich.playCard(players, listOf(Joker.J2), Joker.J2)
            stich.assert(containsCardsInOrder = listOf(Null.N1, Joker.J1, Null.N2, Joker.J2))
        }

        @Test
        @DisplayName("c) Narr")
        fun _null() {
            val stich = stich.playCard(players, listOf(Null.N3), Null.N3)
            stich.assert(containsCardsInOrder = listOf(Null.N1, Joker.J1, Null.N2, Null.N3))
        }
    }

    @DisplayName("Wenn der Stich noch nicht zu Ende ist, gibt winningPlayer() null zurück.")
    @Test
    fun stichNochNichtZuEnde() {
        Truth.assertThat(emptyStich().winningPlayer(players, TrumpColor.Blue)).isNull()
        Truth.assertThat(stichOneCard(ccBlau11).winningPlayer(players, TrumpColor.Blue)).isNull()
        Truth.assertThat(stichTwoCards(ccBlau11, ccBlau12).winningPlayer(players, TrumpColor.Blue)).isNull()
        Truth.assertThat(fullStich(ccBlau11, ccBlau12, ccBlau13).winningPlayer(players, TrumpColor.Blue)).isNotNull()
    }

    companion object {
        private val wpSebastian = TestWizardPlayer("Sebastian")
        private val wpTabea = TestWizardPlayer("Tabea")
        private val wpDomi = TestWizardPlayer("Domi")

        val ccBlau13 = NumberColorCard(CardColor.Blue, 13)
        val ccBlau12 = NumberColorCard(CardColor.Blue, 12)
        val ccBlau11 = NumberColorCard(CardColor.Blue, 11)

        val ccGruen3 = NumberColorCard(CardColor.Green, 3)
        private val ccGruen5 = NumberColorCard(CardColor.Green, 5)

        data class ThreePlayersCase(
            val name: String,
            val trumpColor: TrumpColor,
            val stich: List<WizardCard>,
            val expectedWinner: TestWizardPlayer
        )

        @JvmStatic
        // Reihenfolge: Domi, Sebastian, Tabea
        fun winners3Players() = listOf(
            ThreePlayersCase(
                "Farbstich: Höchste Farbe (zuerst gespielt) gewinnt", TrumpColor.Yellow, listOf(
                    ccBlau13, ccBlau12, ccGruen3
                ), wpDomi
            ),
            ThreePlayersCase(
                "Farbstich: Höchste Farbe (überstochen) gewinnt",
                TrumpColor.Yellow,
                listOf(ccBlau12, ccBlau13, ccGruen3),
                wpSebastian
            ),
            ThreePlayersCase(
                "Farbstich abgestochen: Einziger Trumpf gewinnt",
                TrumpColor.Green,
                listOf(ccBlau12, ccBlau13, ccGruen3),
                wpTabea
            ),
            ThreePlayersCase(
                "Farbstich abgestochen: Trumpf wird übertrumpft",
                TrumpColor.Green,
                listOf(ccBlau12, ccGruen3, ccGruen5),
                wpTabea
            ),
            ThreePlayersCase(
                "Farbstich abgestochen: Trumpf bleibt unter erstem Trumpf", TrumpColor.Green, listOf(
                    ccBlau12, ccGruen5, ccGruen3
                ), wpSebastian
            ),
            ThreePlayersCase(
                "Farbstich abgestochen: Zauberer übersticht Trumpf",
                TrumpColor.Green,
                listOf(ccBlau12, ccGruen5, Joker.J1),
                wpTabea
            ),
            ThreePlayersCase(
                "Farbstich abgestochen: Null bleibt unter Trumpf",
                TrumpColor.Green,
                listOf(ccBlau12, ccGruen5, Null.N1),
                wpSebastian
            ),

            ThreePlayersCase("Trumpfstich: Alle werfen ab", TrumpColor.Blue, listOf(ccBlau12, ccGruen5, Null.N1), wpDomi),
            ThreePlayersCase(
                "Trumpfstich: Höherer Trumpf gewinnt",
                TrumpColor.Blue,
                listOf(ccBlau12, ccBlau13, Null.N1),
                wpSebastian
            ),
            ThreePlayersCase("Trumpfstich: Joker gewinnt", TrumpColor.Blue, listOf(ccBlau12, ccBlau13, Joker.J1), wpTabea),

            ThreePlayersCase("Joker: Erster Joker gewinnt", TrumpColor.Blue, listOf(Joker.J1, Joker.J2, Joker.J3), wpDomi),
            ThreePlayersCase(
                "Joker: Farbe nach Joker verliert",
                TrumpColor.Blue,
                listOf(ccGruen5, Joker.J1, ccBlau13),
                wpSebastian
            ),

            ThreePlayersCase(
                "Narren: Nur Narren: Erster Narr gewinnt",
                TrumpColor.Blue,
                listOf(Null.N1, Null.N2, Null.N3),
                wpDomi
            ),
            ThreePlayersCase(
                "Narren: Nur Narren: Erste Farbe gewinnt",
                TrumpColor.Blue,
                listOf(Null.N1, Null.N2, ccGruen5),
                wpTabea
            ),
        )

    }

    @ParameterizedTest
    @DisplayName("")
    @MethodSource("winners3Players")
    fun testWinners3Players(testCase: ThreePlayersCase) {
        val stich = fullStich(testCase.stich[0], testCase.stich[1], testCase.stich[2])

        Truth.assertThat(stich.winningPlayer(players, testCase.trumpColor)).isEqualTo(testCase.expectedWinner)
    }


}