<script>
    import wizardState from "./GameDemoFiles/12.json"
    import WizardUI from "./WizardUI.svelte";
</script>

<WizardUI wizardState={wizardState} localPlayerPublicID="Sebastian123"
          onCardPlayed={(card) => alert(JSON.stringify(card))}
        onNextRoundStarted={() => alert("next round started")}
          onPlaceBet={(bet) => alert(bet) }
          onSelectTrumpColor={(trumpColor) => alert(trumpColor)}
/>