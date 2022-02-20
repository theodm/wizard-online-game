<script lang="typescript">
    import RawHand from "./RawHand.svelte";
</script>

<RawHand cards={[null]} disabled={false} onCardPlayed={(card) => alert(JSON.stringify(card)) }/>