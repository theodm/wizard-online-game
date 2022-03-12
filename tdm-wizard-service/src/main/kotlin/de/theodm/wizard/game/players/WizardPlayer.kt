package de.theodm.wizard.game.players

interface WizardPlayer {
    fun userPublicID(): String
}

interface BotWizardPlayer: WizardPlayer {
    fun botType(): String
}
