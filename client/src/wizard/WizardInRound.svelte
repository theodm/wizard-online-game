<script lang="ts">
    import {
        RsOpenWizardRound,
        RsRoundStateForPlayerFirstRound, RsRoundStateForPlayerNormalRound,
        RsRoundStateForPlayerPhaseEnum,
        RsWizardCard,
    } from "../generated/client";
    import GameLayout from "./GameLayout/GameLayout.svelte";

    import SelectTrumpInput from "./SelectTrumpInput/SelectTrumpInput.svelte";
    import CardHolder from "./CardHolder/CardHolder.svelte";
    import Deck from "./Deck/Deck.svelte";
    import BetInput from "./BetInput/BetInput.svelte";
    import Hand from "./Hand/Hand.svelte";
    import NewHand from "./Hand/NewHand/NewHand.svelte";
    import {playerBackgroundColor} from "./network/UserColors";

    export let openWizardRound: RsOpenWizardRound;
    export let localPlayerPublicID: string;

    export let onPlaceBet: (bet: number) => Promise<void>;
    export let onCardPlayed: (card: RsWizardCard | null) => Promise<void>;
    export let onSelectTrumpColor: (trumpColor: "Red" | "Yellow" | "Green" | "Blue") => Promise<void>;

    export function currentBackgroundColor(
        openWizardRound: RsOpenWizardRound
    ) {
        const indexToColorMap = new Map([
            [0, "bg-red-200"],
            [1, "bg-green-200"],
            [2, "bg-blue-200"],
            [3, "bg-yellow-200"],
            [4, "bg-violet-200"],
            [5, "bg-orange-200"],
            [5, "bg-orange-200"],
        ])

        console.log("indexToColorMap: ", indexToColorMap)

        let userIDToIndexMap = new Map(openWizardRound
            .immutableRoundState
            .players
            .map((it, index) => [it.userPublicID, index] as [string, number])
            .map(it => [it[0], indexToColorMap.get(it[1])])
        );

        console.log("userIDToIndexMap: ", userIDToIndexMap)

        return userIDToIndexMap.get(
            openWizardRound
            .currentRoundStateForPlayer
            .currentPlayer
            .userPublicID
        );
    }

    export function currentActionText(
        openWizardRound: RsOpenWizardRound,
        localPlayerPublicID: string
    ) {
        const currentRoundState = openWizardRound.currentRoundStateForPlayer as
            | RsRoundStateForPlayerFirstRound
            | RsRoundStateForPlayerNormalRound;
        const localPlayerIsCurrentPlayer =
            currentRoundState.currentPlayer.userPublicID === localPlayerPublicID;

        if (
            currentRoundState.phase ===
            RsRoundStateForPlayerPhaseEnum.SelectTrumpPhase
        ) {
            if (localPlayerIsCurrentPlayer) {
                return "Bitte wählen Sie die Trumpffarbe aus.";
            } else {
                return (
                    currentRoundState.currentPlayer.userName +
                    " wählt eine Trumpffarbe aus."
                );
            }
        }

        if (
            currentRoundState.phase ===
            RsRoundStateForPlayerPhaseEnum.BettingPhase
        ) {
            if (localPlayerIsCurrentPlayer) {
                return "Bitte geben Sie einen Tipp ab.";
            } else {
                return currentRoundState.currentPlayer.userName + " gibt einen Tipp ab.";
            }
        }

        if (
            currentRoundState.phase ===
            RsRoundStateForPlayerPhaseEnum.PlayingPhase
        ) {
            if (localPlayerIsCurrentPlayer) {
                return "Bitte spielen Sie eine Karte aus.";
            } else {
                return (
                    currentRoundState.currentPlayer.userName + " spielt eine Karte aus."
                );
            }
        }

        if (
            currentRoundState.phase ===
            RsRoundStateForPlayerPhaseEnum.RoundEnded
        ) {
            if (localPlayerIsCurrentPlayer) {
                return "Runde ist beedet. Starten Sie eine neue.";
            } else {
                return (
                    "Runde ist beendet. " + currentRoundState.currentPlayer.userName + " kann die neue Runde starten."
                );
            }
        }

        throw new Error("test");
    }

    $: localPlayerIsCurrentPlayer =  (openWizardRound.currentRoundStateForPlayer as RsRoundStateForPlayerFirstRound).currentPlayer.userPublicID === localPlayerPublicID;
    $: backgroundColorClass = localPlayerIsCurrentPlayer ? playerBackgroundColor(openWizardRound, localPlayerPublicID) : ""
    $: currentPhase = (openWizardRound.currentRoundStateForPlayer as RsRoundStateForPlayerFirstRound).phase;
    $: actionName = currentActionText(openWizardRound, localPlayerPublicID);
</script>

<div class={`h-full ${backgroundColorClass} transition-colors`} style={"transition-duration: 1000ms;"} >
    <div class="h-[50%]">
        <CardHolder openWizardRound={openWizardRound} localPlayerPublicID={localPlayerPublicID}/>
    </div>
    <div class="h-[50%]">
        <Hand
                onSelectBet={onPlaceBet}
                onTrumpColorSelected={onSelectTrumpColor}
                openWizardRound={openWizardRound}
                localPlayerPublicID={localPlayerPublicID}
                onCardPlayed={onCardPlayed}
        />
    </div>

</div>

<!--<GameLayout actionText={actionName}>-->
<!--    <svelte:fragment slot="cards">-->
<!--        <CardHolder openWizardRound={openWizardRound} localPlayerPublicID={localPlayerPublicID}/>-->
<!--    </svelte:fragment>-->
<!--    <svelte:fragment slot="deck">-->
<!--        <Deck openWizardRound={openWizardRound}/>-->
<!--    </svelte:fragment>-->
<!--    <svelte:fragment slot="hand">-->
<!--    </svelte:fragment>-->
<!--    <svelte:fragment slot="input">-->
<!--        {#if currentPhase === RsRoundStateForPlayerPhaseEnum.BettingPhase}-->
<!--            <BetInput-->
<!--                    openWizardRound={openWizardRound}-->
<!--                    localPlayerPublicID={localPlayerPublicID}-->
<!--                    onPlaceBet={onPlaceBet}-->
<!--            />-->
<!--        {:else if currentPhase === RsRoundStateForPlayerPhaseEnum.SelectTrumpPhase}-->
<!--            <SelectTrumpInput {onSelectTrumpColor}/>-->
<!--        {/if}-->
<!--    </svelte:fragment>-->
<!--</GameLayout>-->

