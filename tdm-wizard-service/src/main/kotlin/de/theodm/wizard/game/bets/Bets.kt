package de.theodm.wizard.game.bets

import de.theodm.wizard.game.WizardGameSettings
import de.theodm.wizard.game.players.Players
import de.theodm.wizard.game.players.WizardPlayer

/**
 * Verwaltet die Voraussagen der Mitspieler.
 */
data class Bets constructor(
    /**
     * Gibt für den jeweiligen, Spieler die Voraussage an. Jeder Spieler
     * ist immer in der Map enthalten. Der Wert kann jedoch null sein,
     * wenn noch keine Voraussage abgegeben wurde.
     */
    private val origin: Map<WizardPlayer, Int?>
) : Map<WizardPlayer, Int?> by origin {
    companion object {
        /**
         * Erstellt das Voraussagen-Objekt. Dafür
         * muss dieses jeden Spieler kennen. Jeder Spieler hat
         * grundsätzlich erstmal keine Voraussage.
         */
        fun create(
            players: Players
        ): Bets {
            val bets = players
                .associateWith { null }

            return Bets(bets)
        }
    }

    /**
     * Gibt an, ob es noch Spieler gibt, die
     * einen Tipp abgeben müssen.
     */
    fun betsRemaining(): Boolean {
        return origin
            .filter { it.value == null }
            .isNotEmpty()
    }

    /**
     * Gibt an, ob der übergebene Spieler
     * der letzte Spieler ist, der einen
     * Tipp abzugeben hat. Das ist relevant für
     * die Regel, dass die Anzahl der Stiche nicht
     * aufgehen darf.
     */
    private fun isLastBetter(
        player: WizardPlayer
    ): Boolean {
        require(origin.containsKey(player)) { "Der Spieler $player ist kein Teilnehmer dieses Spiels." }

        val otherPlayersPlacedBet = origin
            .filter { it.key != player }
            .filter { it.value == null }
            .isEmpty()

        val playerDidNotPlaceBetYet = origin[player] == null

        return otherPlayersPlacedBet && playerDidNotPlaceBetYet
    }

    private fun notAllowedBet(
        gameSettings: WizardGameSettings,
        player: WizardPlayer,
        numberOfCards: Int
    ): Int? {
        if (gameSettings.hiddenBets) {
            return null
        }

        // Einschränkungen kann es nur für den letzten Spieler
        // in der Reihenfolge geben.
        if (!isLastBetter(player)) {
            return null
        }

        return numberOfCards - origin
            .values
            .filterNotNull()
            .sum()
    }

    fun allowedBets(
        gameSettings: WizardGameSettings,
        player: WizardPlayer,
        numberOfCards: Int
    ): List<Int> {
        val allBets = (0.. numberOfCards).toList()

        val notAllowedBet = notAllowedBet(
            gameSettings,
            player,
            numberOfCards
        )

        if (notAllowedBet == null) {
            return allBets
        }

        return allBets - notAllowedBet
    }

    /**
     * Setzt die Voraussage für einen Spieler.
     */
    fun setBetForPlayer(
        gameSettings: WizardGameSettings,
        player: WizardPlayer,
        bet: Int,
        numberOfCards: Int
    ): Bets {
        require(origin.containsKey(player)) { "Der Spieler $player ist kein Teilnehmer dieses Spiels." }
        require(origin[player] == null) { "Der Spieler $player hat bereits einen Tipp abgegeben. Tipp war ${origin[player]}." }
        require(bet <= numberOfCards) { "Die gewettete Anzahl an Stichen ($bet) darf nicht größer sein als die Anzahl maximal möglicher Stiche ($numberOfCards)." }

        if (bet == notAllowedBet(gameSettings, player, numberOfCards)) {
            throw IllegalStateException("Der Spieler $player darf den Tipp $bet nicht abgeben, da die Summe der Voraussagen der Anzahl der Karten $numberOfCards entspricht.")
        }

        return copy(
            origin = origin.plus(player to bet)
        )
    }
}