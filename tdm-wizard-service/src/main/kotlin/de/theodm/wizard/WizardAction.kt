package de.theodm.wizard

import de.theodm.wizard.card.WizardCard

sealed class WizardAction(
        val actionName: String
) {
    class GameStarted : WizardAction(
            GameStarted::class.simpleName!!
    )

    class RoundStarted(
            /**
             * Daten über die Runde, die die ganze Runde über
             * gleich bleiben.
             */
            val immutableRoundState: ImmutableRoundState,

            /**
             * Daten über die Runde, aus der Sicht eines einzelnen
             * Spielers. Er sieht beispielsweise nur seine Karten.
             */
            val roundStateForPlayer: RoundStateForPlayer
    ) : WizardAction(
            RoundStarted::class.simpleName!!
    )

    class WACardRevealsFirstRound(
            val playersToCards: Map<WizardPlayer, List<WizardCard>>
    )
}