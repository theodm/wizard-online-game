<script lang="ts">
    import BetInput from "./BetInput.svelte";
    import wizardState from "../GameDemoFiles/2.json"

    wizardState.openWizardRound.currentRoundStateForPlayer.bets["Tabea123"] = 1
    wizardState.openWizardRound.currentRoundStateForPlayer.bets["Domi123"] = 0
</script>
<BetInput
        onPlaceBet={(bet) => alert(bet)}
        openWizardRound={wizardState.openWizardRound} localPlayerPublicID={"Sebastian123"}
/>