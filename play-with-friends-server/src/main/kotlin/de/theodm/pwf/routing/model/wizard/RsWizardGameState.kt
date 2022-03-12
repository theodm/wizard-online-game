package de.theodm.pwf.routing.model.wizard

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import de.theodm.Participant
import de.theodm.pwf.routing.lobby.toRsParticipant
import de.theodm.pwf.routing.model.RsParticipant
import de.theodm.wizard.*
import de.theodm.wizard.game.card.TrumpColor
import de.theodm.wizard.game.players.Players
import de.theodm.wizard.game.players.WizardPlayer
import de.theodm.wizard.game.round.OpenWizardRound
import de.theodm.wizard.game.round.RoundStateForPlayer
import de.theodm.wizard.game.round.RoundStateForPlayerFirstRound
import de.theodm.wizard.game.round.RoundStateForPlayerNormalRound
import de.theodm.wizard.game.stich.toPlayerSticheMap

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(
        value = RsRoundStateForPlayerFirstRound::class,
        name = "RsRoundStateForPlayerFirstRound"
    ),
    JsonSubTypes.Type(
        value = RsRoundStateForPlayerNormalRound::class,
        name = "RsRoundStateForPlayerNormalRound"
    )
)
sealed class RsRoundStateForPlayer {
    abstract val phase: OpenWizardRound.Phase
    abstract val bets: Map<String, Int?>
    abstract val currentPlayer: RsParticipant
    abstract val sticheOfPlayer: Map<String, Int>
    abstract val trumpColor: TrumpColor?
    abstract val currentStich: RsStich
}

fun RoundStateForPlayer.toRsRoundStateForPlayer(
    players: Players,
    localPlayer: WizardPlayer
): RsRoundStateForPlayer {
    return when (this) {
        is RoundStateForPlayerFirstRound -> this.toRsRoundStateForPlayerFirstRound(players, localPlayer)
        is RoundStateForPlayerNormalRound -> this.toRsRoundStateForPlayerNormalRound(players)
        else -> TODO("Fehlt")
    }
}

data class RsRoundStateForPlayerFirstRound(
    override val phase: OpenWizardRound.Phase,
    override val bets: Map<String, Int?>,
    override val currentPlayer: RsParticipant,
    override val sticheOfPlayer: Map<String, Int>,
    override val trumpColor: TrumpColor?,
    override val currentStich: RsStich,

    val cardsOfOtherPlayers: Map<String, List<RsWizardCard>>,
    val cmpLocalPlayerHasPlayedCard: Boolean
) : RsRoundStateForPlayer()

fun RoundStateForPlayerFirstRound.toRsRoundStateForPlayerFirstRound(
    players: Players,
    localPlayer: WizardPlayer
): RsRoundStateForPlayerFirstRound {
    return RsRoundStateForPlayerFirstRound(
        phase = this.phase,
        bets = this.bets.mapKeys { it.key.userPublicID() },
        currentPlayer = (this.currentPlayer as Participant).toRsParticipant(),
        sticheOfPlayer = this.sticheOfPlayer.toPlayerSticheMap(players, this.trumpColor).mapKeys { it.key.userPublicID() },
        trumpColor = this.trumpColor,
        currentStich = this.currentStich.toRsStich(players),
        cardsOfOtherPlayers = this.cardsOfOtherPlayers.mapKeys { it.key.userPublicID() }
            .mapValues { it.value.map { it.toRsWizardCard() } },
        cmpLocalPlayerHasPlayedCard = this.currentStich.playerToCardMap(players).containsKey(localPlayer)
    )
}

data class RsRoundStateForPlayerNormalRound(
    override val phase: OpenWizardRound.Phase,
    override val bets: Map<String, Int?>,
    override val currentPlayer: RsParticipant,
    override val sticheOfPlayer: Map<String, Int>,
    override val trumpColor: TrumpColor?,
    override val currentStich: RsStich,

    val ownCards: List<RsCardInHand>,
    val numberOfCardsInHandsOfPlayers: Map<String, Int>
) : RsRoundStateForPlayer()

fun RoundStateForPlayerNormalRound.toRsRoundStateForPlayerNormalRound(players: Players): RsRoundStateForPlayerNormalRound {
    return RsRoundStateForPlayerNormalRound(
        phase = this.phase,
        bets = this.bets.mapKeys { it.key.userPublicID() },
        currentPlayer = (this.currentPlayer as Participant).toRsParticipant(),
        sticheOfPlayer = this.sticheOfPlayer.toPlayerSticheMap(players, this.trumpColor).mapKeys { it.key.userPublicID() },
        trumpColor = this.trumpColor,
        currentStich = this.currentStich.toRsStich(players),
        ownCards = this.ownCards.map { it.toRsCardInHand(players, this.ownCards, this.currentStich) },
        numberOfCardsInHandsOfPlayers = this.numberOfCardsInHandsOfPlayers.mapKeys { it.key.userPublicID() }
    )
}


