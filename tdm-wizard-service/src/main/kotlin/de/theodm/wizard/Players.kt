package de.theodm.wizard

data class Players(
        private val origin: List<WizardPlayer>
): Iterable<WizardPlayer> by origin {
    init {
        require(origin.size >= 3) { "Es muss mindestens 3 Spieler geben." }
        require(origin.size <= 6) { "Es darf nicht mehr als 6 Spieler geben." }
        require(origin.containsUniqueItemsOnly()) { "Ein Spieler darf nur einmal vorkommen." }
    }

    fun getNextPlayerAfter(
            player: WizardPlayer
    ): WizardPlayer {
        require(origin.contains(player)) { TODO() }

        var nextPlayerIndex = origin.indexOf(player) + 1

        if (nextPlayerIndex == origin.size) {
            nextPlayerIndex = 0
        }

        return origin[nextPlayerIndex]
    }

    override fun toString(): String {
        return "$origin"
    }


}