package de.theodm.wizard.game

import com.google.common.truth.Truth
import de.theodm.wizard.card.CardColor
import de.theodm.wizard.card.Joker
import de.theodm.wizard.card.Null
import de.theodm.wizard.card.NumberColorCard
import de.theodm.wizard.game.card.TrumpColor
import de.theodm.wizard.game.deck.Deck
import de.theodm.wizard.game.players.Players
import de.theodm.wizard.game.players.WizardPlayer
import de.theodm.wizard.game.round.*
import de.theodm.wizard.game.stich.Stich
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class OpenWizardRoundTest {
    private val wpSebastian = TestWizardPlayer("Sebastian")
    private val wpTabea = TestWizardPlayer("Tabea")
    private val wpDomi = TestWizardPlayer("Domi")

    private val players = Players(listOf(wpSebastian, wpTabea, wpDomi))

    private val ccBlau12 = NumberColorCard(CardColor.Blue, 12)
    private val ccBlau11 = NumberColorCard(CardColor.Blue, 11)

    private val ccGelb12 = NumberColorCard(CardColor.Yellow, 12)
    private val ccGelb11 = NumberColorCard(CardColor.Yellow, 11)

    private val ccGruen3 = NumberColorCard(CardColor.Green, 3)
    private val ccGruen5 = NumberColorCard(CardColor.Green, 5)

    private val deck = Deck.from(
            ccGruen5, // Trumpfkarte
            ccBlau12,  // Domi
            ccBlau11, // Tabea
            ccGruen3, // Sebastian
    )

    private val gameSettings = WizardGameSettings(hiddenBets = false)

    @Test
    @DisplayName("Runde 1 wird gespielt, dabei ist der Trumpf vorgegeben. (Also kein Zauberer)")
    fun testFirstRound() {
        var round = OpenWizardRound.startNewRoundWithDeck(
                numberOfCards = 1,
                players = players,
                startingPlayer = wpSebastian,
                deck = deck
        )

        val expectedImmutableRoundState = ImmutableRoundState(
                gameSettings = gameSettings,
                players = players,
                numberOfCards = 1,
                trumpCard = ccGruen5
        )

        var expectedViewForPlayerSebastian = RoundStateForPlayerFirstRound(
                phase = OpenWizardRound.Phase.BettingPhase,
                bets = mapOf<WizardPlayer, Int?>(
                        wpSebastian to null,
                        wpTabea to null,
                        wpDomi to null
                ),
                currentPlayer = wpSebastian,
                trumpColor = TrumpColor.Green,
                sticheOfPlayer = listOf(),
                cardsOfOtherPlayers = mapOf(
                        wpTabea to listOf(ccBlau11),
                        wpDomi to listOf(ccBlau12)
                ),
                currentStich = Stich.emptyStich(
                    startPlayer = wpSebastian
                )
        )
        var expectedViewForPlayerTabea = expectedViewForPlayerSebastian
                .copy(
                        cardsOfOtherPlayers = mapOf(
                                wpSebastian to listOf(ccGruen3),
                                wpDomi to listOf(ccBlau12)
                        )
                )
        var expectedViewForPlayerDomi = expectedViewForPlayerSebastian
                .copy(
                        cardsOfOtherPlayers = mapOf(
                                wpSebastian to listOf(ccGruen3),
                                wpTabea to listOf(ccBlau11)
                        )
                )

        run {
            Truth.assertThat(round.immutableRoundState(gameSettings, players)).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)

            Assertions.assertThrows(IllegalArgumentException::class.java) { round.placeCard(players, wpSebastian, ccGruen3) }
            Assertions.assertThrows(IllegalArgumentException::class.java) { round.selectTrump(players, wpSebastian, TrumpColor.Yellow) }
            Assertions.assertThrows(IllegalArgumentException::class.java) { round.placeBet(gameSettings, players, wpTabea, 1) }
            Assertions.assertThrows(IllegalArgumentException::class.java) { round.placeBet(gameSettings, players, wpDomi, 1) }
        }

        round = round.placeBet(gameSettings, players, wpSebastian, 0)

        run {
            val expectedBets = mapOf<WizardPlayer, Int?>(
                    wpSebastian to 0,
                    wpTabea to null,
                    wpDomi to null
            )

            expectedViewForPlayerSebastian = expectedViewForPlayerSebastian.copy(
                    bets = expectedBets,
                    currentPlayer = wpTabea
            )
            expectedViewForPlayerTabea = expectedViewForPlayerTabea.copy(
                    bets = expectedBets,
                    currentPlayer = wpTabea
            )
            expectedViewForPlayerDomi = expectedViewForPlayerDomi.copy(
                    bets = expectedBets,
                    currentPlayer = wpTabea
            )

            Truth.assertThat(round.immutableRoundState(gameSettings, players)).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)

        }

        round = round.placeBet(gameSettings, players, wpTabea, 1)

        run {
            val expectedBets = mapOf<WizardPlayer, Int?>(
                    wpSebastian to 0,
                    wpTabea to 1,
                    wpDomi to null
            )

            expectedViewForPlayerSebastian = expectedViewForPlayerSebastian.copy(
                    bets = expectedBets,
                    currentPlayer = wpDomi
            )
            expectedViewForPlayerTabea = expectedViewForPlayerTabea.copy(
                    bets = expectedBets,
                    currentPlayer = wpDomi
            )
            expectedViewForPlayerDomi = expectedViewForPlayerDomi.copy(
                    bets = expectedBets,
                    currentPlayer = wpDomi
            )

            Truth.assertThat(round.immutableRoundState(gameSettings, players)).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }

        round = round.placeBet(gameSettings, players, wpDomi, 1)

        run {
            val expectedBets = mapOf<WizardPlayer, Int?>(
                    wpSebastian to 0,
                    wpTabea to 1,
                    wpDomi to 1
            )

            expectedViewForPlayerSebastian = expectedViewForPlayerSebastian.copy(
                    phase = OpenWizardRound.Phase.PlayingPhase,
                    bets = expectedBets,
                    currentPlayer = wpSebastian
            )
            expectedViewForPlayerTabea = expectedViewForPlayerTabea.copy(
                    phase = OpenWizardRound.Phase.PlayingPhase,
                    bets = expectedBets,
                    currentPlayer = wpSebastian
            )
            expectedViewForPlayerDomi = expectedViewForPlayerDomi.copy(
                    phase = OpenWizardRound.Phase.PlayingPhase,
                    bets = expectedBets,
                    currentPlayer = wpSebastian
            )

            Truth.assertThat(round.immutableRoundState(gameSettings, players)).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }

        round = round.placeCard(players, wpSebastian, ccGruen3)

        run {
            val expectedStich = Stich(wpSebastian, listOf(ccGruen3))

            expectedViewForPlayerSebastian = expectedViewForPlayerSebastian.copy(
                    cardsOfOtherPlayers = mapOf(
                            wpTabea to listOf(ccBlau11),
                            wpDomi to listOf(ccBlau12)
                    ),
                    currentStich = expectedStich,
                    currentPlayer = wpTabea
            )
            expectedViewForPlayerTabea = expectedViewForPlayerTabea.copy(
                    cardsOfOtherPlayers = mapOf(
                            wpSebastian to listOf(),
                            wpDomi to listOf(ccBlau12)
                    ),
                    currentStich = expectedStich,
                    currentPlayer = wpTabea
            )
            expectedViewForPlayerDomi = expectedViewForPlayerDomi.copy(
                    cardsOfOtherPlayers = mapOf(
                            wpSebastian to listOf(),
                            wpTabea to listOf(ccBlau11)
                    ),
                    currentStich = expectedStich,
                    currentPlayer = wpTabea
            )

            Truth.assertThat(round.immutableRoundState(gameSettings, players)).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }

        round = round.placeCard(players, wpTabea, ccBlau11)

        run {
            val expectedStich = Stich(wpSebastian, listOf(ccGruen3, ccBlau11))

            expectedViewForPlayerSebastian = expectedViewForPlayerSebastian.copy(
                    cardsOfOtherPlayers = mapOf(
                            wpTabea to listOf(),
                            wpDomi to listOf(ccBlau12)
                    ),
                    currentStich = expectedStich,
                    currentPlayer = wpDomi
            )
            expectedViewForPlayerTabea = expectedViewForPlayerTabea.copy(
                    cardsOfOtherPlayers = mapOf(
                            wpSebastian to listOf(),
                            wpDomi to listOf(ccBlau12)
                    ),
                    currentStich = expectedStich,
                    currentPlayer = wpDomi
            )
            expectedViewForPlayerDomi = expectedViewForPlayerDomi.copy(
                    cardsOfOtherPlayers = mapOf(
                            wpSebastian to listOf(),
                            wpTabea to listOf()
                    ),
                    currentStich = expectedStich,
                    currentPlayer = wpDomi
            )

            Truth.assertThat(round.immutableRoundState(gameSettings, players)).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }

        round = round.placeCard(players,wpDomi, ccBlau12)

        run {
            val expectedStich = Stich(wpSebastian, listOf(ccGruen3, ccBlau11, ccBlau12))

            expectedViewForPlayerSebastian = expectedViewForPlayerSebastian.copy(
                    cardsOfOtherPlayers = mapOf(
                            wpTabea to listOf(),
                            wpDomi to listOf()
                    ),
                    phase = OpenWizardRound.Phase.RoundEnded,
                    currentStich = expectedStich,
                    currentPlayer = wpSebastian,
                    sticheOfPlayer = listOf(expectedStich)
            )
            expectedViewForPlayerTabea = expectedViewForPlayerTabea.copy(
                    cardsOfOtherPlayers = mapOf(
                            wpSebastian to listOf(),
                            wpDomi to listOf()
                    ),
                    phase = OpenWizardRound.Phase.RoundEnded,
                    currentStich = expectedStich,
                    currentPlayer = wpSebastian,
                    sticheOfPlayer = listOf(expectedStich)
            )
            expectedViewForPlayerDomi = expectedViewForPlayerDomi.copy(
                    cardsOfOtherPlayers = mapOf(
                            wpTabea to listOf(),
                            wpSebastian to listOf()
                    ),
                    phase = OpenWizardRound.Phase.RoundEnded,
                    currentStich = expectedStich,
                    currentPlayer = wpSebastian,
                    sticheOfPlayer = listOf(expectedStich)
            )

            Truth.assertThat(round.immutableRoundState(gameSettings, players)).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }
    }

    private val deck2 = Deck.from(
            Joker.J1, // Trumpfkarte
            ccGruen3,  // Domi
            ccGelb11,
            ccBlau11, // Tabea
            ccGelb12,
            ccBlau12, // Sebastian
            Null.N1,
    )

    @Test
    @DisplayName("Runde 2 dabei wurde ein Zauberer als Trumpfkarte gezogen.")
    fun testNormalRoundAndChooseTrump() {
        val players = Players(listOf(wpSebastian, wpTabea, wpDomi))

        var round = OpenWizardRound.startNewRoundWithDeck(
                numberOfCards = 2,
                players = players,
                startingPlayer = wpDomi,
                deck = deck2
        )

        val expectedImmutableRoundState = ImmutableRoundState(
            gameSettings = gameSettings,
                players = players,
                numberOfCards = 2,
                trumpCard = Joker.J1
        )

        var expectedViewForPlayerSebastian = RoundStateForPlayerNormalRound(
                phase = OpenWizardRound.Phase.SelectTrumpPhase,
                bets = mapOf<WizardPlayer, Int?>(
                        wpSebastian to null,
                        wpTabea to null,
                        wpDomi to null
                ),
                currentPlayer = wpTabea,
                trumpColor = null,
                sticheOfPlayer = listOf(),
                ownCards = listOf(
                        ccBlau12,
                        Null.N1
                ),
                currentStich = Stich(wpDomi, listOf()),
                numberOfCardsInHandsOfPlayers = mapOf(
                        wpSebastian to 2,
                        wpTabea to 2,
                        wpDomi to 2
                )
        )
        var expectedViewForPlayerTabea = expectedViewForPlayerSebastian
                .copy(
                        ownCards = listOf(
                                ccBlau11,
                                ccGelb12
                        ),
                )
        var expectedViewForPlayerDomi = expectedViewForPlayerSebastian
                .copy(
                        ownCards = listOf(
                                ccGruen3,
                                ccGelb11
                        ),
                )

        run {
            Truth.assertThat(round.immutableRoundState(gameSettings, players)).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }

        round = round.selectTrump(players, wpTabea, TrumpColor.Blue)

        run {
            val expectedBets = mapOf<WizardPlayer, Int?>(
                    wpSebastian to null,
                    wpTabea to null,
                    wpDomi to null
            )

            expectedViewForPlayerSebastian = expectedViewForPlayerSebastian.copy(
                    phase = OpenWizardRound.Phase.BettingPhase,
                    trumpColor = TrumpColor.Blue,
                    bets = expectedBets,
                    currentPlayer = wpDomi
            )
            expectedViewForPlayerTabea = expectedViewForPlayerTabea.copy(
                    phase = OpenWizardRound.Phase.BettingPhase,
                    trumpColor = TrumpColor.Blue,
                    bets = expectedBets,
                    currentPlayer = wpDomi
            )
            expectedViewForPlayerDomi = expectedViewForPlayerDomi.copy(
                    phase = OpenWizardRound.Phase.BettingPhase,
                    trumpColor = TrumpColor.Blue,
                    bets = expectedBets,
                    currentPlayer = wpDomi
            )

            Truth.assertThat(round.immutableRoundState(gameSettings, players)).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }

        round = round.placeBet(gameSettings, players, wpDomi, 1)
        round = round.placeBet(gameSettings, players,wpSebastian, 1)
        round = round.placeBet(gameSettings, players,wpTabea, 1)

        round = round.placeCard(players,wpDomi, ccGruen3)
        round = round.placeCard(players,wpSebastian, Null.N1)
        round = round.placeCard(players,wpTabea, ccBlau11)

        run {
            val expectedBets = mapOf<WizardPlayer, Int?>(
                    wpSebastian to 1,
                    wpTabea to 1,
                    wpDomi to 1
            )

            val expectedStich = Stich(wpDomi, listOf(ccGruen3, Null.N1, ccBlau11))

            expectedViewForPlayerSebastian = expectedViewForPlayerSebastian.copy(
                    phase = OpenWizardRound.Phase.PlayingPhase,
                    bets = expectedBets,
                    currentPlayer = wpTabea,
                    currentStich = expectedStich,
                    ownCards = listOf(ccBlau12),
                    sticheOfPlayer = listOf(expectedStich),
                    numberOfCardsInHandsOfPlayers = mapOf(
                            wpTabea to 1,
                            wpDomi to 1,
                            wpSebastian to 1
                    )
            )
            expectedViewForPlayerTabea = expectedViewForPlayerTabea.copy(
                    phase = OpenWizardRound.Phase.PlayingPhase,
                    bets = expectedBets,
                    currentPlayer = wpTabea,
                    currentStich = expectedStich,
                    ownCards = listOf(ccGelb12),
                    sticheOfPlayer = listOf(expectedStich),
                    numberOfCardsInHandsOfPlayers = mapOf(
                            wpTabea to 1,
                            wpDomi to 1,
                            wpSebastian to 1
                    )
            )
            expectedViewForPlayerDomi = expectedViewForPlayerDomi.copy(
                    phase = OpenWizardRound.Phase.PlayingPhase,
                    bets = expectedBets,
                    currentPlayer = wpTabea,
                    currentStich = expectedStich,
                    ownCards = listOf(ccGelb11),
                    sticheOfPlayer = listOf(expectedStich),
                    numberOfCardsInHandsOfPlayers = mapOf(
                            wpTabea to 1,
                            wpDomi to 1,
                            wpSebastian to 1
                    )
            )

            Truth.assertThat(round.immutableRoundState(gameSettings, players)).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }

        round = round.placeCard(players, wpTabea, ccGelb12)
        round = round.placeCard(players, wpDomi, ccGelb11)
        round = round.placeCard(players, wpSebastian, ccBlau12)

        run {
            val expectedStich = Stich(wpDomi, listOf(ccGruen3, Null.N1, ccBlau11))
            val expectedStich2 = Stich(wpTabea, listOf(ccGelb12, ccGelb11, ccBlau12))

            expectedViewForPlayerSebastian = expectedViewForPlayerSebastian.copy(
                    phase = OpenWizardRound.Phase.RoundEnded,
                    currentPlayer = wpSebastian,
                    currentStich = expectedStich2,
                    ownCards = listOf(),
                    sticheOfPlayer = listOf(expectedStich, expectedStich2),
                    numberOfCardsInHandsOfPlayers = mapOf(
                            wpTabea to 0,
                            wpDomi to 0,
                            wpSebastian to 0
                    )
            )
            expectedViewForPlayerTabea = expectedViewForPlayerTabea.copy(
                    phase = OpenWizardRound.Phase.RoundEnded,
                    currentPlayer = wpSebastian,
                    currentStich = expectedStich2,
                            ownCards = listOf(),
                    sticheOfPlayer = listOf(expectedStich, expectedStich2),
                    numberOfCardsInHandsOfPlayers = mapOf(
                            wpTabea to 0,
                            wpDomi to 0,
                            wpSebastian to 0
                    )
            )
            expectedViewForPlayerDomi = expectedViewForPlayerDomi.copy(
                    phase = OpenWizardRound.Phase.RoundEnded,
                    currentPlayer = wpSebastian,
                    currentStich = expectedStich2,
                    ownCards = listOf(),
                    sticheOfPlayer = listOf(expectedStich, expectedStich2),
                    numberOfCardsInHandsOfPlayers = mapOf(
                            wpTabea to 0,
                            wpDomi to 0,
                            wpSebastian to 0
                    )
            )

            Truth.assertThat(round.immutableRoundState(gameSettings, players)).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }
    }

    @Test
    @DisplayName("Letzte Runde eines 3-Spieler-Spiels")
    fun testLastRound() {
        val deck = Deck.from(
            *(1..60).map { Joker.J1 }.toTypedArray()
        )

        val players = Players(listOf(wpSebastian, wpTabea, wpDomi))

        var round = OpenWizardRound.startNewRoundWithDeck(
            numberOfCards = 20,
            players = players,
            startingPlayer = wpDomi,
            deck = deck
        )

        val expectedImmutableRoundState = ImmutableRoundState(
            gameSettings = gameSettings,
            players = players,
            numberOfCards = 20,
            trumpCard = null
        )

        round = round.placeBet(gameSettings, players, wpDomi, 5)
        round = round.placeBet(gameSettings, players, wpSebastian, 15)
        round = round.placeBet(gameSettings, players, wpTabea, 4)

        for (i in 0..19) {
            round = round.placeCard(players, wpDomi, Joker.J1)
            round = round.placeCard(players, wpSebastian, Joker.J1)
            round = round.placeCard(players, wpTabea, Joker.J1)
        }

        run {
            val expectedStich = Stich(wpDomi, listOf(Joker.J1, Joker.J1, Joker.J1))

            val expectedStiche = listOf(
                expectedStich,
                expectedStich,
                expectedStich,
                expectedStich,
                expectedStich,
                expectedStich,
                expectedStich,
                expectedStich,
                expectedStich,
                expectedStich,
                expectedStich,
                expectedStich,
                expectedStich,
                expectedStich,
                expectedStich,
                expectedStich,
                expectedStich,
                expectedStich,
                expectedStich,
                expectedStich,
            )

            val expectedViewForPlayerSebastian = RoundStateForPlayerNormalRound(
                phase = OpenWizardRound.Phase.RoundEnded,
                currentPlayer = wpDomi,
                currentStich = expectedStich,
                ownCards = listOf(),
                sticheOfPlayer = expectedStiche,
                numberOfCardsInHandsOfPlayers = mapOf(
                    wpTabea to 0,
                    wpDomi to 0,
                    wpSebastian to 0
                ),
                bets = mapOf(
                    wpTabea to 4,
                    wpDomi to 5,
                    wpSebastian to 15
                ),
                trumpColor = TrumpColor.None
            )
            val expectedViewForPlayerTabea = expectedViewForPlayerSebastian.copy(
                phase = OpenWizardRound.Phase.RoundEnded,
                currentPlayer = wpDomi,
                currentStich = expectedStich,
                ownCards = listOf(),
                sticheOfPlayer = expectedStiche,
                numberOfCardsInHandsOfPlayers = mapOf(
                    wpTabea to 0,
                    wpDomi to 0,
                    wpSebastian to 0
                )
            )
            val expectedViewForPlayerDomi = expectedViewForPlayerSebastian.copy(
                phase = OpenWizardRound.Phase.RoundEnded,
                currentPlayer = wpDomi,
                currentStich = expectedStich,
                ownCards = listOf(),
                sticheOfPlayer = expectedStiche,
                numberOfCardsInHandsOfPlayers = mapOf(
                    wpTabea to 0,
                    wpDomi to 0,
                    wpSebastian to 0
                )
            )

            Truth.assertThat(round.immutableRoundState(gameSettings, players)).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }

    }

    // Test: Konvertierung in FinishedWizardRound


}