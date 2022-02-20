package de.theodm.wizard

import com.google.common.truth.Truth
import de.theodm.wizard.card.CardColor
import de.theodm.wizard.card.NumberColorCard
import de.theodm.wizard.stich.Stich
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class WizardGameStateTest {
    companion object {
        private val wpSebastian = TestWizardPlayer("Sebastian")
        private val wpTabea = TestWizardPlayer("Tabea")
        private val wpDomi = TestWizardPlayer("Domi")
        private val wpMarcel = TestWizardPlayer("Marcel")
        private val wpSteiner = TestWizardPlayer("Steiner")
        private val wpAlex = TestWizardPlayer("Alex")

        val ccBlau13 = NumberColorCard(CardColor.Blue, 13)
        val ccBlau12 = NumberColorCard(CardColor.Blue, 12)
        val ccBlau11 = NumberColorCard(CardColor.Blue, 11)

        val ccGruen3 = NumberColorCard(CardColor.Green, 3)
        val ccGruen5 = NumberColorCard(CardColor.Green, 5)


        val players3 = Players(listOf(wpSebastian, wpTabea, wpDomi))
        val players4 = Players(players3 + wpMarcel)
        val players5 = Players(players4 + wpSteiner)
        val players6 = Players(players5 + wpAlex)

        /**
         * Eine fertige Runde mit 3 Spielern,
         * damit wir den Übergang in eine neue Runde
         * testen können.
         */
        val finishedOpenRound = OpenWizardRound(
                startingPlayer = wpSebastian,
                numberOfCards = 1,
                players = players3,
                trumpCard = ccGruen3,
                trumpColor = TrumpColor.Green,
                currentPlayer = wpSebastian,
                bets = Bets(mapOf(
                        wpSebastian to 1,
                        wpDomi to 1,
                        wpTabea to 0
                )),
                handsOfPlayers = HandsOfPlayers(mapOf()),
                sticheOfPlayer = mapOf(
                        wpSebastian to 1,
                        wpDomi to 0,
                        wpTabea to 0
                ),
                currentStich = Stich(
                        players = players3,
                        startPlayer = wpDomi,
                        cards = listOf()
                )
        )
    }

    @DisplayName("Am Ende von Runde 1, wird Runde 2 gestartet.")
    @Test
    public fun test() {
        val previousRound = WizardGameState(
                hostPlayer = wpSebastian,
                players = players3,
                oldRounds = listOf(),
                currentRound = finishedOpenRound
        )

        val result
                = previousRound.finishAndStartNewRound(wpSebastian)

        Truth.assertThat(result.hostPlayer).isEqualTo(wpSebastian)
        Truth.assertThat(result.players).isEqualTo(players3)
        Truth.assertThat(result.oldRounds).isEqualTo(listOf(finishedOpenRound.result()))
        Truth.assertThat(result.currentRound!!.numberOfCards).isEqualTo(2)
        Truth.assertThat(result.currentRound!!.startingPlayer).isEqualTo(wpTabea)
    }

    @DisplayName("Am Ende von Runde 20, wird das Spiel bei 3 Spielern beendet.")
    @Test
    public fun test20() {
        val finishedOpenRound
                = finishedOpenRound.copy(numberOfCards = 20)

        val oldRounds = listOf(
                FinishedWizardRound(1, mapOf(), mapOf()),
                FinishedWizardRound(2, mapOf(), mapOf()),
                FinishedWizardRound(3, mapOf(), mapOf()),
                FinishedWizardRound(4, mapOf(), mapOf()),
                FinishedWizardRound(5, mapOf(), mapOf()),
                FinishedWizardRound(6, mapOf(), mapOf()),
                FinishedWizardRound(7, mapOf(), mapOf()),
                FinishedWizardRound(8, mapOf(), mapOf()),
                FinishedWizardRound(9, mapOf(), mapOf()),
                FinishedWizardRound(10, mapOf(), mapOf()),
                FinishedWizardRound(11, mapOf(), mapOf()),
                FinishedWizardRound(12, mapOf(), mapOf()),
                FinishedWizardRound(13, mapOf(), mapOf()),
                FinishedWizardRound(14, mapOf(), mapOf()),
                FinishedWizardRound(15, mapOf(), mapOf()),
                FinishedWizardRound(16, mapOf(), mapOf()),
                FinishedWizardRound(17, mapOf(), mapOf()),
                FinishedWizardRound(18, mapOf(), mapOf()),
                FinishedWizardRound(19, mapOf(), mapOf()),
        )

        val previousRound = WizardGameState(
                hostPlayer = wpSebastian,
                players = players3,
                oldRounds = oldRounds,
                currentRound = finishedOpenRound
        )

        val result
                = previousRound.finishAndStartNewRound(wpSebastian)

        Truth.assertThat(result.hostPlayer).isEqualTo(wpSebastian)
        Truth.assertThat(result.players).isEqualTo(players3)
        Truth.assertThat(result.oldRounds).isEqualTo(oldRounds + finishedOpenRound.result())
        Truth.assertThat(result.currentRound).isEqualTo(null)
    }

    @DisplayName("Am Ende von Runde 15, wird das Spiel bei 4 Spielern beendet.")
    @Test
    public fun test15() {
        val finishedOpenRound
                = finishedOpenRound.copy(numberOfCards = 15)

        val oldRounds = listOf(
                FinishedWizardRound(1, mapOf(), mapOf()),
                FinishedWizardRound(2, mapOf(), mapOf()),
                FinishedWizardRound(3, mapOf(), mapOf()),
                FinishedWizardRound(4, mapOf(), mapOf()),
                FinishedWizardRound(5, mapOf(), mapOf()),
                FinishedWizardRound(6, mapOf(), mapOf()),
                FinishedWizardRound(7, mapOf(), mapOf()),
                FinishedWizardRound(8, mapOf(), mapOf()),
                FinishedWizardRound(9, mapOf(), mapOf()),
                FinishedWizardRound(10, mapOf(), mapOf()),
                FinishedWizardRound(11, mapOf(), mapOf()),
                FinishedWizardRound(12, mapOf(), mapOf()),
                FinishedWizardRound(13, mapOf(), mapOf()),
                FinishedWizardRound(14, mapOf(), mapOf()),
        )

        val previousRound = WizardGameState(
                hostPlayer = wpSebastian,
                players = players4,
                oldRounds = oldRounds,
                currentRound = finishedOpenRound
        )

        val result
                = previousRound.finishAndStartNewRound(wpSebastian)

        Truth.assertThat(result.hostPlayer).isEqualTo(wpSebastian)
        Truth.assertThat(result.players).isEqualTo(players4)
        Truth.assertThat(result.oldRounds).isEqualTo(oldRounds + finishedOpenRound.result())
        Truth.assertThat(result.currentRound).isEqualTo(null)
    }

    @DisplayName("Am Ende von Runde 12, wird das Spiel bei 5 Spielern beendet.")
    @Test
    public fun test12() {
        val finishedOpenRound
                = finishedOpenRound.copy(numberOfCards = 12)

        val oldRounds = listOf(
                FinishedWizardRound(1, mapOf(), mapOf()),
                FinishedWizardRound(2, mapOf(), mapOf()),
                FinishedWizardRound(3, mapOf(), mapOf()),
                FinishedWizardRound(4, mapOf(), mapOf()),
                FinishedWizardRound(5, mapOf(), mapOf()),
                FinishedWizardRound(6, mapOf(), mapOf()),
                FinishedWizardRound(7, mapOf(), mapOf()),
                FinishedWizardRound(8, mapOf(), mapOf()),
                FinishedWizardRound(9, mapOf(), mapOf()),
                FinishedWizardRound(10, mapOf(), mapOf()),
                FinishedWizardRound(11, mapOf(), mapOf()),
        )

        val previousRound = WizardGameState(
                hostPlayer = wpSebastian,
                players = players5,
                oldRounds = oldRounds,
                currentRound = finishedOpenRound
        )

        val result
                = previousRound.finishAndStartNewRound(wpSebastian)

        Truth.assertThat(result.hostPlayer).isEqualTo(wpSebastian)
        Truth.assertThat(result.players).isEqualTo(players5)
        Truth.assertThat(result.oldRounds).isEqualTo(oldRounds + finishedOpenRound.result())
        Truth.assertThat(result.currentRound).isEqualTo(null)
    }


    @DisplayName("Am Ende von Runde 10, wird das Spiel bei 6 Spielern beendet.")
    @Test
    public fun test10() {
        val finishedOpenRound
                = finishedOpenRound.copy(numberOfCards = 10)

        val oldRounds = listOf(
                FinishedWizardRound(1, mapOf(), mapOf()),
                FinishedWizardRound(2, mapOf(), mapOf()),
                FinishedWizardRound(3, mapOf(), mapOf()),
                FinishedWizardRound(4, mapOf(), mapOf()),
                FinishedWizardRound(5, mapOf(), mapOf()),
                FinishedWizardRound(6, mapOf(), mapOf()),
                FinishedWizardRound(7, mapOf(), mapOf()),
                FinishedWizardRound(8, mapOf(), mapOf()),
                FinishedWizardRound(9, mapOf(), mapOf()),
        )

        val previousRound = WizardGameState(
                hostPlayer = wpSebastian,
                players = players6,
                oldRounds = oldRounds,
                currentRound = finishedOpenRound
        )

        val result
                = previousRound.finishAndStartNewRound(wpSebastian)

        Truth.assertThat(result.hostPlayer).isEqualTo(wpSebastian)
        Truth.assertThat(result.players).isEqualTo(players6)
        Truth.assertThat(result.oldRounds).isEqualTo(oldRounds + finishedOpenRound.result())
        Truth.assertThat(result.currentRound).isEqualTo(null)
    }
}