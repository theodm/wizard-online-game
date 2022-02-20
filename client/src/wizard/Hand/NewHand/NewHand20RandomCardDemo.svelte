<script lang="ts">
    import NewHand from "./NewHand.svelte";
    import {onMount} from "svelte";
    import {delay} from "../../../utils/delay";
    import {randomCard} from "./OrderCards";
    import {RsCardInHand} from "../../../generated/client";

    let cards: RsCardInHand[] = [];

    for (let i = 0; i < 20; i++) {
        cards.push({
            wizardCard: randomCard(),
            cmpAllowedToPlay: true
        })
    }

    console.log(cards)

    onMount(async () => {
    })
</script>

<NewHand disabled="false" cards={cards} trumpColor="Green" onCardPlayed={(card) => alert(JSON.stringify(card))}
         onTrumpColorSelected={(color) => alert(JSON.stringify(color))}/>