package de.theodm.wizard

import de.theodm.wizard.card.WizardCard
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

data class WizardGameState internal constructor(
        val hostPlayer: WizardPlayer,
        val players: Players,
        val oldRounds: List<FinishedWizardRound>,

        /**
         * Aktuelle Runde, oder null,
         * wenn das Spiel beendet ist.
         */
        val currentRound: OpenWizardRound?
) {
    fun finishAndStartNewRoundWithDeck(
        player: WizardPlayer,
        deck: Deck
    ): WizardGameState {
        // ToDo: Requirement
        require(currentRound != null)

        val nextRoundNumberOfCards = currentRound.numberOfCards + 1

        if (nextRoundNumberOfCards > (60 / players.count())) {
            return copy(
                oldRounds = oldRounds + currentRound.result(),
                currentRound = null
            )
        }

        return copy(
            oldRounds = oldRounds + currentRound.result(),
            currentRound = OpenWizardRound.startNewRoundWithDeck(
                nextRoundNumberOfCards,
                players,
                players.getNextPlayerAfter(currentRound.startingPlayer),
                deck
            )
        )
    }

    fun finishAndStartNewRound(
            player: WizardPlayer
    ): WizardGameState {
       return finishAndStartNewRoundWithDeck(player, Deck.shuffled())
    }

    fun selectTrump(
            player: WizardPlayer,
            trumpColor: TrumpColor
    ): WizardGameState {
        return copy(
                currentRound = currentRound!!.selectTrump(player, trumpColor)
        )
    }

    fun placeSingleCard(
        player: WizardPlayer
    ): WizardGameState {
        return copy(
            currentRound = currentRound!!.placeSingleCard(player)
        )
    }

    fun placeCard(
            player: WizardPlayer,
            card: WizardCard
    ): WizardGameState {
        return copy(
                currentRound = currentRound!!.placeCard(player, card)
        )
    }

    fun placeBet(
            player: WizardPlayer,
            bet: Int
    ): WizardGameState {
        return copy(
                currentRound = currentRound!!.placeBet(player, bet)
        )
    }

    companion object {
        fun initial(
                hostPlayer: WizardPlayer,
                players: List<WizardPlayer>
        ): WizardGameState {
            val players = Players(players)

            return WizardGameState(
                    hostPlayer = hostPlayer,
                    players = players,
                    oldRounds = listOf(),
                    currentRound = OpenWizardRound.startNewRound(1, players, players.first())
            )
        }

        @Deprecated("Nur für Testzwecke aufrufen.", level = DeprecationLevel.WARNING)
        fun initialWithDeck(
            hostPlayer: WizardPlayer,
            players: List<WizardPlayer>,
            deck: Deck
        ): WizardGameState {
            val players = Players(players)

            return WizardGameState(
                hostPlayer = hostPlayer,
                players = players,
                oldRounds = listOf(),
                currentRound = OpenWizardRound.startNewRoundWithDeck(1, players, players.first(), deck),
            )
        }

        @Deprecated("Nur für Testzwecke aufrufen.", level = DeprecationLevel.WARNING)
        fun initialWithDeckAndRound(
            hostPlayer: WizardPlayer,
            players: List<WizardPlayer>,
            deck: Deck,
            numberOfCards: Int
        ): WizardGameState {
            val players = Players(players)

            return WizardGameState(
                hostPlayer = hostPlayer,
                players = players,
                oldRounds = listOf(),
                currentRound = OpenWizardRound.startNewRoundWithDeck(numberOfCards, players, players.first(), deck),
            )
        }



    }

}