package de.theodm.wizard.game.hand

import com.google.common.truth.Truth
import de.theodm.wizard.CardNotInHandException
import de.theodm.wizard.HandsOfPlayers
import de.theodm.wizard.game.TestWizardPlayer
import de.theodm.wizard.card.CardColor
import de.theodm.wizard.card.Joker
import de.theodm.wizard.card.NumberColorCard
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class HandsOfPlayersTest {
    private val wpSebastian = TestWizardPlayer("Sebastian")
    private val wpTabea = TestWizardPlayer("Tabea")
    private val wpDomi = TestWizardPlayer("Domi")

    private val ccBlau13 = NumberColorCard(CardColor.Blue, 13)
    private val ccBlau12 = NumberColorCard(CardColor.Blue, 12)
    private val ccBlau11 = NumberColorCard(CardColor.Blue, 11)

    private val ccGruen3 = NumberColorCard(CardColor.Green, 3)
    private val ccGruen5 = NumberColorCard(CardColor.Green, 5)

    @Test
    @DisplayName("Wenn kein Spieler mehr Karten auf der Hand hat, dann gibt isAllEmpty true zurück.")
    fun erfolgreichKeineKartenMehr() {
        val handsOfPlayers = HandsOfPlayers(mapOf(
                wpSebastian to listOf(),
                wpTabea to listOf(),
                wpDomi to listOf()
        ))

        Truth.assertThat(handsOfPlayers.isAllEmpty()).isTrue()
    }

    @Test
    @DisplayName("Wenn ein Spieler noch Karten auf der Hand hat, dann gibt isAllEmpty false zurück.")
    fun erfolgreichKartenVorhanden() {
        val handsOfPlayers = HandsOfPlayers(mapOf(
                wpSebastian to listOf(),
                wpTabea to listOf(),
                wpDomi to listOf(ccBlau11)
        ))

        Truth.assertThat(handsOfPlayers.isAllEmpty()).isFalse()
    }

    @Test
    @DisplayName("Es kann keine Karte von der Hand genommen werden, die der Spieler nicht besitzt.")
    fun karteNichtAblegbar() {
        val handsOfPlayers = HandsOfPlayers(mapOf(
                wpSebastian to listOf(ccBlau13),
                wpTabea to listOf(ccBlau12),
                wpDomi to listOf(ccBlau11)
        ))

        Assertions.assertThrows(CardNotInHandException::class.java) {
            handsOfPlayers.playCardFromHand(wpSebastian, ccGruen3)
        }
    }


    @Test
    @DisplayName("Ein Spieler legt eine Farbkarte erfolgreich von seiner Hand ab.")
    fun karteAbgelegt() {
        var handsOfPlayers = HandsOfPlayers(mapOf(
                wpSebastian to listOf(ccBlau13),
                wpTabea to listOf(ccBlau12),
                wpDomi to listOf(ccBlau11)
        ))

        handsOfPlayers = handsOfPlayers.playCardFromHand(wpSebastian, ccBlau13)

        Truth.assertThat(handsOfPlayers[wpSebastian]).isEmpty()
    }

    @Test
    @DisplayName("getLastRemainingCardForPlayer gibt die letzte Karte eines Spielers zurück")
    fun letzteKarte() {
        val handsOfPlayers = HandsOfPlayers(mapOf(
            wpSebastian to listOf(Joker.J1, Joker.J2),
            wpTabea to listOf(ccBlau12),
            wpDomi to listOf(ccBlau11)
        ))

        Assertions.assertThrows(IllegalStateException::class.java) {
            handsOfPlayers.getLastRemainingCardForPlayer(wpSebastian)
        }
    }

    @Test
    @DisplayName("getLastRemainingCardForPlayer gibt einen Fehler aus, wenn der Spieler mehr als eine Karte hat")
    fun letzteKarteFehler() {
        val handsOfPlayers = HandsOfPlayers(mapOf(
            wpSebastian to listOf(Joker.J1, Joker.J2),
            wpTabea to listOf(ccBlau12),
            wpDomi to listOf(ccBlau11)
        ))

        Truth.assertThat(handsOfPlayers.getLastRemainingCardForPlayer(wpTabea)).isEqualTo(ccBlau12)
    }

    @Test
    @DisplayName("Ein Spieler legt einen Zauberer erfolgreich von seiner Hand ab (Er hat mehrere).")
    fun zaubererAbgelegt() {
        var handsOfPlayers = HandsOfPlayers(mapOf(
                wpSebastian to listOf(Joker.J1, Joker.J2),
                wpTabea to listOf(ccBlau12),
                wpDomi to listOf(ccBlau11)
        ))

        handsOfPlayers = handsOfPlayers.playCardFromHand(wpSebastian, Joker.J1)

        Truth.assertThat(handsOfPlayers[wpSebastian]).containsExactly(Joker.J2)
    }
}