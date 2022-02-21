package de.theodm.wizard

import de.theodm.wizard.NotPlayersTurnException
import de.theodm.wizard.card.Joker
import de.theodm.wizard.card.WizardCard
import de.theodm.wizard.stich.Stich
import kotlin.math.abs

data class ImmutableRoundState(
        /**
         * Gibt alle Spieler in der Reihenfolge, in der sie sitzen an.
         */
        val players: List<WizardPlayer>,

        /**
         * Gibt an, wieviele Karten ein Spieler in der Hand hat.
         */
        val numberOfCards: Int,

        /**
         * Gibt die obenliegende Trumpfkarte an.
         */
        val trumpCard: WizardCard?,
)

interface RoundStateForPlayer {
    /**
     * Aktuelle Phase der Runde.
     */
    val phase: OpenWizardRound.Phase

    /**
     * Gibt die abgegebenen Voraussagen der Spieler an.
     */
    val bets: Map<WizardPlayer, Int?>

    /**
     * Gibt den Spieler an, der gerade an der Reihe ist.
     */
    val currentPlayer: WizardPlayer

    /**
     * Gibt die aktuelle Anzahl an Stichen an, die ein Spieler
     * bereits bekommen hat.
     */
    val sticheOfPlayer: Map<WizardPlayer, Int>

    /**
     * Gibt die aktuelle Trumpffarbe an, oder
     * null, falls der aktuelle Spieler sie auswählen muss.
     */
    val trumpColor: TrumpColor?

    /**
     * Aktueller Stich.
     */
    val currentStich: Stich
}

data class RoundStateForPlayerFirstRound(
        override val phase: OpenWizardRound.Phase,
        override val bets: Map<WizardPlayer, Int?>,
        override val currentPlayer: WizardPlayer,
        override val sticheOfPlayer: Map<WizardPlayer, Int>,
        override val trumpColor: TrumpColor?,
        override val currentStich: Stich,

        /**
         * Gibt in der ersten Runde an, welche Karten die anderen Spieler haben.
         */
        val cardsOfOtherPlayers: Map<WizardPlayer, List<WizardCard>>
) : RoundStateForPlayer

data class RoundStateForPlayerNormalRound(
        override val phase: OpenWizardRound.Phase,
        override val bets: Map<WizardPlayer, Int?>,
        override val currentPlayer: WizardPlayer,
        override val sticheOfPlayer: Map<WizardPlayer, Int>,
        override val trumpColor: TrumpColor?,
        override val currentStich: Stich,

        /**
         * Gibt die eigenen Karten des Spielers an.
         */
        val ownCards: List<WizardCard>,

        /**
         * Gibt an, wieviele Karten die anderen Spieler in der Hand haben.
         */
        val numberOfCardsInHandsOfPlayers: Map<WizardPlayer, Int>
) : RoundStateForPlayer


/**
 * Repräsentiert eine aktuell laufende Runde.
 */
