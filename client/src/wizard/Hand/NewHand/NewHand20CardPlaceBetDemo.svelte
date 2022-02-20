<script>
    import NewHand from "./NewHand.svelte";
    import {onMount} from "svelte";
    import {delay} from "../../../utils/delay";
    import {PlaceBetInput, SelectTrumpColorInput} from "./ExtraOptions";

    let cards = [
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Blue",
                "number": 1,
                "cmpTrumpColor": "Blue"
            },
            "cmpAllowedToPlay": true
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Blue",
                "number": 2,
                "cmpTrumpColor": "Blue"
            },
            "cmpAllowedToPlay": true
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Blue",
                "number": 3,
                "cmpTrumpColor": "Blue"
            },
            "cmpAllowedToPlay": true
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Blue",
                "number": 4,
                "cmpTrumpColor": "Blue"
            },
            "cmpAllowedToPlay": true
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Blue",
                "number": 5,
                "cmpTrumpColor": "Blue"
            },
            "cmpAllowedToPlay": true
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Blue",
                "number": 6,
                "cmpTrumpColor": "Blue"
            },
            "cmpAllowedToPlay": true
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Blue",
                "number": 7,
                "cmpTrumpColor": "Blue"
            },
            "cmpAllowedToPlay": true
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Blue",
                "number": 8,
                "cmpTrumpColor": "Blue"
            },
            "cmpAllowedToPlay": true
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Blue",
                "number": 9,
                "cmpTrumpColor": "Blue"
            },
            "cmpAllowedToPlay": false
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Blue",
                "number": 10,
                "cmpTrumpColor": "Blue"
            },
            "cmpAllowedToPlay": true
        }, {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Green",
                "number": 1,
                "cmpTrumpColor": "Green"
            },
            "cmpAllowedToPlay": true
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Green",
                "number": 2,
                "cmpTrumpColor": "Green"
            },
            "cmpAllowedToPlay": true
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Green",
                "number": 3,
                "cmpTrumpColor": "Green"
            },
            "cmpAllowedToPlay": false
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Green",
                "number": 4,
                "cmpTrumpColor": "Green"
            },
            "cmpAllowedToPlay": true
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Green",
                "number": 5,
                "cmpTrumpColor": "Green"
            },
            "cmpAllowedToPlay": true
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Green",
                "number": 6,
                "cmpTrumpColor": "Green"
            },
            "cmpAllowedToPlay": true
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Green",
                "number": 7,
                "cmpTrumpColor": "Green"
            },
            "cmpAllowedToPlay": false
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Green",
                "number": 8,
                "cmpTrumpColor": "Green"
            },
            "cmpAllowedToPlay": true
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Green",
                "number": 9,
                "cmpTrumpColor": "Green"
            },
            "cmpAllowedToPlay": true
        },
        {
            "wizardCard": {
                "type": "RsNumberColorCard",
                "color": "Green",
                "number": 10,
                "cmpTrumpColor": "Green"
            },
            "cmpAllowedToPlay": true
        },
    ];

    onMount(async () => {
        await delay(2000)

        cards[13].cmpAllowedToPlay = false;
        cards[2].cmpAllowedToPlay = false;
        cards = [...cards];

        await delay(2000)

        cards[17].cmpAllowedToPlay = false;
        cards[5].cmpAllowedToPlay = false;
        cards = [...cards];
    })

</script>

<NewHand disabled="false" cards={cards} extraOptions={new PlaceBetInput(5, 3)}  onCardPlayed={(card) => alert(JSON.stringify(card))} onTrumpColorSelected={(color) => alert(JSON.stringify(color))}/>