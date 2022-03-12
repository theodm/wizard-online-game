package de.theodm.wizard.game.stich

import de.theodm.wizard.game.players.Players
import de.theodm.wizard.game.card.TrumpColor
import de.theodm.wizard.game.players.WizardPlayer
import de.theodm.wizard.card.Joker
import de.theodm.wizard.card.Null
import de.theodm.wizard.card.NumberColorCard
import de.theodm.wizard.card.WizardCard

class PlayerMustPlayStichFarbeException(
    stichColor: StichColor,
    card: WizardCard
) : Exception("Spieler muss die Farbe $stichColor bekennen, hat aber stattdessen die Karte $card gelegt.")


fun List<Stich>.toPlayerSticheMap(
    players: Players,
    trumpColor: TrumpColor?
): MutableMap<WizardPlayer, Int> {
    val result = mutableMapOf<WizardPlayer, Int>()

    for (player in players) {
        if (trumpColor == null) {
            throw IllegalStateException("Um die Anzahl der gemachten Stiche eines Spielers abzufragen, ist es notwendig, die Trumpffarbe des Spiels zu kennen.")
        }

        result[player] = this
            .filter { player == it.winningPlayer(players, trumpColor) }
            .size
    }

    return result
}

/**
 * Repräsentiert einen Stich, der gerade auf dem Spielfeld ist und
 * unvollständig sein kann.
 */
data class Stich internal constructor(
    /**
     * Der Spieler, der die erste Karte des Stiches rauslegen muss.
     */
    val startPlayer: WizardPlayer,

    /**
     * Bisher gelegte Karten im Stich.
     */
    val cards: List<WizardCard>
) {
    override fun toString() = startPlayer.toString() + " -> " + cards.joinToString { " " }

    companion object {
        fun emptyStich(
            startPlayer: WizardPlayer
        ): Stich {
            return Stich(startPlayer, listOf())
        }
    }

    /**
     * Gibt ein Mapping von den Spielern zu ihren im Stich gelegten Karte zurück.
     */
    fun playerToCardMap(players: Players): Map<WizardPlayer, WizardCard> {
        val playerToCardMap = mutableMapOf<WizardPlayer, WizardCard>()

        var currentPlayer = this
            .startPlayer

        for (playerCard in this.cards) {
            playerToCardMap[currentPlayer] = playerCard

            currentPlayer = players.getPlayerAfter(currentPlayer)
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

    /**
     * Welche Karten von seiner Hand darf der Spieler
     * im aktuellen Stich-Status spielen?
     */
    fun allowedToPlayCards(
        players: Players,
        handOfPlayer: List<WizardCard>
    ): List<WizardCard> {
        // In einem leeren Stich darf immer eine beliebige Karte gespielt werden.
        if (cards.isEmpty() || cards.size == players.count())
            return handOfPlayer

        val stichColor = stichColor

        if (stichColor == StichColor.Any) {
            return handOfPlayer
        }

        // Hat der Spieler noch eine Karte der Stichfarbe?
        // Dann muss er bekennen!
        val hasCardsOfStichColor = handOfPlayer
            .filterIsInstance<NumberColorCard>()
            .any { stichColor == StichColor.fromCardColor(it.color) }

        if (!hasCardsOfStichColor) {
            return handOfPlayer
        }

        return handOfPlayer
            .filter { it is Joker || it is Null || it is NumberColorCard && stichColor == StichColor.fromCardColor(it.color) }
    }

    /**
     * Darf der Spieler von seiner Hand
     * die Karete spielen?
     */
    fun isAllowedToPlayCard(
        players: Players,
        handOfPlayer: List<WizardCard>,
        card: WizardCard
    ) = card in allowedToPlayCards(players, handOfPlayer)

    /**
     * @param handOfPlayer Enthält die Karten, die der Spieler gerade in der Hand hält. Es ist ohne Bedeutung, ob die Karte [card], die
     * er spielt noch in dieser Liste enthalten ist.
     *
     * @param card Karte, die der Spieler spielt.
     */
    fun playCard(
        players: Players,
        handOfPlayer: List<WizardCard>,
        card: WizardCard
    ): Stich {
        if (!isAllowedToPlayCard(players, handOfPlayer, card)) {
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
        players: Players,
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

            currentPlayer = players.getPlayerAfter(currentPlayer)
        }

        return winningPlayer
    }

    /**
     * Gibt die Position eines Spielers innerhalb dieses Stiches
     * an.
     */
    fun positionOfPlayerInStich(
        players: Players,
        player: WizardPlayer
    ): Int {
        var index = 0
        var currentPlayer = startPlayer
        while (currentPlayer != player) {
            currentPlayer = players.getPlayerAfter(currentPlayer)

            index++
        }

        return index
    }
}

