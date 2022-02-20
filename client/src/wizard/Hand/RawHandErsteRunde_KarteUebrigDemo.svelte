<script lang="typescript">
    import RawHand from "./RawHand.svelte";
</script>

<RawHand cards={[null]} disabled={true} onCardPlayed={(card) => alert(JSON.stringify(card)) }/>