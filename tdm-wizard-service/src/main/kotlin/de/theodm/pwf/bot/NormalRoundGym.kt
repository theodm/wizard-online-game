package de.theodm.pwf.bot

import de.theodm.pwf.bot.Players.t1
import de.theodm.pwf.bot.Players.t2
import de.theodm.pwf.bot.Players.t3
import de.theodm.pwf.bot.Players.t4
import de.theodm.pwf.bot.Players.t5
import de.theodm.pwf.bot.Players.t6
import de.theodm.wizard.*
import de.theodm.wizard.card.*
import de.theodm.wizard.card.Joker.Companion.J1
import de.theodm.wizard.card.Joker.Companion.J2
import de.theodm.wizard.card.Joker.Companion.J3
import de.theodm.wizard.card.Joker.Companion.J4
import de.theodm.wizard.card.Null.Companion.N1
import de.theodm.wizard.card.Null.Companion.N2
import de.theodm.wizard.card.Null.Companion.N3
import de.theodm.wizard.card.Null.Companion.N4
import de.theodm.wizard.game.WizardGameSettings
import de.theodm.wizard.game.WizardGameState
import de.theodm.wizard.game.card.TrumpColor
import de.theodm.wizard.game.round.FinishedWizardRound
import de.theodm.wizard.game.round.ImmutableRoundState
import de.theodm.wizard.game.round.OpenWizardRound
import de.theodm.wizard.game.round.RoundStateForPlayerNormalRound
import de.theodm.wizard.game.stich.toPlayerSticheMap
import de.vandermeer.asciitable.AsciiTable
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment
import java.io.StringWriter
import kotlin.random.Random

object Players {
    val t1 = WPlayer("T1")
    val t2 = WPlayer("T2")
    val t3 = WPlayer("T3")
    val t4 = WPlayer("T4")
    val t5 = WPlayer("T5")
    val t6 = WPlayer("T6")

    fun random(): List<WPlayer> {
        // Zufällige Anzahl an Spielern
        val maxPlayers = listOf(t1, t2, t3, t4, t5, t6)
        val numOfPlayers = Random.nextInt(3, 7);

        return maxPlayers
            .subList(0, numOfPlayers)
            .shuffled()
    }
}

fun progressState(
    wizardGameState: WizardGameState,
    botMappings: Map<WPlayer, WizardBot>
): WizardGameState {
    var wizardGameState = wizardGameState

    while (true) {
        val currentRound = wizardGameState
            .currentRound

        if (currentRound == null) {
            // Spiel zu Ende
            return wizardGameState
        }

        val currentPlayer = currentRound
            .currentPlayer

        val currentPhase = currentRound
            .phase()

        val numberOfCards = currentRound
            .numberOfCards

        if (currentPhase == OpenWizardRound.Phase.RoundEnded) {
            // Runde ist beendet, starte eine neue!
            wizardGameState = wizardGameState
                .finishRound()
                .startNewRound()

            continue
        }

        if (currentPhase == OpenWizardRound.Phase.PlayingPhase && numberOfCards == 1) {
            // In der ersten Runde kann der Spieler nur
            // eine Karte wählen.
            wizardGameState = wizardGameState
                .placeCardInFirstRound(currentPlayer)
            continue
        }

        if (currentPlayer !in botMappings.keys) {
            // Ein Spieler, für den kein Bot definiert ist, ist an der Reihe
            // dann sind wir nun fertig.
            return wizardGameState
        }

        val bot = botMappings[currentPlayer] ?: TODO()

        wizardGameState = wizardGameState
            .applyBotAction(
                currentRound.viewForPlayer(currentPlayer),
                currentRound.immutableRoundState(wizardGameState.gameSettings, wizardGameState.players),
                bot
            )

    }
}

