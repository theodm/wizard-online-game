<script lang="typescript">
    import type {RsCardInHand, RsWizardCard} from "../../generated/client";

    import Card from "../card/Card.svelte";
    import RawCard from "../card/RawCard.svelte";

    export let onCardPlayed: (card: RsWizardCard | null) => Promise<void>;

    export let cards: Array<RsCardInHand | null>;
    export let disabled: boolean;

</script>

<div class="">
    <h1 class="flex justify-center text-xs text-white pb-2">Eigene Karten</h1>
    <div class="flex justify-center items-center" style={`padding-left: 2em`}>
        {#each cards as card, i}
            <div class={"negative-margin " + (!disabled && (card == null || card.cmpAllowedToPlay) ? "cursor-pointer hover:-translate-y-6" : "")}
                 on:click={() => !disabled && (card == null || card.cmpAllowedToPlay) && onCardPlayed(card !== null ? card.wizardCard : null)}>
                {#if card === null}
                    <RawCard cssClass="portrait:card-w-sm landscape:card-h-md" cardColor="Null" textInTheMiddle="?"
                             textOnTheSides="?"/>
                {:else}
                    <Card cssClass="portrait:card-w-sm landscape:card-h-md" card={card.wizardCard}/>
                {/if}
            </div>
        {/each}
    </div>
</div>

<style global>
    @media (orientation: portrait) {
        .negative-margin {
            margin-left: -6vw;
        }
    }

    @media (orientation: landscape) {
        .negative-margin {
            margin-left: -6vh;
        }
    }
</style>
