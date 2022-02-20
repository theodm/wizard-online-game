package de.theodm.wizard.stich

import de.theodm.wizard.Players
import de.theodm.wizard.TrumpColor
import de.theodm.wizard.WizardPlayer
import de.theodm.wizard.card.Joker
import de.theodm.wizard.card.Null
import de.theodm.wizard.card.NumberColorCard
import de.theodm.wizard.card.WizardCard

class PlayerMustPlayStichFarbeException(
        stichColor: StichColor,
        card: WizardCard
) : Exception("Spieler muss die Farbe $stichColor bekennen, hat aber stattdessen die Karte $card gelegt.")

data class Stich internal constructor(
        /**
         * Alle Spieler dieser Runde.
         */
        val players: Players,

        /**
         * Der Spieler, der die erste Karte des Stiches rauslegen muss.
         */
        val startPlayer: WizardPlayer,

        /**
         * Bisher gelegte Karten im Stich.
         */
        val cards: List<WizardCard>
) {
    companion object {
        fun emptyStich(
                players: Players,
                startPlayer: WizardPlayer
        ): Stich {
            return Stich(players, startPlayer, listOf())
        }
    }

    fun playerToCardMap(): Map<WizardPlayer, WizardCard> {
        val playerToCardMap = mutableMapOf<WizardPlayer, WizardCard>()

        var currentPlayer = this
            .startPlayer

        for (playerCard in this.cards) {
            playerToCardMap[currentPlayer] = playerCard

            currentPlayer = players.getNextPlayerAfter(currentPlayer)
        }

        return playerToCardMap
    }

    /**
     * Gibt die Farbe des Stichs zurück, also die
     * Farbe, die innerhalb dieses Stichs bekannt werden muss.
     */
    val stichColor: StichColor
        get() {
            for (card in cards) {
                // Wenn am Anfang eines Stichs Narren liegen, bleibt
                // die Farbe zunächst unklar.
                if (card is Null) {
                    continue
                }

                // Wenn ein Joker am Anfang gespielt wurde, dann
                // dürfen beliebige Karten gespielt werden.
                if (card is Joker) {
                    return StichColor.Any
                }

                // Wenn eine Farbkarte gespielt wurde, dann gilt diese
                // als Farbe für den verbleibenden Stich.
                if (card is NumberColorCard) {
                    return StichColor.fromCardColor(card.color)
                }
            }

            // Es wurde noch keine Karte, oder nur Narren gespielt
            return StichColor.Any
        }

    fun toList(): List<WizardCard> {
        return cards
    }

    fun isAllowedToPlayCard(
        handOfPlayer: List<WizardCard>,
        card: WizardCard
    ): Boolean {
        // In einem leeren Stich darf immer eine beliebige Karte gespielt werden.
        if (cards.isEmpty() || cards.size == players.count())
            return true

        // Joker und Narren dürfen immer gespielt werden.
        if (card is Joker || card is Null)
            return true

        require(card is NumberColorCard)

        val stichColor = stichColor

        // Es darf jede Karte gespielt werden
        if (stichColor == StichColor.Any) {
            return true
        }

        // Der Spieler bekennt die Stich-Farbe.
        if (stichColor == StichColor.fromCardColor(card.color)) {
            return true
        }

        // Spieler bekennt die Farbe nicht.
        //
        // Hat er noch eine Karte der Stichfarbe?
        // Dann muss er bekennen!
        val hasCardsOfStichColor = handOfPlayer
            .filterIsInstance<NumberColorCard>()
            .any { stichColor == StichColor.fromCardColor(it.color) }

        if (hasCardsOfStichColor) {
            return false
        }

        return true
    }

    /**
     * @param handOfPlayer Enthält die Karten, die der Spieler gerade in der Hand hält. Es ist ohne Bedeutung, ob die Karte [card], die
     * er spielt noch in dieser Liste enthalten ist.
     *
     * @param card Karte, die der Spieler spielt.
     */
    fun playCard(
            handOfPlayer: List<WizardCard>,
            card: WizardCard
    ): Stich {
        if (!isAllowedToPlayCard(handOfPlayer, card)) {
            throw PlayerMustPlayStichFarbeException(stichColor, card)
        }

        // Ansonsten nicht.
        return copy(
                cards = cards + card
        )
    }

    /**
     * Gibt den Spieler zurück, der den Stich gewonnen hat. Falls der Stich noch nicht beendet wurde,
     * dann wird null zurückgegeben.
     */
    fun winningPlayer(
            trumpColor: TrumpColor
    ): WizardPlayer? {
        /**
         * Stich ist noch nicht zu Endec.
         */
        if (cards.size != players.count()) {
            return null
        }

        // Aktueller Spieler des Schleifendurchlaufs
        var currentPlayer = startPlayer

        // Der Spieler, der die höchstwertige Karte gespielt hat und
        // damit den Stich gewinnt.
        var winningPlayer = startPlayer

        // Grundsätzlich ist erstmal die erste Karte
        // die höchste Karte, außer eine nachfolgende ist höherwertig.
        var previousHighestCard: WizardCard = cards[0]

        for (card in cards) {
            if (card.isHigherThan(previousHighestCard, trumpColor)) {
                previousHighestCard = card
                winningPlayer = currentPlayer
            }

            currentPlayer = players.getNextPlayerAfter(currentPlayer)
        }

        return winningPlayer
    }
}