val actionToCardMap = mapOf(
    25 to N1,
    26 to N2,
    27 to N3,
    28 to N4,
    29 to NumberColorCard(CardColor.Yellow, 1),
    30 to NumberColorCard(CardColor.Yellow, 2),
    31 to NumberColorCard(CardColor.Yellow, 3),
    32 to NumberColorCard(CardColor.Yellow, 4),
    33 to NumberColorCard(CardColor.Yellow, 5),
    34 to NumberColorCard(CardColor.Yellow, 6),
    35 to NumberColorCard(CardColor.Yellow, 7),
    36 to NumberColorCard(CardColor.Yellow, 8),
    37 to NumberColorCard(CardColor.Yellow, 9),
    38 to NumberColorCard(CardColor.Yellow, 10),
    39 to NumberColorCard(CardColor.Yellow, 11),
    40 to NumberColorCard(CardColor.Yellow, 12),
    41 to NumberColorCard(CardColor.Yellow, 13),
    42 to NumberColorCard(CardColor.Red, 1),
    43 to NumberColorCard(CardColor.Red, 2),
    44 to NumberColorCard(CardColor.Red, 3),
    45 to NumberColorCard(CardColor.Red, 4),
    46 to NumberColorCard(CardColor.Red, 5),
    47 to NumberColorCard(CardColor.Red, 6),
    48 to NumberColorCard(CardColor.Red, 7),
    49 to NumberColorCard(CardColor.Red, 8),
    50 to NumberColorCard(CardColor.Red, 9),
    51 to NumberColorCard(CardColor.Red, 10),
    52 to NumberColorCard(CardColor.Red, 11),
    53 to NumberColorCard(CardColor.Red, 12),
    54 to NumberColorCard(CardColor.Red, 13),
    55 to NumberColorCard(CardColor.Green, 1),
    56 to NumberColorCard(CardColor.Green, 2),
    57 to NumberColorCard(CardColor.Green, 3),
    58 to NumberColorCard(CardColor.Green, 4),
    59 to NumberColorCard(CardColor.Green, 5),
    60 to NumberColorCard(CardColor.Green, 6),
    61 to NumberColorCard(CardColor.Green, 7),
    62 to NumberColorCard(CardColor.Green, 8),
    63 to NumberColorCard(CardColor.Green, 9),
    64 to NumberColorCard(CardColor.Green, 10),
    65 to NumberColorCard(CardColor.Green, 11),
    66 to NumberColorCard(CardColor.Green, 12),
    67 to NumberColorCard(CardColor.Green, 13),
    68 to NumberColorCard(CardColor.Blue, 1),
    69 to NumberColorCard(CardColor.Blue, 2),
    70 to NumberColorCard(CardColor.Blue, 3),
    71 to NumberColorCard(CardColor.Blue, 4),
    72 to NumberColorCard(CardColor.Blue, 5),
    73 to NumberColorCard(CardColor.Blue, 6),
    74 to NumberColorCard(CardColor.Blue, 7),
    75 to NumberColorCard(CardColor.Blue, 8),
    76 to NumberColorCard(CardColor.Blue, 9),
    77 to NumberColorCard(CardColor.Blue, 10),
    78 to NumberColorCard(CardColor.Blue, 11),
    79 to NumberColorCard(CardColor.Blue, 12),
    80 to NumberColorCard(CardColor.Blue, 13),
    81 to J1,
    82 to J2,
    83 to J3,
    84 to J4,
)

val cardToActionMap = actionToCardMap
    .entries
    .associateBy({ it.value }) { it.key }
class RNAction(
    val action: Int
) {
    init {
        require(action >= 0)
        require(action <= 84)
    }
    // 0 - 20 : Abgegebener Tipp
    // 21 - 24 : Ausgewählte Trumpffarbe
    // 25 - 84 : Karte zum Ablegen

    fun getBet(): Int? {
        if (action > 20) {
            return null
        }

        return action
    }

    fun getTrumpColor(): TrumpColor? {
        if (action < 21 || action > 24) {
            return null
        }

        return when (action) {
            21 -> TrumpColor.Yellow
            22 -> TrumpColor.Red
            23 -> TrumpColor.Green
            24 -> TrumpColor.Blue
            else -> throw IllegalStateException("invalid action $action")
        }
    }

    fun getCard(): WizardCard? {
        if (action < 25 || action > 84) {
            return null
        }

        return actionToCardMap[action]
    }

    override fun toString(): String {
        val card = getCard()
        val trumpColor = getTrumpColor()
        val bet = getBet()

        return when {
            card != null -> card.id()
            trumpColor != null -> trumpColor.toString()
            bet != null -> bet.toString()
            else -> TODO()
        }
    }


}

