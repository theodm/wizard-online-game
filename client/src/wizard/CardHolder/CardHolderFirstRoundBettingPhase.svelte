<script lang="ts">
    import CardHolder from "./RawCardHolder.svelte";
    import {
        RsOpenWizardRound,
        RsRoundStateForPlayerFirstRound,
        RsRoundStateForPlayerPhaseEnum
    } from "../../generated/client";
    import {CardHolderDef} from "./CardHolderDef";
    import {playerTextColor} from "../network/UserColors";

    export let openWizardRound: RsOpenWizardRound;
    export let localPlayerPublicID: string;

    /**
     * In der Vorhersage-Runde werden im ersten Spiel alle Karten der Gegner bereits auf dem Spielfeld angezeigt.
     */
    function cardHolderInBettingRound(openWizardRound: RsOpenWizardRound, localPlayerPublicID: string) {
        const currentRoundState = openWizardRound.currentRoundStateForPlayer as RsRoundStateForPlayerFirstRound;

        const result: CardHolderDef[] = [];

        for (const player of openWizardRound.immutableRoundState.players) {
            console.log(currentRoundState.cardsOfOtherPlayers, player.userPublicID);

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

            let cardHolder = {
                playerName: player.userName,
                playerColorClass: playerTextColor(openWizardRound, player.userPublicID),
                isActive: player.userPublicID === currentRoundState.currentPlayer.userPublicID,
                numberCard: currentRoundState.bets[player.userPublicID] !== null ? currentRoundState.bets[player.userPublicID] : undefined,
                colorCard: colorCard(),
                card:
                    player.userPublicID !== localPlayerPublicID
                        ? currentRoundState.cardsOfOtherPlayers[player.userPublicID][0]
                        : undefined,
                middleText: "",
                numOfBet: currentRoundState.bets[player.userPublicID] !== null ? "" + currentRoundState.bets[player.userPublicID] : "?",
                numOfStiche: openWizardRound.currentRoundStateForPlayer.phase === "PlayingPhase" || openWizardRound.currentRoundStateForPlayer.phase === "RoundEnded" ? ("" + currentRoundState.sticheOfPlayer[player.userPublicID]) : undefined,
            };

            console.log("currentRoundState: ", currentRoundState, "cardHolder: ", cardHolder, "currentRoundState.bets[player.userPublicID]: ", currentRoundState.bets[player.userPublicID])

            result.push(cardHolder);
        }

        return result;
    }
</script>
<CardHolder {openWizardRound} cards={cardHolderInBettingRound(openWizardRound, localPlayerPublicID)}/>