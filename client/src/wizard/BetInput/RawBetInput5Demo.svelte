<script>
    import BetInput from "./RawBetInput.svelte";
</script>
<BetInput maxBet="5" ignoredBet="4" onPlaceBet={(bet) => { alert(bet) }} />