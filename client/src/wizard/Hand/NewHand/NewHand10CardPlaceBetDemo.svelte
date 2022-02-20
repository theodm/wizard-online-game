<script>
    import NewHand from "./NewHand.svelte";
    import {PlaceBetInput, SelectTrumpColorInput} from "./ExtraOptions";

    let cards = [
        {
            "wizardCard" : {
                "type" : "RsNumberColorCard",
                "color" : "Blue",
                "number" : 1,
                "cmpTrumpColor" : "Blue"
            },
            "cmpAllowedToPlay" : true
        },
        {
            "wizardCard" : {
                "type" : "RsNumberColorCard",
                "color" : "Blue",
                "number" : 2,
                "cmpTrumpColor" : "Blue"
            },
            "cmpAllowedToPlay" : true
        },
        {
            "wizardCard" : {
                "type" : "RsNumberColorCard",
                "color" : "Blue",
                "number" : 3,
                "cmpTrumpColor" : "Blue"
            },
            "cmpAllowedToPlay" : true
        },
        {
            "wizardCard" : {
                "type" : "RsNumberColorCard",
                "color" : "Blue",
                "number" : 4,
                "cmpTrumpColor" : "Blue"
            },
            "cmpAllowedToPlay" : true
        },
        {
            "wizardCard" : {
                "type" : "RsNumberColorCard",
                "color" : "Blue",
                "number" : 5,
                "cmpTrumpColor" : "Blue"
            },
            "cmpAllowedToPlay" : true
        },
        {
            "wizardCard" : {
                "type" : "RsNumberColorCard",
                "color" : "Blue",
                "number" : 6,
                "cmpTrumpColor" : "Blue"
            },
            "cmpAllowedToPlay" : true
        },
        {
            "wizardCard" : {
                "type" : "RsNumberColorCard",
                "color" : "Blue",
                "number" : 7,
                "cmpTrumpColor" : "Blue"
            },
            "cmpAllowedToPlay" : true
        },
        {
            "wizardCard" : {
                "type" : "RsNumberColorCard",
                "color" : "Blue",
                "number" : 8,
                "cmpTrumpColor" : "Blue"
            },
            "cmpAllowedToPlay" : true
        },
        {
            "wizardCard" : {
                "type" : "RsNumberColorCard",
                "color" : "Blue",
                "number" : 9,
                "cmpTrumpColor" : "Blue"
            },
            "cmpAllowedToPlay" : true
        },
        {
            "wizardCard" : {
                "type" : "RsNumberColorCard",
                "color" : "Blue",
                "number" : 10,
                "cmpTrumpColor" : "Blue"
            },
            "cmpAllowedToPlay" : true
        },
    ]
</script>

<NewHand disabled="false" cards={cards} extraOptions={new PlaceBetInput(20, 1)} onCardPlayed={(card) => alert(JSON.stringify(card))} onTrumpColorSelected={(color) => alert(JSON.stringify(color))}/>