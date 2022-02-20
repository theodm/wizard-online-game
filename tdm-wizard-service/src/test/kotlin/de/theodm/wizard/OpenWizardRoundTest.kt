package de.theodm.wizard

import com.google.common.truth.Truth
import de.theodm.wizard.card.CardColor
import de.theodm.wizard.card.Joker
import de.theodm.wizard.card.Null
import de.theodm.wizard.card.NumberColorCard
import de.theodm.wizard.stich.Stich
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
                players = listOf(wpSebastian, wpTabea, wpDomi),
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
                sticheOfPlayer = mapOf(
                        wpSebastian to 0,
                        wpTabea to 0,
                        wpDomi to 0
                ),
                cardsOfOtherPlayers = mapOf(
                        wpTabea to listOf(ccBlau11),
                        wpDomi to listOf(ccBlau12)
                ),
                currentStich = Stich.emptyStich(
                    players = players,
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
            Truth.assertThat(round.immutableRoundState()).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)

            Assertions.assertThrows(WrongPhaseException::class.java) { round.placeCard(wpSebastian, ccGruen3) }
            Assertions.assertThrows(WrongPhaseException::class.java) { round.selectTrump(wpSebastian, TrumpColor.Yellow) }
            Assertions.assertThrows(NotPlayersTurnException::class.java) { round.placeBet(wpTabea, 1) }
            Assertions.assertThrows(NotPlayersTurnException::class.java) { round.placeBet(wpDomi, 1) }
        }

        round = round.placeBet(wpSebastian, 0)

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

            Truth.assertThat(round.immutableRoundState()).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)

        }

        round = round.placeBet(wpTabea, 1)

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

            Truth.assertThat(round.immutableRoundState()).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }

        round = round.placeBet(wpDomi, 1)

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

            Truth.assertThat(round.immutableRoundState()).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }

        round = round.placeCard(wpSebastian, ccGruen3)

        run {
            val expectedStich = Stich(players, wpSebastian, listOf(ccGruen3))

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

            Truth.assertThat(round.immutableRoundState()).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }

        round = round.placeCard(wpTabea, ccBlau11)

        run {
            val expectedStich = Stich(players, wpSebastian, listOf(ccGruen3, ccBlau11))

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

            Truth.assertThat(round.immutableRoundState()).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }

        round = round.placeCard(wpDomi, ccBlau12)

        run {
            val expectedStich = Stich(players, wpSebastian, listOf(ccGruen3, ccBlau11, ccBlau12))

            expectedViewForPlayerSebastian = expectedViewForPlayerSebastian.copy(
                    cardsOfOtherPlayers = mapOf(
                            wpTabea to listOf(),
                            wpDomi to listOf()
                    ),
                    phase = OpenWizardRound.Phase.RoundEnded,
                    currentStich = expectedStich,
                    currentPlayer = wpSebastian,
                    sticheOfPlayer = mapOf(
                            wpSebastian to 1,
                            wpTabea to 0,
                            wpDomi to 0
                    )
            )
            expectedViewForPlayerTabea = expectedViewForPlayerTabea.copy(
                    cardsOfOtherPlayers = mapOf(
                            wpSebastian to listOf(),
                            wpDomi to listOf()
                    ),
                    phase = OpenWizardRound.Phase.RoundEnded,
                    currentStich = expectedStich,
                    currentPlayer = wpSebastian,
                    sticheOfPlayer = mapOf(
                            wpSebastian to 1,
                            wpTabea to 0,
                            wpDomi to 0
                    )
            )
            expectedViewForPlayerDomi = expectedViewForPlayerDomi.copy(
                    cardsOfOtherPlayers = mapOf(
                            wpTabea to listOf(),
                            wpSebastian to listOf()
                    ),
                    phase = OpenWizardRound.Phase.RoundEnded,
                    currentStich = expectedStich,
                    currentPlayer = wpSebastian,
                    sticheOfPlayer = mapOf(
                            wpSebastian to 1,
                            wpTabea to 0,
                            wpDomi to 0
                    )
            )

            Truth.assertThat(round.immutableRoundState()).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }
    }

    private val deck2 = Deck.from(
            Joker, // Trumpfkarte
            ccGruen3,  // Domi
            ccGelb11,
            ccBlau11, // Tabea
            ccGelb12,
            ccBlau12, // Sebastian
            Null,
    )

    @Test
    @DisplayName("Runde 2 dabei wurde ein Zauberer als Trumpfkarte gezogen.")
    fun testNormalRoundAndChooseTrump() {
        var round = OpenWizardRound.startNewRoundWithDeck(
                numberOfCards = 2,
                players = Players(listOf(wpSebastian, wpTabea, wpDomi)),
                startingPlayer = wpDomi,
                deck = deck2
        )

        val expectedImmutableRoundState = ImmutableRoundState(
                players = listOf(wpSebastian, wpTabea, wpDomi),
                numberOfCards = 2,
                trumpCard = Joker
        )

        var expectedViewForPlayerSebastian = RoundStateForPlayerNormalRound(
                phase = OpenWizardRound.Phase.SelectTrumpPhase,
                bets = mapOf<WizardPlayer, Int?>(
                        wpSebastian to null,
                        wpTabea to null,
                        wpDomi to null
                ),
                currentPlayer = wpDomi,
                trumpColor = null,
                sticheOfPlayer = mapOf(
                        wpSebastian to 0,
                        wpTabea to 0,
                        wpDomi to 0
                ),
                ownCards = listOf(
                        ccBlau12,
                        Null
                ),
                currentStich = Stich(players, wpDomi, listOf()),
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
            Truth.assertThat(round.immutableRoundState()).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }

        round = round.selectTrump(wpDomi, TrumpColor.Blue)

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

            Truth.assertThat(round.immutableRoundState()).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }

        round = round.placeBet(wpDomi, 1)
        round = round.placeBet(wpSebastian, 1)
        round = round.placeBet(wpTabea, 1)

        round = round.placeCard(wpDomi, ccGruen3)
        round = round.placeCard(wpSebastian, Null)
        round = round.placeCard(wpTabea, ccBlau11)

        run {
            val expectedBets = mapOf<WizardPlayer, Int?>(
                    wpSebastian to 1,
                    wpTabea to 1,
                    wpDomi to 1
            )

            val expectedStich = Stich(players, wpDomi, listOf(ccGruen3, Null, ccBlau11))

            expectedViewForPlayerSebastian = expectedViewForPlayerSebastian.copy(
                    phase = OpenWizardRound.Phase.PlayingPhase,
                    bets = expectedBets,
                    currentPlayer = wpTabea,
                    currentStich = expectedStich,
                    ownCards = listOf(ccBlau12),
                    sticheOfPlayer = mapOf(
                            wpTabea to 1,
                            wpDomi to 0,
                            wpSebastian to 0
                    ),
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
                    sticheOfPlayer = mapOf(
                            wpTabea to 1,
                            wpDomi to 0,
                            wpSebastian to 0
                    ),
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
                    sticheOfPlayer = mapOf(
                            wpTabea to 1,
                            wpDomi to 0,
                            wpSebastian to 0
                    ),
                    numberOfCardsInHandsOfPlayers = mapOf(
                            wpTabea to 1,
                            wpDomi to 1,
                            wpSebastian to 1
                    )
            )

            Truth.assertThat(round.immutableRoundState()).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }

        round = round.placeCard(wpTabea, ccGelb12)
        round = round.placeCard(wpDomi, ccGelb11)
        round = round.placeCard(wpSebastian, ccBlau12)

        run {
            val expectedStich = Stich(players, wpTabea, listOf(ccGelb12, ccGelb11, ccBlau12))

            expectedViewForPlayerSebastian = expectedViewForPlayerSebastian.copy(
                    phase = OpenWizardRound.Phase.RoundEnded,
                    currentPlayer = wpSebastian,
                    currentStich = expectedStich,
                    ownCards = listOf(),
                    sticheOfPlayer = mapOf(
                            wpTabea to 1,
                            wpDomi to 0,
                            wpSebastian to 1
                    ),
                    numberOfCardsInHandsOfPlayers = mapOf(
                            wpTabea to 0,
                            wpDomi to 0,
                            wpSebastian to 0
                    )
            )
            expectedViewForPlayerTabea = expectedViewForPlayerTabea.copy(
                    phase = OpenWizardRound.Phase.RoundEnded,
                    currentPlayer = wpSebastian,
                    currentStich = expectedStich,
                            ownCards = listOf(),
                    sticheOfPlayer = mapOf(
                            wpTabea to 1,
                            wpDomi to 0,
                            wpSebastian to 1
                    ),
                    numberOfCardsInHandsOfPlayers = mapOf(
                            wpTabea to 0,
                            wpDomi to 0,
                            wpSebastian to 0
                    )
            )
            expectedViewForPlayerDomi = expectedViewForPlayerDomi.copy(
                    phase = OpenWizardRound.Phase.RoundEnded,
                    currentPlayer = wpSebastian,
                    currentStich = expectedStich,
                    ownCards = listOf(),
                    sticheOfPlayer = mapOf(
                            wpTabea to 1,
                            wpDomi to 0,
                            wpSebastian to 1
                    ),
                    numberOfCardsInHandsOfPlayers = mapOf(
                            wpTabea to 0,
                            wpDomi to 0,
                            wpSebastian to 0
                    )
            )

            Truth.assertThat(round.immutableRoundState()).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }
    }

    @Test
    @DisplayName("Letzte Runde eines 3-Spieler-Spiels")
    fun testLastRound() {
        val deck = Deck.from(
            *(1..60).map { Joker }.toTypedArray()
        )

        var round = OpenWizardRound.startNewRoundWithDeck(
            numberOfCards = 20,
            players = Players(listOf(wpSebastian, wpTabea, wpDomi)),
            startingPlayer = wpDomi,
            deck = deck
        )

        val expectedImmutableRoundState = ImmutableRoundState(
            players = listOf(wpSebastian, wpTabea, wpDomi),
            numberOfCards = 20,
            trumpCard = null
        )

        round = round.placeBet(wpDomi, 5)
        round = round.placeBet(wpSebastian, 15)
        round = round.placeBet(wpTabea, 4)

        for (i in 0..19) {
            round = round.placeCard(wpDomi, Joker)
            round = round.placeCard(wpSebastian, Joker)
            round = round.placeCard(wpTabea, Joker)
        }

        run {
            val expectedStich = Stich(players, wpDomi, listOf(Joker, Joker, Joker))

            val expectedViewForPlayerSebastian = RoundStateForPlayerNormalRound(
                phase = OpenWizardRound.Phase.RoundEnded,
                currentPlayer = wpDomi,
                currentStich = expectedStich,
                ownCards = listOf(),
                sticheOfPlayer = mapOf(
                    wpTabea to 0,
                    wpDomi to 20,
                    wpSebastian to 0
                ),
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
                sticheOfPlayer = mapOf(
                    wpTabea to 0,
                    wpDomi to 20,
                    wpSebastian to 0
                ),
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
                sticheOfPlayer = mapOf(
                    wpTabea to 0,
                    wpDomi to 20,
                    wpSebastian to 0
                ),
                numberOfCardsInHandsOfPlayers = mapOf(
                    wpTabea to 0,
                    wpDomi to 0,
                    wpSebastian to 0
                )
            )

            Truth.assertThat(round.immutableRoundState()).isEqualTo(expectedImmutableRoundState)

            Truth.assertThat(round.viewForPlayer(wpSebastian)).isEqualTo(expectedViewForPlayerSebastian)
            Truth.assertThat(round.viewForPlayer(wpTabea)).isEqualTo(expectedViewForPlayerTabea)
            Truth.assertThat(round.viewForPlayer(wpDomi)).isEqualTo(expectedViewForPlayerDomi)
        }

    }

    // Test: Konvertierung in FinishedWizardRound


}