package de.theodm.wizard.game.players

import de.theodm.wizard.containsUniqueItemsOnly

data class Players(
        private val origin: List<WizardPlayer>
): List<WizardPlayer> by origin {
    constructor(vararg args: WizardPlayer): this(args.toList())

    init {
        require(origin.size >= 3) { "Es muss mindestens 3 Spieler geben." }
        require(origin.size <= 6) { "Es darf nicht mehr als 6 Spieler geben." }
        require(origin.containsUniqueItemsOnly()) { "Ein Spieler darf nur einmal vorkommen." }
    }

    fun getPlayerBefore(
        player: WizardPlayer
    ): WizardPlayer {
        require(origin.contains(player)) { "Spieler $player ist in dieser Spieler-Liste nicht vorhanden." }

        var nextPlayerIndex = origin.indexOf(player) - 1

        if (nextPlayerIndex == -1) {
            nextPlayerIndex = origin.size - 1
        }

        return origin[nextPlayerIndex]
    }

    fun getPlayerAfter(
            player: WizardPlayer
    ): WizardPlayer {
        require(origin.contains(player)) { "Spieler $player ist in dieser Spieler-Liste nicht vorhanden." }

        var nextPlayerIndex = origin.indexOf(player) + 1

        if (nextPlayerIndex == origin.size) {
            nextPlayerIndex = 0
        }

        return origin[nextPlayerIndex]
    }

    override fun toString() = origin
        .toString()


}