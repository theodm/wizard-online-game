package de.theodm.pwf.routing.wizard

import de.theodm.pwf.routing.lobby.LobbyParticipant
import de.theodm.pwf.routing.model.wizard.RsWizardCard
import de.theodm.pwf.user.User
import de.theodm.wizard.TrumpColor
import de.theodm.wizard.WizardService
import javax.inject.Inject

class WizardEndpoints @Inject constructor(
    private val wizardService: WizardService
) {
    fun playSingleCard(
        authUser: User,
        lobbyCode: String
    ) {
        wizardService.playSingleCard(
            lobbyCode = lobbyCode,
            player = LobbyParticipant.of(authUser)
        )
    }

    fun playCard(
        authUser: User,
        lobbyCode: String,
        wizardCard: RsWizardCard
    ) {
        wizardService.playCard(
            lobbyCode = lobbyCode,
            player = LobbyParticipant.of(authUser),
            card = wizardCard.toWizardCard()
        )
    }

    fun placeBet(
        authUser: User,
        lobbyCode: String,
        bet: Int
    ) {
        wizardService.placeBet(
            lobbyCode = lobbyCode,
            player = LobbyParticipant.of(authUser),
            bet = bet
        )
    }

    fun selectTrumpColor(
        authUser: User,
        lobbyCode: String,
        trumpColor: TrumpColor
    ) {
        wizardService.selectTrumpColor(
            lobbyCode = lobbyCode,
            player = LobbyParticipant.of(authUser),
            trumpColor = trumpColor
        )
    }

    fun finishRound(
        authUser: User,
        lobbyCode: String
    ) {
        wizardService.finishRound(
            lobbyCode = lobbyCode,
            player = LobbyParticipant.of(authUser)
        )
    }

}
