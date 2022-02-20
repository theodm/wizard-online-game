package de.theodm.wizard

data class TestWizardPlayer(
    private val name: String
) : WizardPlayer {
    override fun userPublicID() = name
    override fun toString(): String {
        return name
    }


}