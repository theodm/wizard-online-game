package de.theodm.wizard.game

import de.theodm.wizard.game.players.WizardPlayer

data class TestWizardPlayer(
    private val name: String
) : WizardPlayer {
    override fun userPublicID() = name
    override fun toString() = name
}