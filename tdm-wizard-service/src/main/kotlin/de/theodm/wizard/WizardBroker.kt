package de.theodm.wizard

import de.theodm.wizard.card.WizardCard

class WizardBroker(
) {
    private lateinit var gameState: WizardGameState

    fun sendMessage(player: WizardPlayer, wizardAction: WizardAction) {

    }

    fun startGame(players: List<WizardPlayer>) {
        val delta = mutableListOf<WizardAction>()

        gameState = WizardGameState.initial(players[0], players);
    }

    fun placeBet(
            player: WizardPlayer,
            bet: Int
    ) {
        gameState = gameState.placeBet(player, bet)
    }

    fun placeCard(
            player: WizardPlayer,
            card: WizardCard
    ) {

    }

    fun viewForPlayer(
            player: WizardPlayer
    ) {
        gameState.currentRound!!.viewForPlayer(
                player
        )
    }


}