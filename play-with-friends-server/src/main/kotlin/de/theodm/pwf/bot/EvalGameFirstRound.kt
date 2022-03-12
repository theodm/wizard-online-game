//package de.theodm.pwf.bot
//
//import de.theodm.pwf.routing.model.bot.RsGenome
//import de.theodm.wizard.*
//import de.theodm.wizard.card.*
//import de.theodm.wizard.game.bets.Bets
//import de.theodm.wizard.game.card.TrumpColor
//import de.theodm.wizard.game.deck.Deck
//import de.theodm.wizard.game.players.WizardPlayer
//import de.theodm.wizard.game.round.ImmutableRoundState
//import de.theodm.wizard.game.round.OpenWizardRound
//import de.theodm.wizard.game.round.RoundStateForPlayer
//import de.theodm.wizard.game.round.RoundStateForPlayerFirstRound
//import kotlin.random.Random
//import kotlin.random.asJavaRandom
//
//
//data class WPlayer(
//    val id: String
//): WizardPlayer {
//    override fun userPublicID() = id
//    override fun isBot() = true
//    override fun botType() = "DSNTMatter"
//}
//
//fun normalize(value: Double, min: Double, max: Double): Double {
//    return 1 - (value - min) / (max - min)
//}
//
//fun executeFFNetwork(
//    ffNetwork: FFNetwork,
//    vfp: RoundStateForPlayerFirstRound,
//    irst: ImmutableRoundState
//): BotAction {
//    when (vfp.phase) {
//        OpenWizardRound.Phase.PlayingPhase -> {
//            return BotActionPlaceCardInFirstRound
//        }
//        OpenWizardRound.Phase.RoundEnded -> {
//            return BotActionStartNewRound
//        }
//    }
//
//    fun boolToHotOne(b: Boolean): Double {
//        return if (b) 1.0 else 0.0
//    }
//
//    fun intToScaledOne(int: Int, min: Int, max: Int): Double {
//        return normalize(int.toDouble(), min.toDouble(), max.toDouble())
//    }
//
//    data class CardMapping(
//        val dblCardIsYellow: Double,
//        val dblCardIsGreen: Double,
//        val dblCardIsRed: Double,
//        val dblCardIsBlue: Double,
//        val dblCardIsJoker: Double,
//        val dblCardIsNull: Double,
//        val dblCardValue: Double
//    ) {
//        fun asValues(): Array<Double> {
//            return arrayOf(
//                dblCardIsYellow,
//                dblCardIsGreen,
//                dblCardIsRed,
//                dblCardIsBlue,
//                dblCardIsJoker,
//                dblCardIsNull,
//                dblCardValue
//            )
//        }
//    }
//
//    fun mapCard(
//        card: WizardCard?
//    ): CardMapping {
//        if (card == null) {
//            return CardMapping(
//                -1.0,
//                -1.0,
//                -1.0,
//                -1.0,
//                -1.0,
//                -1.0,
//                -1.0
//            )
//        }
//
//        return CardMapping(
//            boolToHotOne(card is NumberColorCard && card.color == CardColor.Yellow),
//            boolToHotOne(card is NumberColorCard && card.color == CardColor.Green),
//            boolToHotOne(card is NumberColorCard && card.color == CardColor.Red),
//            boolToHotOne(card is NumberColorCard && card.color == CardColor.Blue),
//            boolToHotOne(card is Joker),
//            boolToHotOne(card is Null),
//            if (card is NumberColorCard) intToScaledOne(card.number, 0, 13) else -1.0
//        )
//    }
//
//    val trumpCard = mapCard(irst.trumpCard!!)
//
//    val trumpColorIsYellow = if (vfp.trumpColor != null) boolToHotOne(vfp.trumpColor == TrumpColor.Yellow) else -1.0
//    val trumpColorIsGreen = if (vfp.trumpColor != null) boolToHotOne(vfp.trumpColor == TrumpColor.Green) else -1.0
//    val trumpColorIsRed = if (vfp.trumpColor != null) boolToHotOne(vfp.trumpColor == TrumpColor.Red) else -1.0
//    val trumpColorIsBlue = if (vfp.trumpColor != null) boolToHotOne(vfp.trumpColor == TrumpColor.Blue) else -1.0
//
//    val dblNumOfPlayers = intToScaledOne(irst.players.size, 3, 6)
//    val dblOwnPosition = intToScaledOne(irst.players.indexOf(vfp.currentPlayer), 0, 5)
//
//    val firstPlayer = irst.players.getOrNull(0)
//    val secondPlayer = irst.players.getOrNull(1)
//    val thirdPlayer = irst.players.getOrNull(2)
//    val fourthPlayer = irst.players.getOrNull(3)
//    val fifthPlayer = irst.players.getOrNull(4)
//    val sixthPlayer = irst.players.getOrNull(5)
//
//    val firstPlayerCard = mapCard(vfp.cardsOfOtherPlayers[firstPlayer]?.let { it[0] })
//    val secondPlayerCard = mapCard(vfp.cardsOfOtherPlayers[secondPlayer]?.let{ it[0] })
//    val thirdPlayerCard = mapCard(vfp.cardsOfOtherPlayers[thirdPlayer]?.let { it[0] })
//    val fourthPlayerCard = mapCard(vfp.cardsOfOtherPlayers[fourthPlayer]?.let { it[0] })
//    val fifthPlayerCard = mapCard(vfp.cardsOfOtherPlayers[fifthPlayer]?.let { it[0] })
//    val sixthPlayerCard = mapCard(vfp.cardsOfOtherPlayers[sixthPlayer]?.let { it[0] })
//
//    val firstPlayerBet = if (vfp.bets[firstPlayer] != null) intToScaledOne(vfp.bets[firstPlayer]!!, 0, 1) else -1.0
//    val secondPlayerBet = if (vfp.bets[secondPlayer] != null) intToScaledOne(vfp.bets[secondPlayer]!!, 0, 1) else -1.0
//    val thirdPlayerBet = if (vfp.bets[thirdPlayer] != null) intToScaledOne(vfp.bets[thirdPlayer]!!, 0, 1) else -1.0
//    val fourthPlayerBet = if (vfp.bets[fourthPlayer] != null) intToScaledOne(vfp.bets[fourthPlayer]!!, 0, 1) else -1.0
//
//    val _inputs = listOf(
//        *trumpCard.asValues(),
//        trumpColorIsYellow,
//        trumpColorIsGreen,
//        trumpColorIsRed,
//        trumpColorIsBlue,
//        dblNumOfPlayers,
//        dblOwnPosition,
//        *firstPlayerCard.asValues(),
//        *secondPlayerCard.asValues(),
//        *thirdPlayerCard.asValues(),
//        *fourthPlayerCard.asValues(),
//        *fifthPlayerCard.asValues(),
//        *sixthPlayerCard.asValues(),
//        firstPlayerBet,
//        secondPlayerBet,
//        thirdPlayerBet,
//        fourthPlayerBet
//    )
//
//    val result = ffNetwork.activate(_inputs)
//
//    val resultChooseTrumpColor = result.subList(0, 4)
//        .mapIndexed { i, v -> i to v }
//        .maxByOrNull { (i, v) -> v }!!
//        .first
//
//    val trumpColor = when (resultChooseTrumpColor) {
//        0 -> TrumpColor.Yellow
//        1 -> TrumpColor.Green
//        2 -> TrumpColor.Red
//        3 -> TrumpColor.Blue
//        else -> throw IllegalStateException()
//    }
//
//    val resultPlaceBet = if (result[4] >= 0.5) 1 else 0
//
//    when (vfp.phase) {
//        OpenWizardRound.Phase.SelectTrumpPhase -> {
//            return BotActionSelectTrump(trumpColor)
//        }
//        OpenWizardRound.Phase.BettingPhase -> {
//            val allowedBets = Bets(vfp.bets)
//                .allowedBets(vfp.currentPlayer, irst.numberOfCards)
//
//            return if (resultPlaceBet in allowedBets) {
//                BotActionPlaceBet(resultPlaceBet)
//            } else {
//                BotActionPlaceBet(allowedBets[0])
//            }
//        }
//        else -> TODO()
//    }
//
//}
//
//fun evalGameFirstRound(genome: RsGenome): Double {
//    val ffNetwork = createFFNetwork(
//        genome.toGenome(),
//        genome.inputNodeKeys.toSet(),
//        genome.outputNodeKeys.toSet()
//    )
//
//    val t1 = WPlayer("T1")
//    val t2 = WPlayer("T2")
//    val t3 = WPlayer("T3")
//    val t4 = WPlayer("T4")
//    val t5 = WPlayer("T5")
//    val t6 = WPlayer("T6")
//
//    val playerToBot = mapOf(
//        t1 to { irst: ImmutableRoundState, rsfp: RoundStateForPlayer -> if (rsfp is RoundStateForPlayerFirstRound) executeFFNetwork(ffNetwork, rsfp, irst) else BotActionStartNewRound },
//        t2 to { irst: ImmutableRoundState, rsfp: RoundStateForPlayer -> if (rsfp is RoundStateForPlayerFirstRound) executeFFNetwork(ffNetwork, rsfp, irst) else BotActionStartNewRound },
//        t3 to { irst: ImmutableRoundState, rsfp: RoundStateForPlayer -> if (rsfp is RoundStateForPlayerFirstRound) executeFFNetwork(ffNetwork, rsfp, irst) else BotActionStartNewRound },
//        t4 to { irst: ImmutableRoundState, rsfp: RoundStateForPlayer -> if (rsfp is RoundStateForPlayerFirstRound) executeFFNetwork(ffNetwork, rsfp, irst) else BotActionStartNewRound },
//        t5 to { irst: ImmutableRoundState, rsfp: RoundStateForPlayer -> if (rsfp is RoundStateForPlayerFirstRound) executeFFNetwork(ffNetwork, rsfp, irst) else BotActionStartNewRound },
//        t6 to { irst: ImmutableRoundState, rsfp: RoundStateForPlayer -> if (rsfp is RoundStateForPlayerFirstRound) executeFFNetwork(ffNetwork, rsfp, irst) else BotActionStartNewRound }
//    )
//
//    var sumOfPoints = 0
//    var playerSelected1 = 0
//    var playerSelected0 = 0
//    var numberOfWins = 0
//    var numberOf1Wins = 0
//    var numberOf0Wins = 0
//
//    var random = Random(5000)
//
//    var numberOf0WinsPossible = 0
//    var numberOf1WinsPossible = 0
//    for (i in 0 until 100) {
//        val maxPlayers = listOf(t1, t2, t3, t4, t5, t6)
//        val numOfPlayers = Random.Default.nextInt(3, 7);
//        var players = maxPlayers
//            .subList(0, numOfPlayers)
//
//        do {
//            players = players.shuffled()
//        } while (players.indexOf(t3) == players.size - 1);
//
//        var w = WizardGameState.initialWithDeck(
//            hostPlayer = t1,
//            players = players,
//            deck = Deck.shuffled(random.asJavaRandom())
//        )
//
//        while (w.oldRounds.isEmpty()) {
//            val action = playerToBot[w.currentRound!!.currentPlayer]!!.invoke(w.currentRound!!.immutableRoundState(), w.currentRound!!.viewForPlayer(w.currentRound!!.currentPlayer))
//
//            w = when (action) {
//                is BotActionSelectTrump -> w.selectTrump(w.currentRound!!.currentPlayer, action.trumpColor)
//                is BotActionPlaceBet -> w.placeBet(w.currentRound!!.currentPlayer, action.bet)
//                is BotActionPlaceCardInFirstRound -> w.placeSingleCard(w.currentRound!!.currentPlayer)
//                is BotActionStartNewRound -> w.finishAndStartNewRound(w.currentRound!!.currentPlayer)
//                is BotActionPlaceCardInNormalRound -> TODO()
//            }
//        }
//
//        val madeStich = w
//            .oldRounds[0]
//            .sticheOfPlayer[t3] == 1
//
//        val madeBet = w
//            .oldRounds[0]
//            .bets[t3]
//
//        numberOf0WinsPossible += if (!madeStich) 1 else 0
//        numberOf1WinsPossible += if (madeStich) 1 else 0
//
//        val r = w.oldRounds[0].sumPointsOfRoundForPlayer(t3, w.oldRounds)
//        numberOf1Wins += if (r == 30) 1 else 0
//        numberOf0Wins += if (r == 20) 1 else 0
//
//        playerSelected1 += if (w.oldRounds[0].bets[t3] == 1) 1 else 0
//        playerSelected0 += if (w.oldRounds[0].bets[t3] == 0) 1 else 0
//        numberOfWins += if (r > 0) 1 else 0
//        sumOfPoints += w.oldRounds[0].sumPointsOfRoundForPlayer(t3, w.oldRounds)
//    }
//
//    println("numberOfWins: $numberOfWins numberOf1Wins: $numberOf1Wins / $numberOf1WinsPossible numberOf0Wins: $numberOf0Wins / $numberOf0WinsPossible")
//
//    return (((numberOf0Wins.toDouble() + numberOf1Wins.toDouble() * 3) / (numberOf1WinsPossible.toDouble() * 3 + numberOf0WinsPossible.toDouble())))
//}