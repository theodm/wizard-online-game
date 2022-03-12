package de.theodm.wizard

import com.google.common.truth.Truth
import de.theodm.wizard.card.CardColor
import de.theodm.wizard.card.NumberColorCard
import de.theodm.wizard.game.TestWizardPlayer
import de.theodm.wizard.game.WizardGameSettings
import de.theodm.wizard.game.WizardGameState
import de.theodm.wizard.game.bets.Bets
import de.theodm.wizard.game.card.TrumpColor
import de.theodm.wizard.game.players.Players
import de.theodm.wizard.game.round.FinishedWizardRound
import de.theodm.wizard.game.round.OpenWizardRound
import de.theodm.wizard.game.stich.Stich
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
            trumpCard = ccGruen3,
            trumpColor = TrumpColor.Green,
            currentPlayer = wpSebastian,
            bets = Bets(
                mapOf(
                    wpSebastian to 1,
                    wpDomi to 1,
                    wpTabea to 0
                )
            ),
            handsOfPlayers = HandsOfPlayers(mapOf()),
            oldStiche = listOf(
                Stich(wpSebastian, listOf(ccGruen5, ccGruen3, ccBlau12))
            ),
            currentStich = Stich(
                startPlayer = wpSebastian,
                cards = listOf()
            )
        )
    }

    val gameSettings = WizardGameSettings(hiddenBets = false)

    @DisplayName("Am Ende von Runde 1, wird Runde 2 gestartet.")
    @Test
    public fun test() {
        val previousRound = WizardGameState(
            players = players3,
            previousRounds = listOf(),
            currentRound = finishedOpenRound,
            gameSettings = gameSettings
        )

        val result = previousRound
            .finishRound()
            .startNewRound()

        Truth.assertThat(result.players).isEqualTo(players3)
        Truth.assertThat(result.previousRounds).isEqualTo(listOf(finishedOpenRound.result()))
        Truth.assertThat(result.currentRound!!.numberOfCards).isEqualTo(2)
        Truth.assertThat(result.currentRound!!.startingPlayer).isEqualTo(wpTabea)
    }

    @DisplayName("Am Ende von Runde 20, wird das Spiel bei 3 Spielern beendet.")
    @Test
    public fun test20() {
        val finishedOpenRound = finishedOpenRound.copy(numberOfCards = 20)

        val oldRounds = listOf(
            FinishedWizardRound(1, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(2, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(3, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(4, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(5, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(6, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(7, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(8, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(9, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(10, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(11, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(12, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(13, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(14, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(15, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(16, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(17, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(18, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(19, null, TrumpColor.Green, Bets(mapOf()), listOf()),
        )

        val previousRound = WizardGameState(
            players = players3,
            previousRounds = oldRounds,
            currentRound = finishedOpenRound,
            gameSettings = gameSettings
        )

        val result = previousRound
            .finishRound()
            .startNewRound()

        Truth.assertThat(result.isEnded()).isTrue()
        Truth.assertThat(result.players).isEqualTo(players3)
        Truth.assertThat(result.previousRounds).isEqualTo(oldRounds + finishedOpenRound.result())
        Truth.assertThat(result.currentRound).isEqualTo(null)
    }

    @DisplayName("Am Ende von Runde 15, wird das Spiel bei 4 Spielern beendet.")
    @Test
    public fun test15() {
        val finishedOpenRound = finishedOpenRound.copy(numberOfCards = 15)

        val oldRounds = listOf(
            FinishedWizardRound(1, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(2, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(3, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(4, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(5, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(6, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(7, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(8, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(9, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(10, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(11, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(12, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(13, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(14, null, TrumpColor.Green, Bets(mapOf()), listOf()),
        )

        val previousRound = WizardGameState(
            players = players4,
            previousRounds = oldRounds,
            currentRound = finishedOpenRound,
            gameSettings = gameSettings
        )

        val result = previousRound
            .finishRound()
            .startNewRound()

        Truth.assertThat(result.isEnded()).isTrue()
        Truth.assertThat(result.players).isEqualTo(players4)
        Truth.assertThat(result.previousRounds).isEqualTo(oldRounds + finishedOpenRound.result())
        Truth.assertThat(result.currentRound).isEqualTo(null)
    }

    @DisplayName("Am Ende von Runde 12, wird das Spiel bei 5 Spielern beendet.")
    @Test
    public fun test12() {
        val finishedOpenRound = finishedOpenRound.copy(numberOfCards = 12)

        val oldRounds = listOf(
            FinishedWizardRound(1, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(2, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(3, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(4, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(5, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(6, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(7, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(8, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(9, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(10, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(11, null, TrumpColor.Green, Bets(mapOf()), listOf()),
        )

        val previousRound = WizardGameState(
            players = players5,
            previousRounds = oldRounds,
            currentRound = finishedOpenRound,
            gameSettings = gameSettings
        )

        val result = previousRound.finishRound()
            .startNewRound()

        Truth.assertThat(result.isEnded()).isTrue()
        Truth.assertThat(result.players).isEqualTo(players5)
        Truth.assertThat(result.previousRounds).isEqualTo(oldRounds + finishedOpenRound.result())
        Truth.assertThat(result.currentRound).isEqualTo(null)
    }


    @DisplayName("Am Ende von Runde 10, wird das Spiel bei 6 Spielern beendet.")
    @Test
    public fun test10() {
        val finishedOpenRound = finishedOpenRound.copy(numberOfCards = 10)

        val oldRounds = listOf(
            FinishedWizardRound(1, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(2, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(3, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(4, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(5, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(6, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(7, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(8, null, TrumpColor.Green, Bets(mapOf()), listOf()),
            FinishedWizardRound(9, null, TrumpColor.Green, Bets(mapOf()), listOf()),
        )

        val previousRound = WizardGameState(
            players = players6,
            previousRounds = oldRounds,
            currentRound = finishedOpenRound,
            gameSettings = gameSettings
        )

        val result = previousRound
            .finishRound()
            .startNewRound()

        Truth.assertThat(result.isEnded()).isTrue()
        Truth.assertThat(result.players).isEqualTo(players6)
        Truth.assertThat(result.previousRounds).isEqualTo(oldRounds + finishedOpenRound.result())
        Truth.assertThat(result.currentRound).isEqualTo(null)
    }
}