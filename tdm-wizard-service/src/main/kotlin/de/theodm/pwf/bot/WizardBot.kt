package de.theodm.pwf.bot

import de.theodm.wizard.*
import de.theodm.wizard.card.WizardCard
import de.theodm.wizard.game.WizardGameSettings
import de.theodm.wizard.game.WizardGameState
import de.theodm.wizard.game.bets.Bets
import de.theodm.wizard.game.card.TrumpColor
import de.theodm.wizard.game.round.*
import mu.KotlinLogging


interface WizardBot {
    fun firstRoundBettingPhase(roundStateForPlayer: RoundStateForPlayerFirstRound, immutableRoundState: ImmutableRoundState): Int
    fun firstRoundSelectTrumpPhase(roundStateForPlayer: RoundStateForPlayerFirstRound, immutableRoundState: ImmutableRoundState): TrumpColor

    fun normalRoundBettingPhase(roundStateForPlayer: RoundStateForPlayerNormalRound, immutableRoundState: ImmutableRoundState): Int
    fun normalRoundSelectTrumpPhase(roundStateForPlayer: RoundStateForPlayerNormalRound, immutableRoundState: ImmutableRoundState): TrumpColor
    fun normalRoundPlayingPhase(roundStateForPlayer: RoundStateForPlayerNormalRound, immutableRoundState: ImmutableRoundState): WizardCard
}

fun WizardGameState.applyBotAction(
    roundStateForPlayer: RoundStateForPlayer,
    immutableRoundState: ImmutableRoundState,
    bot: WizardBot
): WizardGameState {
    val phase = roundStateForPlayer
        .phase

    val currentPlayer = roundStateForPlayer
        .currentPlayer

    return when (roundStateForPlayer) {
        is RoundStateForPlayerFirstRound -> when (phase) {
                OpenWizardRound.Phase.SelectTrumpPhase -> this.selectTrump(currentPlayer, bot.firstRoundSelectTrumpPhase(roundStateForPlayer, immutableRoundState))
                OpenWizardRound.Phase.BettingPhase -> this.placeBet(currentPlayer, bot.firstRoundBettingPhase(roundStateForPlayer, immutableRoundState))
                OpenWizardRound.Phase.PlayingPhase -> this.placeCardInFirstRound(currentPlayer)
                OpenWizardRound.Phase.RoundEnded -> this //.finishAndStartNewRound(currentPlayer)
            }
        is RoundStateForPlayerNormalRound -> when(phase) {
                OpenWizardRound.Phase.SelectTrumpPhase -> this.selectTrump(currentPlayer, bot.normalRoundSelectTrumpPhase(roundStateForPlayer, immutableRoundState))
                OpenWizardRound.Phase.BettingPhase -> this.placeBet(currentPlayer, bot.normalRoundBettingPhase(roundStateForPlayer, immutableRoundState))
                OpenWizardRound.Phase.PlayingPhase -> this.placeCard(currentPlayer, bot.normalRoundPlayingPhase(roundStateForPlayer, immutableRoundState))
                OpenWizardRound.Phase.RoundEnded -> this //.finishAndStartNewRound(currentPlayer)
            }
        else -> TODO()
    }
}

interface ModelEvaluator {
    fun evaluateR1Observation(observation: R1Observation): Int
}

val log = KotlinLogging.logger {  }

class ConnectedPythonBot(
    private val modelEvaluator: ModelEvaluator,
    private val defaultBot: RandomBot = RandomBot()
): WizardBot by defaultBot  {
    override fun firstRoundBettingPhase(
        roundStateForPlayer: RoundStateForPlayerFirstRound,
        immutableRoundState: ImmutableRoundState
    ): Int {
        val action = modelEvaluator
            .evaluateR1Observation(r1ObservationOf(roundStateForPlayer, immutableRoundState))

        if (action >= 2) {
            log.warn { "Bot $this took invalid action for this phase: $action in BettingPhase." }
            log.warn { "Taking random action..." }

            return defaultBot.firstRoundBettingPhase(roundStateForPlayer, immutableRoundState)
        }

        val allowedBets = Bets(roundStateForPlayer.bets)
            .allowedBets(
                gameSettings = immutableRoundState.gameSettings,
                player = roundStateForPlayer.currentPlayer,
                numberOfCards = immutableRoundState.numberOfCards
            )

        if (action !in allowedBets) {
            log.warn { "Bot $this placed invalid bet ${action}." }

            if (action + 1 in allowedBets) {
                log.warn { "Place bet ${action + 1} instead..." }
                return action + 1
            }

            log.warn { "Place bet ${action - 1} instead..." }
            return action - 1
        }

        return action
    }

    override fun firstRoundSelectTrumpPhase(
        roundStateForPlayer: RoundStateForPlayerFirstRound,
        immutableRoundState: ImmutableRoundState
    ): TrumpColor {
        val action = modelEvaluator
            .evaluateR1Observation(r1ObservationOf(roundStateForPlayer, immutableRoundState))

        return when (action) {
            2 -> TrumpColor.Yellow
            3 -> TrumpColor.Red
            4 -> TrumpColor.Green
            5 -> TrumpColor.Blue
            else -> {
                log.warn { "Bot $this took invalid action ${action} in SelectTrumpPhase." }
                log.warn { "Defaulting to random color" }
                defaultBot.firstRoundSelectTrumpPhase(roundStateForPlayer, immutableRoundState)
            }
        }
    }
}

class RandomBot: WizardBot {

    private fun randomAllowedBet(
        roundStateForPlayer: RoundStateForPlayer,
        immutableRoundState: ImmutableRoundState
    ): Int {
        return Bets(roundStateForPlayer.bets)
            .allowedBets(
                gameSettings = immutableRoundState.gameSettings,
                player = roundStateForPlayer.currentPlayer,
                numberOfCards = immutableRoundState.numberOfCards
            ).random()
    }

    private fun randomAllowedTrumpColor(
        roundStateForPlayer: RoundStateForPlayer,
        immutableRoundState: ImmutableRoundState
    ): TrumpColor {
        return listOf(
            TrumpColor.Blue,
            TrumpColor.Red,
            TrumpColor.Green,
            TrumpColor.Yellow
        ).random()
    }

    override fun firstRoundBettingPhase(
        roundStateForPlayer: RoundStateForPlayerFirstRound,
        immutableRoundState: ImmutableRoundState
    ) = randomAllowedBet(roundStateForPlayer, immutableRoundState)

    override fun firstRoundSelectTrumpPhase(
        roundStateForPlayer: RoundStateForPlayerFirstRound,
        immutableRoundState: ImmutableRoundState
    ) = randomAllowedTrumpColor(roundStateForPlayer, immutableRoundState)

    override fun normalRoundBettingPhase(
        roundStateForPlayer: RoundStateForPlayerNormalRound,
        immutableRoundState: ImmutableRoundState
    ) = randomAllowedBet(roundStateForPlayer, immutableRoundState)

    override fun normalRoundSelectTrumpPhase(
        roundStateForPlayer: RoundStateForPlayerNormalRound,
        immutableRoundState: ImmutableRoundState
    )  = randomAllowedTrumpColor(roundStateForPlayer, immutableRoundState)


    override fun normalRoundPlayingPhase(
        roundStateForPlayer: RoundStateForPlayerNormalRound,
        immutableRoundState: ImmutableRoundState
    ): WizardCard {
        return roundStateForPlayer
            .currentStich
            .allowedToPlayCards(immutableRoundState.players, roundStateForPlayer.ownCards)
            .random()
    }

}