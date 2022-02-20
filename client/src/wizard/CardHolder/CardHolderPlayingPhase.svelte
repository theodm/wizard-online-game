<script lang="ts">
    import CardHolder from "./RawCardHolder.svelte";
    import {
        RsOpenWizardRound,
        RsRoundStateForPlayerFirstRound,
        RsRoundStateForPlayerPhaseEnum, RsRoundStateForPlayerTrumpColorEnum
    } from "../../generated/client";
    import {CardHolderDef} from "./CardHolderDef";
    import {playerTextColor} from "../network/UserColors";

    export let openWizardRound: RsOpenWizardRound;
    export let localPlayerPublicID: string;

    /**
     * In der tatsächlichen Spielrunde, in der die Karten ausgespielt werden, dann nicht mehr. Dort werden sie nur
     * nacheinander aufgedeckt. Das sieht genauso aus, wie in einer normalen Runde.
     */
    function cardHolderInPlayingRound(openWizardRound: RsOpenWizardRound, localPlayerPublicID: string) {
        const currentRoundState = openWizardRound.currentRoundStateForPlayer as RsRoundStateForPlayerFirstRound;

        const result: CardHolderDef[] = [];

        for (let i = 0; i < openWizardRound.immutableRoundState.players.length; i++) {
            const player = openWizardRound.immutableRoundState.players[i];

            const cardOfPlayer = openWizardRound.currentRoundStateForPlayer.currentStich.cmpPlayerToCardMap[player.userPublicID];

            const phase = openWizardRound.currentRoundStateForPlayer.phase;
            const colorCard = () => {
                if (phase !== RsRoundStateForPlayerPhaseEnum.BettingPhase)
                    return undefined

                if (openWizardRound.immutableRoundState.trumpCard?.type !== "RsJoker")
                    return undefined

                if (player.userPublicID !== openWizardRound.currentRoundStateForPlayer.currentStich.startPlayer.userPublicID)
                    return undefined;

                return openWizardRound.currentRoundStateForPlayer.trumpColor as "Yellow" | "Red" | "Blue" | "Green";
            }

            result.push({
                playerName: player.userName,
                playerColorClass: playerTextColor(openWizardRound, player.userPublicID),
                isActive: player.userPublicID === currentRoundState.currentPlayer.userPublicID,
                card: cardOfPlayer,
                colorCard: colorCard(),
                numberCard: openWizardRound.currentRoundStateForPlayer.phase === RsRoundStateForPlayerPhaseEnum.BettingPhase ? currentRoundState.bets[player.userPublicID] : undefined,
                middleText: "",
                numOfBet: openWizardRound.currentRoundStateForPlayer.phase === "PlayingPhase" || openWizardRound.currentRoundStateForPlayer.phase === "RoundEnded" ? (currentRoundState.bets[player.userPublicID] !== null ? "" + currentRoundState.bets[player.userPublicID] : "?") : undefined,
                numOfStiche: openWizardRound.currentRoundStateForPlayer.phase === "PlayingPhase" || openWizardRound.currentRoundStateForPlayer.phase === "RoundEnded" ? ("" + currentRoundState.sticheOfPlayer[player.userPublicID]) : undefined,
            });
        }

        return result;
    }
</script>
<CardHolder {openWizardRound} cards={cardHolderInPlayingRound(openWizardRound, localPlayerPublicID)}/>