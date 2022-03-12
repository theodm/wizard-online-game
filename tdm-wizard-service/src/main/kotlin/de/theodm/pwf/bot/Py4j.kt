package de.theodm.pwf.bot

import de.theodm.wizard.*
import de.theodm.wizard.card.*
import de.theodm.wizard.game.WizardGameSettings
import de.theodm.wizard.game.WizardGameState
import de.theodm.wizard.game.card.TrumpColor
import de.theodm.wizard.game.players.WizardPlayer
import de.theodm.wizard.game.round.OpenWizardRound
import de.theodm.wizard.game.round.RoundStateForPlayerFirstRound
import py4j.GatewayServer
import java.io.StringWriter
import kotlin.random.Random.Default.nextInt


fun mapTrumpCardColor(trumpCard: WizardCard?) = when {
    trumpCard is Null -> 0
    trumpCard is Joker -> 1
    trumpCard is NumberColorCard && trumpCard.color == CardColor.Yellow -> 2
    trumpCard is NumberColorCard && trumpCard.color == CardColor.Red -> 3
    trumpCard is NumberColorCard && trumpCard.color == CardColor.Green -> 4
    trumpCard is NumberColorCard && trumpCard.color == CardColor.Blue -> 5
    trumpCard == null -> 0
    else -> throw IllegalStateException()
}

fun mapTrumpCardNumber(trumpCard: WizardCard?) = when (trumpCard) {
    is NumberColorCard -> trumpCard.number
    else -> 13
}

fun mapTrumpColor(trumpColor: TrumpColor?) = when {
    trumpColor == TrumpColor.None -> 0
    trumpColor == TrumpColor.Yellow -> 1
    trumpColor == TrumpColor.Red -> 2
    trumpColor == TrumpColor.Green -> 3
    trumpColor == TrumpColor.Blue -> 4
    else -> 5
}

fun mapNumberOfPlayers(numberOfPlayers: Int) = when (numberOfPlayers) {
    3 -> 0
    4 -> 1
    5 -> 2
    6 -> 3
    else -> throw IllegalStateException()
}

fun mapCardColor(card: WizardCard?) = when {
    card is Null -> 0
    card is Joker -> 1
    card is NumberColorCard && card.color == CardColor.Yellow -> 2
    card is NumberColorCard && card.color == CardColor.Red -> 3
    card is NumberColorCard && card.color == CardColor.Green -> 4
    card is NumberColorCard && card.color == CardColor.Blue -> 5
    else -> 6
}

fun mapCardNumber(card: WizardCard?) = when (card) {
    is NumberColorCard -> card.number
    else -> 13
}

fun mapBet(bet: Int?) = when (bet) {
    0 -> 0
    1 -> 1
    else -> 2
}

data class WPlayer(
    val id: String
): WizardPlayer {
    override fun userPublicID() = id
}

class GymUtils {
    private lateinit var wizardGameState: WizardGameState

    private val t1 = WPlayer("T1")
    private val t2 = WPlayer("T2")
    private val t3 = WPlayer("T3")
    private val t4 = WPlayer("T4")
    private val t5 = WPlayer("T5")
    private val t6 = WPlayer("T6")

    init {
        reset()
    }

    fun reset() {
        // Zufällige Anzahl an Spielern
        val maxPlayers = listOf(t1, t2, t3, t4, t5, t6)
        val numOfPlayers = nextInt(3, 7);
        val players = maxPlayers
            .subList(0, numOfPlayers)
            .shuffled()

        wizardGameState = WizardGameState
            .initial(WizardGameSettings(hiddenBets = false), players)

        progressUntilNext()
    }

    fun print(): String {
        val s = StringWriter()

        val currentRound = wizardGameState
            .currentRound!!

        val currentPlayer = currentRound
            .currentPlayer

        val immutableRoundState = currentRound
            .immutableRoundState(wizardGameState.gameSettings, wizardGameState.players)

        val viewForPlayer = currentRound
            .viewForPlayer(t3)

        if (viewForPlayer !is RoundStateForPlayerFirstRound) {
            return ""
        }

        s.append("Trumpfkarte: ${immutableRoundState.trumpCard!!.id()}\n")
        s.append("Trumpffarbe: ${viewForPlayer.trumpColor?.name ?: "?"}\n")
        s.append("\n")

        for (player in immutableRoundState.players) {
            val card = viewForPlayer.cardsOfOtherPlayers[player]?.firstOrNull() ?: "?"
            val bet = viewForPlayer.bets[player] ?: "?"
            s.append("${player.userPublicID()} : $card ($bet) ${if (player == currentPlayer) "<-" else ""}\n")
        }

        s.append("\n")

        return s.toString()
    }

