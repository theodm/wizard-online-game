<script>
    import NewHand from "./NewHand.svelte";

    let cards = [
        {
            "wizardCard" : {
                "type" : "RsNumberColorCard",
                "color" : "Blue",
                "number" : 1,
                "cmpTrumpColor" : "Blue"
            },
            "cmpAllowedToPlay" : true
        }
    ]
</script>

<NewHand disabled="false" cards={cards}  onCardPlayed={(card) => alert(JSON.stringify(card))} onTrumpColorSelected={(color) => alert(JSON.stringify(color))}/>