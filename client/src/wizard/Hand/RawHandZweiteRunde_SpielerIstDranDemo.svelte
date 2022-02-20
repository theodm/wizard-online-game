<script lang="typescript">
    import RawHand from "./RawHand.svelte";
    import wizardState from "../GameDemoFiles/14.json"


</script>

<RawHand cards={wizardState.openWizardRound.currentRoundStateForPlayer.ownCards} disabled={false}
         onCardPlayed={(card) => alert(JSON.stringify(card)) }/>