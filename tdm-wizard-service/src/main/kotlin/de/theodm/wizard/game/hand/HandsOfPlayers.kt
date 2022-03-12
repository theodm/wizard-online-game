package de.theodm.wizard

import de.theodm.wizard.card.WizardCard
import de.theodm.wizard.game.players.WizardPlayer

class CardNotInHandException(
    player: WizardPlayer,
    card: WizardCard
): Exception("Der Spieler $player hat die Karte $card nicht auf der Hand.")

/**
 * Verwaltet die aktuellen Handkarten aller Mitspieler.
 */
data class HandsOfPlayers(
        private val origin: Map<WizardPlayer, List<WizardCard>>
): Map<WizardPlayer, List<WizardCard>> by origin  {
    override fun get(key: WizardPlayer): List<WizardCard> {
        require(origin.containsKey(key))

        return origin[key]!!
    }

    /**
     * Gibt an, ob kein Spieler mehr Karten hat.
     */
    fun isAllEmpty(): Boolean {
        return origin
                .flatMap { it.value }
                .isEmpty()
    }

    /**
     * Gibt die letzte Karte des Spielers zurück, falls er noch genau eine Karte
     * auf der Hand hat.
     */
    fun getLastRemainingCardForPlayer(player: WizardPlayer): WizardCard {
        val playerCards = origin[player] ?: throw IllegalArgumentException("Der Spieler $player existiert nicht.")

        if (playerCards.size != 1) {
            throw IllegalStateException("Diese Methode kann nur aufgerufen werden, wenn der Spieler genau eine Karte übrig hat.")
        }

        return playerCards[0]
    }

    /**
     * Lege eine Karte von der Hand ab.
     */
    fun playCardFromHand(player: WizardPlayer, card: WizardCard): HandsOfPlayers {
        // Der Spieler muss die Karte auf der Hand haben!
        val playerCards = origin[player] ?: throw IllegalArgumentException("Der Spieler $player existiert nicht.")

        if (!playerCards.contains(card)) {
            throw CardNotInHandException(player, card)
        }

        return copy(
                origin = origin.plus(player to playerCards - card)
        )
    }

}