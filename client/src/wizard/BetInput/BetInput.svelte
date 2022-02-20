<script lang="ts">
    import RawBetInput from "./RawBetInput.svelte";
    import {RsOpenWizardRound, RsRoundStateForPlayer, RsRoundStateForPlayerFirstRound} from "../../generated/client";

    export let onPlaceBet: (bet: number) => Promise<void>;
    export let openWizardRound: RsOpenWizardRound;
    export let localPlayerPublicID: string;

    /**
     * Wie viele Stiche wurden bisher angesagt?
     */
    function currentBets(roundState: RsRoundStateForPlayer) {
        const bets = Object.entries(roundState.bets)
            .filter((it) => it !== null)
            .map((it) => it[1])
            .reduce((a, b) => a + b);

        return bets;
    }

    /**
     * Ist der Spieler der letzte Spieler, der eine Wette abgeben muss?
     */
    function isLastInRound(roundStateForPlayer: RsRoundStateForPlayer, playerPublicID: string) {
        const bets = Object.entries(roundStateForPlayer.bets)
            .filter((it) => it[1] === null)

        if (bets.length > 1) {
            // Es sind noch mehrere Tipps abzugeben, daher ist der Spieler nicht
            // der letzte.
            return false;
        }

        if (bets.length === 0) {
            throw new Error("bets.length sollte an dieser Stelle niemals 0 sein.")
        }

        return bets[0][0] === playerPublicID;
    }

    /**
     * Welcher Wert darf nicht ausgewählt werden? Ein Wert darf nicht ausgewählt werden,
     * wenn der Spieler der letzte in der Reihe ist und das Spiel sonst aufgehen würde.
     */
    function ignoredBet(openWizardRound: RsOpenWizardRound) {
        const currentRoundState = openWizardRound.currentRoundStateForPlayer as RsRoundStateForPlayerFirstRound;

        if (isLastInRound(currentRoundState, localPlayerPublicID)) {
            return openWizardRound.immutableRoundState.numberOfCards - currentBets(currentRoundState);
        }

        return undefined;
    }

    $: isLocalPlayersTurn =
        openWizardRound.currentRoundStateForPlayer.currentPlayer.userPublicID ===
        localPlayerPublicID;
</script>
<RawBetInput
        {onPlaceBet}
        maxBet={openWizardRound.immutableRoundState.numberOfCards}
        ignoredBet={ignoredBet(openWizardRound)}
        disabled={!isLocalPlayersTurn}
/>