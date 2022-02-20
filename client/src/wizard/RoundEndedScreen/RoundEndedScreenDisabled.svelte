<script>
    import RoundEndedScreen from "./RoundEndedScreen.svelte";
    import wizardState from "../GameDemoFiles/8.json"
</script>

<RoundEndedScreen wizardState={wizardState} disabled={true} onNextRoundStarted={() => alert("Nächste Runde gestartet.")}/>