package de.theodm

import de.theodm.wizard.WizardPlayer

data class BotParticipant(
    private val uniqueID: String,
    val name: String,
    private val botType: String
) : Participant, WizardPlayer {
    override fun userPublicID() = uniqueID
    override fun isBot() = true
    override fun botType() = botType
}
