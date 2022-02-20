<script lang="typescript">
    import {RsWizardCard, RsWizardGameStateForPlayer} from "../generated/client";

    import WizardInRound from "./WizardInRound.svelte";
    import RoundEndedScreen from "./RoundEndedScreen/RoundEndedScreen.svelte";
    import LgPointsModal from "./PointsModal/LgPointsModal.svelte";
    import NButton from "../components/NButton.svelte";

    export let wizardState: RsWizardGameStateForPlayer;
    export let localPlayerPublicID: string;

    export let onPlaceBet: (bet: number) => Promise<void>;
    export let onCardPlayed: (card: RsWizardCard | null) => Promise<void>;
    export let onNextRoundStarted: () => Promise<void>;
    export let onSelectTrumpColor: (trumpColor: "Red" | "Yellow" | "Green" | "Blue") => Promise<void>;

</script>

<div class="h-full relative overflow-hidden">
    {#if wizardState && wizardState.openWizardRound}
        <WizardInRound
                {onSelectTrumpColor}
                {onCardPlayed}
                openWizardRound={wizardState.openWizardRound}
                {wizardState}
                {localPlayerPublicID}
                {onPlaceBet}
                {onNextRoundStarted}
        />
    {/if}
    {#if wizardState && wizardState.openWizardRound && wizardState.openWizardRound.currentRoundStateForPlayer.phase === "RoundEnded"}
        <LgPointsModal wizardGameStateForPlayer={wizardState} mode="NextRound" onStartNextRound={onNextRoundStarted}
                       localPlayerPublicID={localPlayerPublicID}/>
    {/if}
</div>
<style global>

    :root {
        --app-height: 100%;
    }

    html,
    body {
        padding: 0;
        margin: 0;
        width: 100vw;
        height: 100vh;
        height: var(--app-height);
    }

</style>