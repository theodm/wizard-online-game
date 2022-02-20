<script lang="ts">
    import BetInput from "./BetInput.svelte";
    import wizardState from "../GameDemoFiles/3.json"

</script>
<BetInput
        onPlaceBet={(bet) => alert(bet)}
        openWizardRound={wizardState.openWizardRound} localPlayerPublicID={"Sebastian123"}
/>