data class RNObservation(
    val p1Score: Float,
    val p2Score: Float,
    val p3Score: Float,
    val p4Score: Float,
    val p5Score: Float,
    val p6Score: Float,
    val trumpCardColor: Int,
    val trumpCardNumber: Int,
    val trumpColor: Int,
    val numberOfPlayers: Int,
    val ownPosition: Int,
    val ownPositionInStich: Int,
    val numberOfCards: Int,
    val remainingNumberOfCards: Int,
    val phase: Int,
    val Null: Int,
    val Joker: Int,
    val Y1: Int,
    val Y2: Int,
    val Y3: Int,
    val Y4: Int,
    val Y5: Int,
    val Y6: Int,
    val Y7: Int,
    val Y8: Int,
    val Y9: Int,
    val Y10: Int,
    val Y11: Int,
    val Y12: Int,
    val Y13: Int,
    val R1: Int,
    val R2: Int,
    val R3: Int,
    val R4: Int,
    val R5: Int,
    val R6: Int,
    val R7: Int,
    val R8: Int,
    val R9: Int,
    val R10: Int,
    val R11: Int,
    val R12: Int,
    val R13: Int,
    val G1: Int,
    val G2: Int,
    val G3: Int,
    val G4: Int,
    val G5: Int,
    val G6: Int,
    val G7: Int,
    val G8: Int,
    val G9: Int,
    val G10: Int,
    val G11: Int,
    val G12: Int,
    val G13: Int,
    val B1: Int,
    val B2: Int,
    val B3: Int,
    val B4: Int,
    val B5: Int,
    val B6: Int,
    val B7: Int,
    val B8: Int,
    val B9: Int,
    val B10: Int,
    val B11: Int,
    val B12: Int,
    val B13: Int,
    val p1CardColor: Int,
    val p1CardNumber: Int,
    val p2CardColor: Int,
    val p2CardNumber: Int,
    val p3CardColor: Int,
    val p3CardNumber: Int,
    val p4CardColor: Int,
    val p4CardNumber: Int,
    val p5CardColor: Int,
    val p5CardNumber: Int,
    val p6CardColor: Int,
    val p6CardNumber: Int,
    val firstPlayerBet: Int,
    val secondPlayerBet: Int,
    val thirdPlayerBet: Int,
    val fourthPlayerBet: Int,
    val fifthPlayerBet: Int,
    val sixthPlayerBet: Int,
    val firstPlayerStiche: Int,
    val secondPlayerStiche: Int,
    val thirdPlayerStiche: Int,
    val fourthPlayerStiche: Int,
    val fifthPlayerStiche: Int,
    val sixthPlayerStiche: Int,
    val ownNumberOfStiche: Int,
    val numberOfRemainingCards: Int
)

fun mapScoreToFloat(
    score: Int
): Float {
    return normalize(score.toFloat(), 0.0f, 1000.0f);
}

fun normalize(value: Float, min: Float, max: Float): Float {
    return 1 - (value - min) / (max - min)
}

fun mapPhase(phase: OpenWizardRound.Phase) = when (phase) {
    OpenWizardRound.Phase.SelectTrumpPhase -> 0
    OpenWizardRound.Phase.BettingPhase -> 1
    OpenWizardRound.Phase.PlayingPhase -> 2
    else -> throw IllegalStateException("illegal phase: ${phase}")
}

fun mapNumberOfStiche(int: Int?) = when (int) {
    null -> 21
    else -> int
}

fun mapOwnNumberOfStiche(int: Int) = int

fun mapNumberOfRemainingCards(int: Int) = int

