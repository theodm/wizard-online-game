package de.theodm.wizard.game.round

import de.theodm.wizard.*
import de.theodm.wizard.card.Joker
import de.theodm.wizard.card.WizardCard
import de.theodm.wizard.game.WizardGameSettings
import de.theodm.wizard.game.bets.Bets
import de.theodm.wizard.game.card.TrumpColor
import de.theodm.wizard.game.deck.Deck
import de.theodm.wizard.game.players.Players
import de.theodm.wizard.game.players.WizardPlayer
import de.theodm.wizard.game.stich.Stich

data class ImmutableRoundState(
    val gameSettings: WizardGameSettings,

    /**
     * Gibt alle Spieler in der Reihenfolge, in der sie sitzen an.
     */
    val players: Players,

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
     * Gibt die bereits erledigten Stiche der Spieler an.
     */
    val sticheOfPlayer: List<Stich>

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
    override val trumpColor: TrumpColor?,
    override val currentStich: Stich,
    override val sticheOfPlayer: List<Stich>,
    /**
     * Gibt in der ersten Runde an, welche Karten die anderen Spieler haben.
     */
    val cardsOfOtherPlayers: Map<WizardPlayer, List<WizardCard>>
) : RoundStateForPlayer

data class RoundStateForPlayerNormalRound(
    override val phase: OpenWizardRound.Phase,
    override val bets: Map<WizardPlayer, Int?>,
    override val currentPlayer: WizardPlayer,
    override val trumpColor: TrumpColor?,
    override val currentStich: Stich,
    override val sticheOfPlayer: List<Stich>,

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
    val startingPlayer: WizardPlayer,

    val currentPlayer: WizardPlayer,

    /**
     * Wieviele Karten werden an jeden Spieler verteilt
     * (um welche Runde handelt es sich?).
     */
    val numberOfCards: Int,

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

    val bets: Bets,
    val handsOfPlayers: HandsOfPlayers,

    private val oldStiche: List<Stich>,

    val currentStich: Stich,
) {
    enum class Phase {
        SelectTrumpPhase,
        BettingPhase,
        PlayingPhase,
        RoundEnded
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun phase(): Phase {
        if (trumpColor == null && (trumpCard is Joker))
            return Phase.SelectTrumpPhase

        if (bets.betsRemaining())
            return Phase.BettingPhase

        if (handsOfPlayers.isAllEmpty())
            return Phase.RoundEnded

        return Phase.PlayingPhase
    }

    private fun requirePhase(phase: Phase) {
        require(this.phase() == phase) { "Für diese Aktion muss sich das Spiel in der Phase $phase befinden. Es befindet sich aber in der Phase ${this.phase()}." }
    }

    private fun requireCurrentPlayer(accessingPlayer: WizardPlayer) {
        require(accessingPlayer == currentPlayer) { "Der Spieler $accessingPlayer ist gerade nicht am Zug. Am Zug ist $currentPlayer." }
    }

//    private fun pointsOfRoundForPlayer(player: WizardPlayer): Int? {
//        val stiche = sticheOfPlayers()[player] ?: return null
//        val bet = bets.toMap()[player]!!
//
//        if (stiche == bet) {
//            return stiche * 10 + 20
//        }
//
//        return -1 * abs(stiche - bet) * 10
//    }
//
//    private fun sumPointsOfRoundForPlayer(
//        player: WizardPlayer,
//        oldRounds: List<FinishedWizardRound>
//    ): Int? {
//        val pointsOfRoundForPlayer = pointsOfRoundForPlayer(player) ?: return null
//
//        if (this.numberOfCards == 1) {
//            return pointsOfRoundForPlayer
//        }
//
//        return oldRounds
//            .single { it.numberOfCards == this.numberOfCards - 1 }
//            .sumPointsOfRoundForPlayer(player, oldRounds) + pointsOfRoundForPlayer
//    }
//
//    fun sumPointsOfRound(oldRound: List<FinishedWizardRound>): Map<WizardPlayer, Int?> {
//        val result = mutableMapOf<WizardPlayer, Int?>()
//        for (player in players) {
//            result[player] = sumPointsOfRoundForPlayer(player, oldRound)
//        }
//
//        return result
//    }

    fun result(): FinishedWizardRound {
        requirePhase(Phase.RoundEnded)

        return FinishedWizardRound(
            numberOfCards = numberOfCards,
            trumpCard = trumpCard,
            trumpColor = trumpColor,
            bets = bets,
            sticheOfPlayer = oldStiche
        )
    }

    fun selectTrump(
        players: Players,
        player: WizardPlayer,
        trumpColor: TrumpColor
    ): OpenWizardRound {
        requireCurrentPlayer(player)
        requirePhase(Phase.SelectTrumpPhase)
        require(trumpColor != TrumpColor.None) { "Es kann als Trumpffarbe nur Rot, Gelb, Grün oder Blau gewählt werden." }

        return copy(
            trumpColor = trumpColor,
            currentPlayer = players.getPlayerAfter(player)
        )
    }

    fun placeBet(
        gameSettings: WizardGameSettings,
        players: Players,
        player: WizardPlayer,
        bet: Int
    ): OpenWizardRound {
        requireCurrentPlayer(player)
        requirePhase(Phase.BettingPhase)

        return copy(
            bets = bets.setBetForPlayer(
                gameSettings = gameSettings,
                player = player,
                bet = bet,
                numberOfCards = numberOfCards
            ),
            currentPlayer = players.getPlayerAfter(player)
        )
    }

    fun placeCardInFirstRound(
        players: Players,
        player: WizardPlayer
    ): OpenWizardRound {
        requireCurrentPlayer(player)
        requirePhase(Phase.PlayingPhase)
        require(numberOfCards == 1) { "Diese Methode kann nur in der ersten Runde aufgerufen werden, wenn die eigene Karte unbekannt ist." }

        return placeCard(
            players = players,
            player = player,
            card = handsOfPlayers.getLastRemainingCardForPlayer(player)
        )
    }

    fun placeCard(
        players: Players,
        player: WizardPlayer,
        card: WizardCard
    ): OpenWizardRound {
        requireCurrentPlayer(player)
        requirePhase(Phase.PlayingPhase)
        require(trumpColor != null)

        // Karte aus der Hand des Spielers wegnehmen.
        val newHandsOfPlayers = handsOfPlayers.playCardFromHand(player, card)

        // Ist der aktuelle Stich beendet und muss
        // somit ein neuer Stich gestartet werden?
        val currentStichIsFinished = currentStich.winningPlayer(players, trumpColor) != null

        // Dann erstellen wir einen Neuen
        var currentStich: Stich =
            if (currentStichIsFinished) Stich.emptyStich(currentPlayer) else currentStich

        // Und dem Stich diese Karte hinzufügen.
        currentStich = currentStich.playCard(players, handsOfPlayers[player], card)

        // Überprüfen, ob der Stich zu Ende ist und es einen Gewinner gibt.
        val winningPlayer = currentStich
            .winningPlayer(players, trumpColor)

        // Wenn ja, dann löschen neuen Stich
        // und aktualiseren die Daten über die bereits
        // gemachten Stiche des Gewinners
        if (winningPlayer != null) {
            return copy(
                handsOfPlayers = newHandsOfPlayers,
                oldStiche = oldStiche + currentStich,
                currentStich = currentStich,
                currentPlayer = winningPlayer
            )
        }

        return copy(
            handsOfPlayers = newHandsOfPlayers,
            currentStich = currentStich,
            currentPlayer = players.getPlayerAfter(player)
        )
    }

    fun immutableRoundState(gameSettings: WizardGameSettings, players: Players) = ImmutableRoundState(
        gameSettings = gameSettings,
        players = players,
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
                sticheOfPlayer = oldStiche,
                trumpColor = trumpColor,
                cardsOfOtherPlayers = cardsOfOtherPlayers,
                currentStich = currentStich
            )
        }

        return RoundStateForPlayerNormalRound(
            phase = phase(),
            bets = bets.toMap(),
            currentPlayer = currentPlayer,
            sticheOfPlayer = oldStiche,
            ownCards = this.handsOfPlayers[forPlayer],
            trumpColor = trumpColor,
            numberOfCardsInHandsOfPlayers = this.handsOfPlayers.mapValues { it.value.count() },
            currentStich = currentStich
        )
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
            val cardsOfPlayer = mutableMapOf<WizardPlayer, List<WizardCard>>()
            @Suppress("NAME_SHADOWING") var deck = deck

            for (player in players) {
                val (newDeck, drawnCards) =
                    deck.drawCards(numberOfCards)

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
                oldStiche = listOf(),
                // Wenn eine Trumpffarbe ausgewählt werden muss, dann macht das der letzte Spieler in der Reihe.
                currentPlayer = if (trumpCard != null && trumpCard.trumpColor() == null) players.getPlayerBefore(startingPlayer) else startingPlayer,
                trumpColor = if (trumpCard == null) TrumpColor.None else trumpCard.trumpColor(),
                currentStich = Stich.emptyStich(startingPlayer)
            )
        }
    }
}