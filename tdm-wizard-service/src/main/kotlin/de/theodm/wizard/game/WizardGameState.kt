package de.theodm.wizard.game

import de.theodm.wizard.card.WizardCard
import de.theodm.wizard.game.bets.Bets
import de.theodm.wizard.game.card.TrumpColor
import de.theodm.wizard.game.deck.Deck
import de.theodm.wizard.game.players.Players
import de.theodm.wizard.game.players.WizardPlayer
import de.theodm.wizard.game.round.FinishedWizardRound
import de.theodm.wizard.game.round.OpenWizardRound
import de.theodm.wizard.game.stich.Stich
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

data class WizardGameSettings(
    val hiddenBets: Boolean = false
)

data class WizardGameState internal constructor(
    val gameSettings: WizardGameSettings,

    /**
     * Spieler, die sich im Spiel befinden.
     */
    val players: Players,

    val previousRounds: List<FinishedWizardRound>,

    /**
     * Aktuelle Runde, oder null,
     * wenn das Spiel beendet ist.
     */
    val currentRound: OpenWizardRound?
) {

    fun finishRound(): WizardGameState {
        require(currentRound != null)
        require(currentRound.phase() == OpenWizardRound.Phase.RoundEnded)

        return copy(
            previousRounds = previousRounds + currentRound.result(),
            currentRound = null
        )
    }

    fun isEnded(): Boolean {
        val nextRoundNumberOfCards = previousRounds
            .last()
            .numberOfCards + 1

        return nextRoundNumberOfCards > (60 / players.count())
    }

    fun startNewRound(deck: Deck = Deck.shuffled()): WizardGameState {
        require(currentRound == null)

        val nextRoundNumberOfCards = previousRounds
            .last()
            .numberOfCards + 1

        if (isEnded()) {
            return this
        }

        return copy(
            currentRound = OpenWizardRound.startNewRound(
                nextRoundNumberOfCards,
                players,
                players.getPlayerAfter(previousRounds.last().sticheOfPlayer[0].startPlayer)
            )
        )
    }

    fun selectTrump(
        player: WizardPlayer,
        trumpColor: TrumpColor
    ): WizardGameState {
        require(currentRound != null)

        return copy(
            currentRound = currentRound.selectTrump(players, player, trumpColor)
        )
    }

    fun placeCardInFirstRound(
        player: WizardPlayer
    ): WizardGameState {
        require(currentRound != null)

        return copy(
            currentRound = currentRound.placeCardInFirstRound(players, player)
        )
    }

    fun placeCard(
        player: WizardPlayer,
        card: WizardCard
    ): WizardGameState {
        require(currentRound != null)

        return copy(
            currentRound = currentRound.placeCard(players, player, card)
        )
    }

    fun placeBet(
        player: WizardPlayer,
        bet: Int
    ): WizardGameState {
        require(currentRound != null)

        return copy(
            currentRound = currentRound.placeBet(gameSettings, players, player, bet)
        )
    }

    companion object {
        fun initialRound2(
            gameSettings: WizardGameSettings,
            players: List<WizardPlayer>
        ): WizardGameState {
            return WizardGameState(
                gameSettings,
                players = Players(players),
                previousRounds = listOf(FinishedWizardRound(1, null, TrumpColor.Green, Bets(players.associateWith { 0 }), listOf())),
                currentRound = OpenWizardRound.startNewRound(2, Players(players), players.first())
            )
        }

        fun initial(
            gameSettings: WizardGameSettings,
            players: List<WizardPlayer>
        ): WizardGameState {
            val players = Players(players)

            return WizardGameState(
                gameSettings = gameSettings,
                players = players,
                previousRounds = listOf(),
                currentRound = OpenWizardRound.startNewRound(1, players, players.first())
            )
        }

        @Deprecated("Nur für Testzwecke aufrufen.", level = DeprecationLevel.WARNING)
        fun initialWithDeck(
            gameSettings: WizardGameSettings,
            players: List<WizardPlayer>,
            deck: Deck
        ): WizardGameState {
            val players = Players(players)

            return WizardGameState(
                gameSettings = gameSettings,
                players = players,
                previousRounds = listOf(),
                currentRound = OpenWizardRound.startNewRoundWithDeck(1, players, players.first(), deck),
            )
        }

        @Deprecated("Nur für Testzwecke aufrufen.", level = DeprecationLevel.WARNING)
        fun initialWithDeckAndRound(
            gameSettings: WizardGameSettings,
            players: List<WizardPlayer>,
            deck: Deck,
            numberOfCards: Int
        ): WizardGameState {
            val players = Players(players)

            return WizardGameState(
                gameSettings = gameSettings,
                players = players,
                previousRounds = listOf(),
                currentRound = OpenWizardRound.startNewRoundWithDeck(numberOfCards, players, players.first(), deck),
            )
        }


    }

}