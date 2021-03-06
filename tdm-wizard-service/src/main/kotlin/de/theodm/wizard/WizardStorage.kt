package de.theodm.wizard

import de.theodm.wizard.game.WizardGameState
import io.reactivex.rxjava3.core.Observable

interface WizardStorage {
    fun createWizard(lobbyCode: String, wizard: WizardGameState)

    fun getWizard(lobbyCode: String): WizardGameState
    fun updateWizard(lobbyCode: String, wizard: WizardGameState)
    fun wizardStream(lobbyCode: String): Observable<WizardGameState>
    fun wizardStream(): Observable<Pair<String, WizardGameState>>
}
