package de.theodm.wizard

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject

class InMemoryWizardStore @Inject constructor() : WizardStorage {
    private val wizards: ConcurrentMap<String, WizardGameState> = ConcurrentHashMap()
    private val subject: BehaviorSubject<Pair<String, WizardGameState>> = BehaviorSubject.create()

    override fun wizardStream(lobbyCode: String): Observable<WizardGameState> {
        return subject
            .filter { it.first == lobbyCode }
            .map { it.second }
            .hide()
    }

    override fun getWizard(lobbyCode: String): WizardGameState {
        return wizards[lobbyCode]!!
    }

    override fun createWizard(lobbyCode: String, wizard: WizardGameState) {
        wizards[lobbyCode] = wizard

        subject.onNext(lobbyCode to wizard)
    }

    override fun updateWizard(lobbyCode: String, wizard: WizardGameState) {
        wizards[lobbyCode] = wizard

        subject.onNext(lobbyCode to wizard)
    }
}