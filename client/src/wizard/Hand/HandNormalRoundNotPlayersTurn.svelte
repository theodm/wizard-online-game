<script lang="ts">
    import Hand from "./Hand.svelte";
    import wizardState from "../GameDemoFiles/16.json"
</script>

<Hand openWizardRound={wizardState.openWizardRound} localPlayerPublicID={"Sebastian123"}
      onCardPlayed={(it) => alert(JSON.stringify(it))}/>