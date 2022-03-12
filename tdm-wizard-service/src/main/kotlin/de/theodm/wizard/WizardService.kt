package de.theodm.wizard

import de.theodm.pwf.bot.*
import de.theodm.wizard.card.WizardCard
import de.theodm.wizard.game.WizardGameSettings
import de.theodm.wizard.game.WizardGameState
import de.theodm.wizard.game.card.TrumpColor
import de.theodm.wizard.game.players.BotWizardPlayer
import de.theodm.wizard.game.players.WizardPlayer
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import mu.KotlinLogging
import py4j.ClientServer
import java.util.concurrent.TimeUnit
import javax.inject.Inject


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


private val log = KotlinLogging.logger {  }


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
        val clientServer = ClientServer(null)
        val modelEvaluator: ModelEvaluator = clientServer
            .getPythonServerEntryPoint(arrayOf<Class<*>>(ModelEvaluator::class.java)) as ModelEvaluator

        wizardStorage
            .wizardStream()
            .filter {
                val currentRound = it
                    .second
                    .currentRound

                if (currentRound == null) {
                    return@filter false
                }

                if (currentRound.currentPlayer is BotWizardPlayer
                    && (currentRound.currentPlayer.botType() == "RandomBot" || currentRound.currentPlayer.botType() == "MaxPointsBot")) {
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

                if (
                    currentRound
                        .currentPlayer is BotWizardPlayer && (currentRound.currentPlayer.botType() == "RandomBot" || currentRound.currentPlayer.botType() == "MaxPointsBet")
                ) {
                    log.info { "Bot's turn: ${currentRound.currentPlayer}" }

                    val bot = when (currentRound.currentPlayer.botType()) {
                        "RandomBot" -> RandomBot()
                        "MaxPointsBot" -> RandomBot()
                        else -> TODO()
                    }
                    try {
                        updateGameState(lobbyCode) { wizardGameState ->
                            val viewForPlayer = wizardGameState
                                .currentRound!!
                                .viewForPlayer(currentRound.currentPlayer)

                            val immutableRoundState = wizardGameState
                                .currentRound!!
                                .immutableRoundState(wizardGameState.gameSettings, wizardGameState.players)

                            return@updateGameState wizardGameState
                                .applyBotAction(viewForPlayer, immutableRoundState, bot)
                        }
                    } catch(npe: Exception) {
                        log.error { npe }
                    }

                }

            }
    }

    private fun updateGameState(
        lobbyCode: String,
        block: (wizardState: WizardGameState) -> WizardGameState
    ) {
        val gameState = wizardStorage.getWizard(lobbyCode)

        val newGameState = block(gameState)

        wizardStorage.updateWizard(lobbyCode, newGameState)
    }

    fun startGame(lobbyCode: String, players: List<WizardPlayer>) {
        val gameState = WizardGameState.initial(WizardGameSettings(hiddenBets = false), players)

        wizardStorage.createWizard(lobbyCode, gameState)
    }

    fun playSingleCard(lobbyCode: String, player: WizardPlayer) {
        val gameState = wizardStorage.getWizard(lobbyCode)

        val newGameState = gameState.placeCardInFirstRound(player)

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

        val newGameState = gameState
            .finishRound()
            .startNewRound()

        wizardStorage.updateWizard(lobbyCode, newGameState)
    }

}