fun rnObservationOf(
    oldRounds: List<FinishedWizardRound>,
    viewForPlayer: RoundStateForPlayerNormalRound,
    immutableRoundState: ImmutableRoundState
): RNObservation {
    val currentPlayer = viewForPlayer.currentPlayer

    val firstPlayer = immutableRoundState.players.getOrNull(0)
    val secondPlayer = immutableRoundState.players.getOrNull(1)
    val thirdPlayer = immutableRoundState.players.getOrNull(2)
    val fourthPlayer = immutableRoundState.players.getOrNull(3)
    val fifthPlayer = immutableRoundState.players.getOrNull(4)
    val sixthPlayer = immutableRoundState.players.getOrNull(5)

    val playerToCardMap = viewForPlayer.currentStich.playerToCardMap(immutableRoundState.players)

    val firstPlayerCardInStich = playerToCardMap[firstPlayer]
    val secondPlayerCardInStich = playerToCardMap[firstPlayer]
    val thirdPlayerCardInStich = playerToCardMap[firstPlayer]
    val fourthPlayerCardInStich = playerToCardMap[firstPlayer]
    val fifthPlayerCardInStich = playerToCardMap[firstPlayer]
    val sixthPlayerCardInStich = playerToCardMap[firstPlayer]

    val toPlayerSticheMap =
        viewForPlayer.sticheOfPlayer.toPlayerSticheMap(immutableRoundState.players, viewForPlayer.trumpColor)
    val firstPlayerNumberOfStiche= mapNumberOfStiche(toPlayerSticheMap[firstPlayer])
    val secondPlayerNumberOfStiche= mapNumberOfStiche(toPlayerSticheMap[secondPlayer])
    val thirdPlayerNumberOfStiche= mapNumberOfStiche(toPlayerSticheMap[thirdPlayer])
    val fourthPlayerNumberOfStiche= mapNumberOfStiche(toPlayerSticheMap[fourthPlayer])
    val fifthPlayerNumberOfStiche= mapNumberOfStiche(toPlayerSticheMap[fifthPlayer])
    val sixthPlayerNumberOfStiche= mapNumberOfStiche(toPlayerSticheMap[sixthPlayer])

    val ownNumberOfStiche = mapOwnNumberOfStiche(toPlayerSticheMap[currentPlayer]!!)
    val numberOfRemainingCards = mapNumberOfRemainingCards(viewForPlayer.ownCards.size)

    val currentPlayerIndex = immutableRoundState
        .players
        .indexOf(currentPlayer)

    val points = oldRounds
        .last()
        .sumPointsOfRound(de.theodm.wizard.game.players.Players(immutableRoundState.players), oldRounds)

    val positionInStich = viewForPlayer
        .currentStich
        .positionOfPlayerInStich(immutableRoundState.players, currentPlayer)

    return RNObservation(
        mapScoreToFloat(points[firstPlayer]!!),
        mapScoreToFloat(points[secondPlayer]!!),
        mapScoreToFloat(points[thirdPlayer]!!),
        mapScoreToFloat(if (fourthPlayer != null) points[fourthPlayer]!! else 0),
        mapScoreToFloat(if (fifthPlayer != null) points[fifthPlayer]!! else 0),
        mapScoreToFloat(if (sixthPlayer != null) points[sixthPlayer]!! else 0),
        mapTrumpCardColor(immutableRoundState.trumpCard),
        mapTrumpCardNumber(immutableRoundState.trumpCard),
        mapTrumpColor(viewForPlayer.trumpColor),
        mapNumberOfPlayers(immutableRoundState.players.size),
        currentPlayerIndex,
        positionInStich,
        immutableRoundState.numberOfCards - 1,
        viewForPlayer.ownCards.size - 1,
        mapPhase(viewForPlayer.phase),
        viewForPlayer.ownCards.filter { it is Null }.size,
        viewForPlayer.ownCards.filter { it is Joker }.size,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Yellow, 1))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Yellow, 2))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Yellow, 3))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Yellow, 4))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Yellow, 5))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Yellow, 6))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Yellow, 7))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Yellow, 8))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Yellow, 9))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Yellow, 10))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Yellow, 11))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Yellow, 12))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Yellow, 13))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Red, 1))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Red, 2))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Red, 3))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Red, 4))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Red, 5))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Red, 6))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Red, 7))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Red, 8))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Red, 9))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Red, 10))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Red, 11))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Red, 12))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Red, 13))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Green, 1))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Green, 2))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Green, 3))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Green, 4))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Green, 5))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Green, 6))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Green, 7))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Green, 8))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Green, 9))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Green, 10))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Green, 11))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Green, 12))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Green, 13))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Blue, 1))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Blue, 2))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Blue, 3))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Blue, 4))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Blue, 5))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Blue, 6))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Blue, 7))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Blue, 8))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Blue, 9))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Blue, 10))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Blue, 11))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Blue, 12))) 1 else 0,
        if (viewForPlayer.ownCards.contains(NumberColorCard(CardColor.Blue, 13))) 1 else 0,
        mapCardColor(firstPlayerCardInStich),
        mapCardNumber(firstPlayerCardInStich),
        mapCardColor(secondPlayerCardInStich),
        mapCardNumber(secondPlayerCardInStich),
        mapCardColor(thirdPlayerCardInStich),
        mapCardNumber(thirdPlayerCardInStich),
        mapCardColor(fourthPlayerCardInStich),
        mapCardNumber(fourthPlayerCardInStich),
        mapCardColor(fifthPlayerCardInStich),
        mapCardNumber(fifthPlayerCardInStich),
        mapCardColor(sixthPlayerCardInStich),
        mapCardNumber(sixthPlayerCardInStich),
        mapBet(viewForPlayer.bets[firstPlayer]),
        mapBet(viewForPlayer.bets[secondPlayer]),
        mapBet(viewForPlayer.bets[thirdPlayer]),
        mapBet(viewForPlayer.bets[fourthPlayer]),
        mapBet(viewForPlayer.bets[fifthPlayer]),
        mapBet(viewForPlayer.bets[sixthPlayer]),
        firstPlayerNumberOfStiche,
        secondPlayerNumberOfStiche,
        thirdPlayerNumberOfStiche,
        fourthPlayerNumberOfStiche,
        fifthPlayerNumberOfStiche,
        sixthPlayerNumberOfStiche,
        ownNumberOfStiche,
        numberOfRemainingCards
    )
}

