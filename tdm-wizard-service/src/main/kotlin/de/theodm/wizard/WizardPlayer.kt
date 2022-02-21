package de.theodm.wizard

interface WizardPlayer {
    fun userPublicID(): String
    fun isBot(): Boolean
    fun botType(): String?
}
