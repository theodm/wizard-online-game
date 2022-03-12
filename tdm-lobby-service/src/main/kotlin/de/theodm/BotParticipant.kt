package de.theodm

import de.theodm.wizard.game.players.BotWizardPlayer
import de.theodm.wizard.game.players.WizardPlayer

data class BotParticipant(
    private val uniqueID: String,
    val name: String,
    private val botType: String
) : Participant, BotWizardPlayer {
    override fun userPublicID() = uniqueID
    override fun isBot() = true
    override fun botType() = botType
}
