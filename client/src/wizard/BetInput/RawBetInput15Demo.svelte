<script>
    import BetInput from "./RawBetInput.svelte";
</script>
<BetInput maxBet="15" ignoredBet="2" onPlaceBet={(bet) => { alert(bet) }} />