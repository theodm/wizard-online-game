package de.theodm.wizard

import de.theodm.wizard.card.WizardCard
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import mu.KotlinLogging
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.round

sealed class BotAction

data class BotActionSelectTrump(
    val trumpColor: TrumpColor,
): BotAction()

data class BotActionPlaceBet(
    val bet: Int
): BotAction()

object BotActionPlaceCardInFirstRound: BotAction()

data class BotActionPlaceCardInNormalRound(
    val card: WizardCard
): BotAction()

object BotActionStartNewRound: BotAction()

fun doAction(
    wizardService: WizardService,
    lobbyCode: String,
    player: WizardPlayer,
    action: BotAction
) {
    when (action) {
        is BotActionSelectTrump ->  wizardService.selectTrumpColor(lobbyCode, player, action.trumpColor)
        is BotActionPlaceBet ->  wizardService.placeBet(lobbyCode, player, action.bet)
        is BotActionPlaceCardInFirstRound ->  wizardService.playSingleCard(lobbyCode, player)
        is BotActionPlaceCardInNormalRound ->  wizardService.playCard(lobbyCode, player, action.card)
        is BotActionStartNewRound ->  wizardService.finishRound(lobbyCode, player)

    }


}

private val log = KotlinLogging.logger {  }

fun randomBot(
    immutableRoundState: ImmutableRoundState,
    roundStateForPlayer: RoundStateForPlayer
): BotAction {
    when (roundStateForPlayer.phase) {
        OpenWizardRound.Phase.SelectTrumpPhase -> {
            return BotActionSelectTrump(listOf(TrumpColor.Blue, TrumpColor.Red, TrumpColor.Green, TrumpColor.Yellow).random())
        }
        OpenWizardRound.Phase.BettingPhase -> {
            val allowedBets = Bets(roundStateForPlayer.bets)
                .allowedBets(roundStateForPlayer.currentPlayer, immutableRoundState.numberOfCards)

            return BotActionPlaceBet(allowedBets.random())
        }
        OpenWizardRound.Phase.PlayingPhase -> {
            if (roundStateForPlayer is RoundStateForPlayerFirstRound) {
                return BotActionPlaceCardInFirstRound
            }

            require(roundStateForPlayer is RoundStateForPlayerNormalRound)

            val allowedCards = roundStateForPlayer
                .currentStich
                .allowedToPlayCards(roundStateForPlayer.ownCards)

            return BotActionPlaceCardInNormalRound(allowedCards.random())
        }
        OpenWizardRound.Phase.RoundEnded -> {
            return BotActionStartNewRound
        }
    }
}

class WizardService @Inject constructor(
    private val wizardStorage: WizardStorage
) {
    fun wizardStream(lobbyCode: String): Observable<WizardGameState> = wizardStorage
        .wizardStream(lobbyCode)
        .distinctUntilChanged()

    fun lobbyFinishedStream(lobbyCode: String): Single<Unit> = wizardStream(lobbyCode)
        .filter { it.currentRound == null }
        .map { }
        .firstOrError()

    init {
        wizardStorage
            .wizardStream()
            .filter {
                val currentRound = it
                    .second
                    .currentRound

                if (currentRound == null) {
                    return@filter false
                }

                if (currentRound.currentPlayer.isBot()
                    && currentRound.currentPlayer.botType() == "RandomBot") {
                    return@filter true
                }

                return@filter false
            }
            .concatMap { Observable.just(it).delay(2, TimeUnit.SECONDS) }
            .subscribe {
                val lobbyCode = it
                    .first

                val currentRound = it
                    .second
                    .currentRound

                if (currentRound == null)
                    return@subscribe

                if (currentRound.currentPlayer.isBot()
                    && currentRound.currentPlayer.botType() == "RandomBot") {
                    log.info { "Bot's turn: ${currentRound.currentPlayer}" }

                    val botAction = randomBot(
                        currentRound.immutableRoundState(),
                        currentRound.viewForPlayer(currentRound.currentPlayer)
                    )

                    log.info { "Bot's action: $botAction" }

                    doAction(this, lobbyCode, currentRound.currentPlayer, botAction)
                }

            }
    }

    fun startGame(lobbyCode: String, players: List<WizardPlayer>) {
        val gameState = WizardGameState.initial(players[0], players);

        wizardStorage.createWizard(lobbyCode, gameState)
    }

    fun playSingleCard(lobbyCode: String, player: WizardPlayer) {
        val gameState = wizardStorage.getWizard(lobbyCode)

        val newGameState = gameState.placeSingleCard(player)

        wizardStorage.updateWizard(lobbyCode, newGameState)
    }

    fun playCard(lobbyCode: String, player: WizardPlayer, card: WizardCard) {
        val gameState = wizardStorage.getWizard(lobbyCode)

        val newGameState = gameState.placeCard(player, card)

        wizardStorage.updateWizard(lobbyCode, newGameState)
    }

    fun placeBet(lobbyCode: String, player: WizardPlayer, bet: Int) {
        val gameState = wizardStorage.getWizard(lobbyCode)

        val newGameState = gameState.placeBet(player, bet)

        wizardStorage.updateWizard(lobbyCode, newGameState)
    }

    fun selectTrumpColor(lobbyCode: String, player: WizardPlayer, trumpColor: TrumpColor) {
        val gameState = wizardStorage.getWizard(lobbyCode)

        val newGameState = gameState.selectTrump(player, trumpColor)

        wizardStorage.updateWizard(lobbyCode, newGameState)
    }

    fun finishRound(lobbyCode: String, player: WizardPlayer) {
        val gameState = wizardStorage.getWizard(lobbyCode)

        val newGameState = gameState.finishAndStartNewRound(player)

        wizardStorage.updateWizard(lobbyCode, newGameState)
    }

}