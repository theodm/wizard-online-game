package de.theodm.wizard

/**
 * Verwaltet die Voraussagen der Mitspieler.
 */
data class Bets internal constructor(
    /**
     * Gibt für den jeweiligen, Spieler die Voraussage an. Jeder Spieler
     * ist immer in der Map enthalten. Der Wert kann jedoch null sein,
     * wenn noch keine Voraussage abgegeben wurde.
     */
    private val origin: Map<WizardPlayer, Int?>
) {
    companion object {
        /**
         * Erstellt das Voraussagen-Objekt. Dafür
         * muss dieses jeden Spieler kennen. Jeder Spieler hat
         * grundsätzlich erstmal keine Voraussage.
         */
        fun create(
            players: Players
        ): Bets {
            val bets: Map<WizardPlayer, Int?> = players
                .map { it to null }
                .toMap()

            return Bets(bets)
        }
    }

    fun toMap(): Map<WizardPlayer, Int?> {
        return origin;
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
    fun isLastBetter(
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
        player: WizardPlayer,
        numberOfCards: Int
    ): Int? {
        if (!isLastBetter(player)) {
            return null
        }

        return numberOfCards - origin
            .values
            .filterNotNull()
            .sum()
    }

    fun allowedBets(
        player: WizardPlayer,
        numberOfCards: Int
    ): List<Int> {
        val allBets = (1.. numberOfCards).toList()

        val notAllowedBet = notAllowedBet(player, numberOfCards)

        if (notAllowedBet == null) {
            return allBets
        }

        return allBets - notAllowedBet
    }

    /**
     * Setzt die Voraussage für einen Spieler.
     */
    fun setBetForPlayer(
        player: WizardPlayer,
        bet: Int,
        numberOfCards: Int
    ): Bets {
        require(origin.containsKey(player)) { "Der Spieler $player ist kein Teilnehmer dieses Spiels." }
        require(origin[player] == null) { "Der Spieler $player hat bereits einen Tipp abgegeben. Tipp war ${origin[player]}." }

        if (bet > numberOfCards) {
            throw BetBiggerThanNumberOfCardsException(bet, numberOfCards)
        }

        if (bet == notAllowedBet(player, numberOfCards)) {
            throw BetsAreEqualToNumberOfCardsException(player, bet, numberOfCards)
        }

        return copy(
            origin = origin.plus(player to bet)
        )
    }
}

class BetsAreEqualToNumberOfCardsException(player: WizardPlayer, bet: Int, numberOfCards: Int) :
    Exception("Der Spieler $player darf den Tipp $bet nicht abgeben, da die Summe der Voraussagen der Anzahl der Karten $numberOfCards entspricht.")

class BetBiggerThanNumberOfCardsException(
        bet: Int,
        numberOfCards: Int
) : Exception("Die gewettete Anzahl an Stichen ($bet) darf nicht größer sein als die Anzahl maximal möglicher Stiche ($numberOfCards).")