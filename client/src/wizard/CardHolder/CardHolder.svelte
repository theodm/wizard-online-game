<script lang="ts">
    import {RsOpenWizardRound} from "../../generated/client";
    import CardHolderFirstRoundBettingPhase from "./CardHolderFirstRoundBettingPhase.svelte";
    import CardHolderPlayingPhase from "./CardHolderPlayingPhase.svelte";

    export let openWizardRound: RsOpenWizardRound;
    export let localPlayerPublicID: string;

    $: isFirstRoundAndBettingPhase = openWizardRound.currentRoundStateForPlayer.type === "RsRoundStateForPlayerFirstRound" && (openWizardRound.currentRoundStateForPlayer.phase === "BettingPhase" || openWizardRound.currentRoundStateForPlayer.phase === "SelectTrumpPhase")
</script>
{#if isFirstRoundAndBettingPhase}
    <CardHolderFirstRoundBettingPhase openWizardRound={openWizardRound} localPlayerPublicID={localPlayerPublicID}/>
{:else}
    <CardHolderPlayingPhase openWizardRound={openWizardRound} localPlayerPublicID={localPlayerPublicID}/>
{/if}
<style>
</style>