class NormalRoundGym(

) {
    private lateinit var wizardGameState: WizardGameState
    private var wrongMoveCounter: Int = 0

    init {
        reset()
    }

    fun reset() {
        wizardGameState = WizardGameState.initialRound2(
            gameSettings = WizardGameSettings(false),
            players = Players.random()
        )
        wrongMoveCounter = 0

        progress()
    }

    fun done(): Boolean {
        return wizardGameState.currentRound == null
    }

    fun getObservation(): RNObservation {
        if (wizardGameState.currentRound == null) {
            return RNObservation(
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0
            )
        }
        return rnObservationOf(
            wizardGameState.previousRounds,
            wizardGameState.currentRound!!.viewForPlayer(wizardGameState.currentRound!!.currentPlayer) as RoundStateForPlayerNormalRound,
            wizardGameState.currentRound!!.immutableRoundState(wizardGameState.gameSettings, wizardGameState.players)
        )
    }

    fun mask(): Array<Boolean> {
        val currentRound = wizardGameState
            .currentRound!!

        val currentPlayer = currentRound
            .currentPlayer

        val currentPhase = currentRound
            .phase()

        val mask = (0..84)
            .map { false }
            .toMutableList()

        when (currentPhase) {
            OpenWizardRound.Phase.BettingPhase -> {
                val allowedBets = currentRound
                    .bets
                    .allowedBets(wizardGameState.gameSettings, t3, currentRound.numberOfCards)

                for (bet in allowedBets) {
                    mask[bet] = true
                }

            }
            OpenWizardRound.Phase.SelectTrumpPhase -> {
                mask[21] = true
                mask[22] = true
                mask[23] = true
                mask[24] = true
            }
            OpenWizardRound.Phase.PlayingPhase -> {
                val allowedCards = currentRound
                    .currentStich
                    .allowedToPlayCards(wizardGameState.players, currentRound.handsOfPlayers[currentPlayer]!!)

                for (card in allowedCards) {
                    mask[cardToActionMap[card]!!] = true
                }
            }
            else -> {}
        }

        return mask.toTypedArray()
    }

    fun print(): String {
        val s = StringWriter()

        val currentRound = wizardGameState
            .currentRound

        if (currentRound == null) {
            return "Game ended"
        }

        val immutableRoundState = currentRound
            .immutableRoundState(wizardGameState.gameSettings, wizardGameState.players)

        val viewForPlayer = currentRound
            .viewForPlayer(t3)

        if (viewForPlayer !is RoundStateForPlayerNormalRound) {
            return ""
        }

        val a = AsciiTable();

        a.addRow("Trumpfkarte", immutableRoundState.trumpCard?.id() ?: "None")
        a.addRow("Trumpffarbe", viewForPlayer.trumpColor?.name ?: "?")
        a.addRow("Phase", "${viewForPlayer.phase}")
        a.setTextAlignment(TextAlignment.CENTER)

        s.append(a.render())

        s.append("\n\n")

        val b = AsciiTable()

        val playerToCardMap = viewForPlayer.currentStich.playerToCardMap(wizardGameState.players)

        b.addRow("Spieler", *immutableRoundState.players.map { it.userPublicID() }.toTypedArray())
        b.addRow("Tipp", *immutableRoundState.players.map { viewForPlayer.bets[it]?.toString() ?: "?" }.toTypedArray())
        b.addRow("Karte", *immutableRoundState.players.map { playerToCardMap[it]?.id() ?: " " }.toTypedArray())
        b.setTextAlignment(TextAlignment.CENTER)

        s.append(b.render())
        s.append("\n\nEigene Karten: ")

        for (card in viewForPlayer.ownCards) {
            s.append(card.id() + " ")
        }

        s.append("\n\n")

        return s.toString()
    }

    fun progress() {
        wizardGameState = progressState(
            wizardGameState = wizardGameState,
            botMappings = mapOf(
                t1 to RandomBot(),
                t2 to RandomBot(),
                t4 to RandomBot(),
                t5 to RandomBot(),
                t6 to RandomBot()
            )
        )
    }

    fun step(action: RNAction): Int {
        val prevState = wizardGameState

        val currentRound = wizardGameState
            .currentRound!!

        val currentPlayer = currentRound
            .currentPlayer

        val currentPhase = currentRound
            .phase()

        require(currentPlayer == t3)
        require(
            currentPhase == OpenWizardRound.Phase.BettingPhase
                    || currentPhase == OpenWizardRound.Phase.SelectTrumpPhase
                    || currentPhase == OpenWizardRound.Phase.PlayingPhase
        )

        when (currentPhase) {
            OpenWizardRound.Phase.BettingPhase -> {
                val bet = action.getBet()

                if (bet == null) {
                    log.error { "should not happen (bet == null) " + action }
                    return -100
                }

                val allowedBets = currentRound
                    .bets
                    .allowedBets(wizardGameState.gameSettings, t3, currentRound.numberOfCards)

                if (bet !in allowedBets) {
                    return -100
                }

                wizardGameState = wizardGameState
                    .placeBet(currentPlayer, bet)
            }
            OpenWizardRound.Phase.SelectTrumpPhase -> {
                val trumpColor = action.getTrumpColor()

                if (trumpColor == null) {
                    log.error { "should not happen (trumpColor == null) " + action }
                    return -100
                }

                wizardGameState = wizardGameState
                    .selectTrump(currentPlayer, trumpColor)
            }
            OpenWizardRound.Phase.PlayingPhase -> {
                val card = action.getCard()

                if (card == null) {
                    log.error { "should not happen (card == null) " + action }
                    return -100
                }

                val allowedCards = currentRound
                    .currentStich
                    .allowedToPlayCards(wizardGameState.players, currentRound.handsOfPlayers[currentPlayer])

                if (card !in allowedCards) {

                    log.error { "should not happen (card !in allowedCards) " + action }
                    return -100
                }

                wizardGameState = wizardGameState
                    .placeCard(currentPlayer, card)
            }
        }

        progress()

        if (prevState.previousRounds.size < wizardGameState.previousRounds.size) {
            // Eine Runde wurde beendet
            return wizardGameState
                .previousRounds
                .last()
                .pointsOfRoundForPlayer(wizardGameState.players, currentPlayer)
        }

        return 0
    }
}