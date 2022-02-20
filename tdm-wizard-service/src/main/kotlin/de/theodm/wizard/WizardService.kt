package de.theodm.wizard

import de.theodm.wizard.card.WizardCard
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

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