package de.theodm.pwf.routing.model.wizard

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import de.theodm.pwf.routing.lobby.LobbyParticipant
import de.theodm.pwf.routing.lobby.toRsParticipant
import de.theodm.pwf.routing.model.RsParticipant
import de.theodm.wizard.*

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
    localPlayer: WizardPlayer
): RsRoundStateForPlayer {
    return when (this) {
        is RoundStateForPlayerFirstRound -> this.toRsRoundStateForPlayerFirstRound(localPlayer)
        is RoundStateForPlayerNormalRound -> this.toRsRoundStateForPlayerNormalRound()
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
    localPlayer: WizardPlayer
): RsRoundStateForPlayerFirstRound {
    return RsRoundStateForPlayerFirstRound(
        phase = this.phase,
        bets = this.bets.mapKeys { it.key.userPublicID() },
        currentPlayer = (this.currentPlayer as LobbyParticipant).toRsParticipant(),
        sticheOfPlayer = this.sticheOfPlayer.mapKeys { it.key.userPublicID() },
        trumpColor = this.trumpColor,
        currentStich = this.currentStich.toRsStich(),
        cardsOfOtherPlayers = this.cardsOfOtherPlayers.mapKeys { it.key.userPublicID() }
            .mapValues { it.value.map { it.toRsWizardCard() } },
        cmpLocalPlayerHasPlayedCard = this.currentStich.playerToCardMap().containsKey(localPlayer)
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

fun RoundStateForPlayerNormalRound.toRsRoundStateForPlayerNormalRound(): RsRoundStateForPlayerNormalRound {
    return RsRoundStateForPlayerNormalRound(
        phase = this.phase,
        bets = this.bets.mapKeys { it.key.userPublicID() },
        currentPlayer = (this.currentPlayer as LobbyParticipant).toRsParticipant(),
        sticheOfPlayer = this.sticheOfPlayer.mapKeys { it.key.userPublicID() },
        trumpColor = this.trumpColor,
        currentStich = this.currentStich.toRsStich(),
        ownCards = this.ownCards.map { it.toRsCardInHand(this.ownCards, this.currentStich) },
        numberOfCardsInHandsOfPlayers = this.numberOfCardsInHandsOfPlayers.mapKeys { it.key.userPublicID() }
    )
}