    private fun progressUntilNext() {
        while (true) {
            val currentRound = wizardGameState
                .currentRound!!

            val currentPlayer = currentRound
                .currentPlayer

            val currentPhase = currentRound
                .phase()

            // Bot-Spieler ist an der Reihe
            if (currentPlayer == t3
                && (currentPhase == OpenWizardRound.Phase.SelectTrumpPhase || currentPhase == OpenWizardRound.Phase.BettingPhase)
            ) {
                break
            }

            // Spiel ist zu Ende
            if (wizardGameState.previousRounds.size == 1) {
                break
            }

            // Spiele immer die einzige Karte
            if (currentPlayer == t3
                && (currentPhase == OpenWizardRound.Phase.PlayingPhase)
            ) {
                wizardGameState = wizardGameState
                    .placeCardInFirstRound(currentPlayer)
                continue
            }

            // Starte immer die neue Runde
            if (currentPlayer == t3
                && (currentPhase == OpenWizardRound.Phase.RoundEnded)
            ) {
                wizardGameState = wizardGameState
                    .finishRound()
                    .startNewRound()

                continue
            }

            val action = TODO()//randomBot(currentRound.immutableRoundState(), currentRound.viewForPlayer(currentPlayer))

            wizardGameState = when (action) {
                is BotActionSelectTrump -> TODO()//wizardGameState.selectTrump(currentPlayer, action.trumpColor)
                is BotActionPlaceBet -> TODO() //wizardGameState.placeBet(currentPlayer, action.bet)
                is BotActionPlaceCardInFirstRound -> wizardGameState.placeCardInFirstRound(currentPlayer)
                is BotActionStartNewRound -> wizardGameState.finishRound().startNewRound()
                is BotActionPlaceCardInNormalRound -> TODO()
            }
        }
    }

    fun getObservation(): R1Observation {
        val currentRound = wizardGameState
            .currentRound!!

        val currentPlayer = currentRound
            .currentPlayer

        val currentPhase = currentRound
            .phase()

        val viewForPlayer = currentRound
            .viewForPlayer(currentPlayer)
        val immutableRoundState = currentRound
            .immutableRoundState(wizardGameState.gameSettings, wizardGameState.players)

        if (viewForPlayer !is RoundStateForPlayerFirstRound) {
            return R1Observation(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        }

        return r1ObservationOf(viewForPlayer, immutableRoundState)
    }

    fun done(): Boolean {
        return wizardGameState.previousRounds.size == 1
    }

    fun step(action: Int): Int {
        val currentRound = wizardGameState
            .currentRound!!

        val currentPlayer = currentRound
            .currentPlayer

        val currentPhase = currentRound
            .phase()

        require(currentPlayer == t3)
        require(currentPhase == OpenWizardRound.Phase.BettingPhase || currentPhase == OpenWizardRound.Phase.SelectTrumpPhase)

        if (currentPhase == OpenWizardRound.Phase.BettingPhase) {
            if (action > 1) {
                return -100
            }

            if (action !in currentRound.bets.allowedBets(WizardGameSettings(hiddenBets = false), t3, currentRound.numberOfCards)) {
                return -100
            }

            wizardGameState = wizardGameState
                .placeBet(t3, action)
        } else if (currentPhase == OpenWizardRound.Phase.SelectTrumpPhase) {
            if (action <= 1) {
                return -100
            }

            val trumpColor = when (action) {
                2 -> TrumpColor.Yellow
                3 -> TrumpColor.Red
                4 -> TrumpColor.Green
                5 -> TrumpColor.Blue
                else -> throw IllegalStateException()
            }

            wizardGameState = wizardGameState
                .selectTrump(t3, trumpColor)

        }

        progressUntilNext()

        if (wizardGameState.previousRounds.size == 1) {
            val r = wizardGameState
                .previousRounds[0]
                .sumPointsOfRoundForPlayer(wizardGameState.players, t3, wizardGameState.previousRounds)

            if (r == 30)
                return 3
            if (r == 20)
                return 2
            if (r == -10)
                return -1

            return 0
        } else {
            return 0
        }
    }
}

fun main() {
    val server = GatewayServer("")
    server.start()
}