data class OpenWizardRound internal constructor(
        /**
         * Startspieler der Runde.
         */
        val startingPlayer: WizardPlayer,

        val numberOfCards: Int,
        private val players: Players,

        /**
         * Aktuelle Trumpfkarte.
         */
        private val trumpCard: WizardCard?,

        /**
         * Aktuelle Trumpfkarte, sie muss eigenständig nachverfolgt werden,
         * da, sie ja auch vom Benutzer festgelegt werden kann, falls die
         * Trumpfkarte ein Zauberer ist.
         *
         * Wenn die Variable null ist, dann bedeutet es, dass der Startspieler
         * noch eine Farbe auswählen muss.
         */
        private val trumpColor: TrumpColor?,

        /**
         * Aktueller Spieler, der an der Reihe ist.
         */
        val currentPlayer: WizardPlayer,

        private val bets: Bets,
        private val handsOfPlayers: HandsOfPlayers,
        private val sticheOfPlayer: Map<WizardPlayer, Int>,
        private val currentStich: Stich,
) {
    enum class Phase {
        SelectTrumpPhase,
        BettingPhase,
        PlayingPhase,
        RoundEnded
    }

    private fun phase(): Phase {
        if (trumpColor == null && (trumpCard != null && trumpCard.isJoker()))
            return Phase.SelectTrumpPhase

        if (bets.betsRemaining())
            return Phase.BettingPhase

        if (handsOfPlayers.isAllEmpty())
            return Phase.RoundEnded

        return Phase.PlayingPhase
    }

    private fun requirePhase(phase: Phase) {
        if (this.phase() != phase)
            throw WrongPhaseException(phase(), phase)
    }

    private fun requireCurrentPlayer(accessingPlayer: WizardPlayer) {
        if (accessingPlayer != currentPlayer)
            throw NotPlayersTurnException(accessingPlayer, currentPlayer)
    }


    private fun pointsOfRoundForPlayer(player: WizardPlayer): Int? {
        val stiche = sticheOfPlayer[player] ?: return null
        val bet = bets.toMap()[player]!!

        if (stiche == bet) {
            return stiche * 10 + 20
        }

        return -1 * abs(stiche - bet) * 10
    }

    private fun sumPointsOfRoundForPlayer(
        player: WizardPlayer,
        oldRounds: List<FinishedWizardRound>
    ): Int? {
        val pointsOfRoundForPlayer = pointsOfRoundForPlayer(player) ?: return null

        if (this.numberOfCards == 1) {
            return pointsOfRoundForPlayer
        }

        return oldRounds
            .single { it.numberOfCards == this.numberOfCards - 1 }
            .sumPointsOfRoundForPlayer(player, oldRounds) + pointsOfRoundForPlayer
    }

    fun sumPointsOfRound(oldRound: List<FinishedWizardRound>): Map<WizardPlayer, Int?> {
        val result = mutableMapOf<WizardPlayer, Int?>()
        for (player in players) {
            result[player] = sumPointsOfRoundForPlayer(player, oldRound)
        }

        return result
    }

    fun result(): FinishedWizardRound {
        requirePhase(Phase.RoundEnded)

        return FinishedWizardRound(
                numberOfCards,
                bets.toMap() as Map<WizardPlayer, Int>,
                sticheOfPlayer
        )
    }

    fun selectTrump(
            player: WizardPlayer,
            trumpColor: TrumpColor
    ): OpenWizardRound {
        requireCurrentPlayer(player)
        requirePhase(Phase.SelectTrumpPhase)
        require(trumpColor != TrumpColor.None) { "Es kann als Trumpffarbe nur Rot, Gelb, Grün oder Blau gewählt werden." };

        return copy(
                trumpColor = trumpColor
        )
    }

    fun placeBet(
            player: WizardPlayer,
            bet: Int
    ): OpenWizardRound {
        requireCurrentPlayer(player)
        requirePhase(Phase.BettingPhase)

        return copy(
                bets = bets.setBetForPlayer(player, bet, numberOfCards),
                currentPlayer = players.getNextPlayerAfter(player)
        )
    }

    fun placeCardInFirstRound(
        player: WizardPlayer
    ) {
        requireCurrentPlayer(player)
        requirePhase(Phase.PlayingPhase)
        require(numberOfCards == 1) { "Diese Methode kann nur in der ersten Runde aufgerufen werden, wenn die eigene Karte unbekannt ist."}

        placeCard(player, handsOfPlayers.getLastRemainingCardForPlayer(player))
    }

    fun placeCard(
            player: WizardPlayer,
            card: WizardCard
    ): OpenWizardRound {
        requireCurrentPlayer(player)
        requirePhase(Phase.PlayingPhase)

        // Karte aus der Hand des Spielers wegnehmen.
        val handsOfPlayers = handsOfPlayers.playCardFromHand(player, card)

        // Ist der aktuelle Stich beendet und muss
        // somit ein neuer Stich gestartet werden?
        val currentStichIsFinished = currentStich.winningPlayer(trumpColor ?: SHOULD_NOT_HAPPEN()) != null

        // Dann erstellen wir einen Neuen
        var currentStich = if (currentStichIsFinished) Stich.emptyStich(players, currentPlayer) else currentStich

        // Und dem Stich diese Karte hinzufügen.
        currentStich = currentStich.playCard(handsOfPlayers[player] ?: SHOULD_NOT_HAPPEN(), card)

        // Überprüfen, ob der Stich zu Ende ist und es einen Gewinner gibt.
        val winningPlayer = currentStich
                .winningPlayer(trumpColor ?: SHOULD_NOT_HAPPEN())

        // Wenn ja, dann starten wir einen neuen Stich
        // und aktualiseren die Daten über die bereits
        // gemachten Stiche des Gewinners
        if (winningPlayer != null) {
            val newNumberOfStiche = (sticheOfPlayer[winningPlayer] ?: SHOULD_NOT_HAPPEN()) + 1

            return copy(
                    handsOfPlayers = handsOfPlayers,
                    sticheOfPlayer = sticheOfPlayer + (winningPlayer to newNumberOfStiche),
                    currentStich = currentStich,
                    currentPlayer = winningPlayer
            )
        }

        return copy(
                handsOfPlayers = handsOfPlayers,
                currentStich = currentStich,
                currentPlayer = players.getNextPlayerAfter(player)
        );
    }

    fun immutableRoundState() = ImmutableRoundState(
            players = players.toList(),
            numberOfCards = numberOfCards,
            trumpCard = trumpCard
    )

    /**
     * Gibt die aktuelle Sicht des Spielers zurück. Das heißt nur
     * den Teil des Spielfelds, den er auch sehen kann.
     */
    fun viewForPlayer(forPlayer: WizardPlayer): RoundStateForPlayer {
        // Sonderbehandlung: In Runde 1 kann man nur die Karten der Gegner sehen.
        if (numberOfCards == 1) {
            val cardsOfOtherPlayers = handsOfPlayers
                    .filter { playerCards -> playerCards.key != forPlayer }
                    .mapValues { playerCards -> playerCards.value }

            return RoundStateForPlayerFirstRound(
                    phase = phase(),
                    bets = bets.toMap(),
                    currentPlayer = currentPlayer,
                    sticheOfPlayer = sticheOfPlayer,
                    trumpColor = trumpColor,
                    cardsOfOtherPlayers = cardsOfOtherPlayers,
                    currentStich = currentStich
            )
        }

        return RoundStateForPlayerNormalRound(
                phase = phase(),
                bets = bets.toMap(),
                currentPlayer = currentPlayer,
                sticheOfPlayer = sticheOfPlayer,
                ownCards = this.handsOfPlayers[forPlayer] ?: SHOULD_NOT_HAPPEN(),
                trumpColor = trumpColor,
                numberOfCardsInHandsOfPlayers = this.handsOfPlayers.mapValues { it.value.count() },
                currentStich = currentStich
        )
    }

    fun placeSingleCard(player: WizardPlayer): OpenWizardRound {
        return placeCard(player, this.handsOfPlayers[player]!![0])
    }

    companion object {
        internal fun startNewRound(
                numberOfCards: Int,
                players: Players,
                startingPlayer: WizardPlayer
        ) = startNewRoundWithDeck(
                numberOfCards,
                players,
                startingPlayer,
                Deck.shuffled()
        )

        /**
         * Nur für Testzwecke direkt aufrufen.
         */
        internal fun startNewRoundWithDeck(
                numberOfCards: Int,
                players: Players,
                startingPlayer: WizardPlayer,
                deck: Deck
        ): OpenWizardRound {
            require(numberOfCards in 1..20) { "Die Anzahl der Karten darf nur zwischen 1 und 20 liegen (war aber: $numberOfCards)" }

            val bets = Bets.create(players)
            val sticheOfPlayer: Map<WizardPlayer, Int> = players
                    .map { it to 0 }
                    .toMap()

            val cardsOfPlayer = mutableMapOf<WizardPlayer, List<WizardCard>>()

            var deck = deck

            for (player in players) {
                val (newDeck, drawnCards) = deck.drawCards(numberOfCards)

                cardsOfPlayer[player] = drawnCards

                deck = newDeck
            }

            val trumpCard = if (!deck.hasCards()) {
                // Letzte Runde: Es wird keine Trumpfkarte mehr aufgedeckt
                null
            } else {
                deck.drawCards(1).returnValue1.single()
            }

            return OpenWizardRound(
                    startingPlayer = startingPlayer,
                    numberOfCards = numberOfCards,
                    bets = bets,
                    trumpCard = trumpCard,
                    handsOfPlayers = HandsOfPlayers(cardsOfPlayer),
                    sticheOfPlayer = sticheOfPlayer,
                    currentPlayer = startingPlayer,
                    players = players,
                    trumpColor = if (trumpCard == null) TrumpColor.None else trumpCard.trumpColor(),
                    currentStich = Stich.emptyStich(players, startingPlayer)
            )
        }
    }
}

class WrongPhaseException(actualPhase: OpenWizardRound.Phase, expectedPhase: OpenWizardRound.Phase) : Exception("Für diese Aktion muss sich das Spiel in der Phase $expectedPhase befinden. Es befindet sich aber in der Phase $actualPhase.")
class NotPlayersTurnException(
        accessingPlayer: WizardPlayer,
        currentPlayer: WizardPlayer,
) : Exception("Der Spieler $accessingPlayer ist gerade nicht am Zug. Am Zug ist $currentPlayer.")