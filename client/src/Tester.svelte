<script lang="typescript">

    import RawBetInput15Demo from "./wizard/BetInput/RawBetInput15Demo.svelte";
    import RawBetInput5Demo from "./wizard/BetInput/RawBetInput5Demo.svelte";
    import SelectTrumpInputDemo from "./wizard/SelectTrumpInput/SelectTrumpInputDemo.svelte";
    import RoundEndedScreenFirstRoundDemo from "./wizard/RoundEndedScreen/RoundEndedScreenFirstRoundDemo.svelte";
    import RoundEndedScreenSecondRoundDemo from "./wizard/RoundEndedScreen/RoundEndedScreenSecondRoundDemo.svelte";
    import RawHandErsteRunde_KarteUebrigDemo from "./wizard/Hand/RawHandErsteRunde_KarteUebrigDemo.svelte";
    import RawHandErsteRunde_KarteUebrig_SpielerIstDranDemo
        from "./wizard/Hand/RawHandErsteRunde_KarteUebrig_SpielerIstDranDemo.svelte";
    import RoundEndedScreenDisabled from "./wizard/RoundEndedScreen/RoundEndedScreenDisabled.svelte";
    import RawHandZweiteRunde_SpielerIstDranDemo from "./wizard/Hand/RawHandZweiteRunde_SpielerIstDranDemo.svelte";
    import CardHolderFirstRoundBettingPhaseDemo from "./wizard/CardHolder/CardHolderFirstRoundBettingPhaseDemo.svelte";
    import CardHolderFirstRoundPlayingPhaseDemo from "./wizard/CardHolder/CardHolderFirstRoundPlayingPhaseDemo.svelte";
    import CardHolderSecondRoundPlayingPhaseDemo
        from "./wizard/CardHolder/CardHolderSecondRoundPlayingPhaseDemo.svelte";
    import DeckFirstRoundDemo from "./wizard/Deck/DeckFirstRoundDemo.svelte";
    import DeckFirstRoundTrumpUndecidedDemo from "./wizard/Deck/DeckFirstRoundTrumpUndecidedDemo.svelte";
    import BetInputFirstRoundNotLocalPlayersTurnDemo
        from "./wizard/BetInput/BetInputFirstRoundNotLocalPlayersTurnDemo.svelte";
    import BetInputFirstRoundLocalPlayersTurnDemo
        from "./wizard/BetInput/BetInputFirstRoundLocalPlayersTurnDemo.svelte";
    import BetInputFirstRoundLocalPlayersTurn0NotAllowedDemo
        from "./wizard/BetInput/BetInputFirstRoundLocalPlayersTurn0NotAllowedDemo.svelte";
    import HandFirstRoundCardNotPlayedDemo from "./wizard/Hand/HandFirstRoundCardNotPlayedDemo.svelte";
    import HandFirstRoundCardPlayedDemo from "./wizard/Hand/HandFirstRoundCardPlayedDemo.svelte";
    import HandNormalRoundNotPlayersTurn from "./wizard/Hand/HandNormalRoundNotPlayersTurn.svelte";
    import HandNormalRoundPlayersTurn from "./wizard/Hand/HandNormalRoundPlayersTurn.svelte";
    import WizardUIDemo from "./wizard/WizardUIDemo.svelte";
    import EnterUserNameDemo from "./lobby/EnterUserNameDemo.svelte";
    import HomepageDemo from "./homepage/HomepageDemo.svelte";
    import HomepageLobbiesEmptyDemo from "./homepage/HomepageLobbiesEmptyDemo.svelte";
    import HomepageLobbiesErrorDemo from "./homepage/HomepageLobbiesErrorDemo.svelte";
    import HomepageLobbiesLoadingDemo from "./homepage/HomepageLobbiesLoadingDemo.svelte";
    import HomepageErrorMessageDemo from "./homepage/HomepageErrorMessageDemo.svelte";
    import HomepageInLobbyDemo from "./homepage/HomepageInLobbyDemo.svelte";
    import ErrorForLobbyDemo from "./lobby/ErrorForLobbyDemo.svelte";
    import NewHand1CardDemo from "./wizard/Hand/NewHand/NewHand1CardDemo.svelte";
    import NewHand10CardDemo from "./wizard/Hand/NewHand/NewHand10CardDemo.svelte";
    import NewHand20CardDemo from "./wizard/Hand/NewHand/NewHand20CardDemo.svelte";
    import NewHand10CardTrumpColorDemo from "./wizard/Hand/NewHand/NewHand10CardTrumpColorDemo.svelte";
    import NewHand20CardTrumpColorDemo from "./wizard/Hand/NewHand/NewHand20CardTrumpColorDemo.svelte";
    import NewHand10CardPlaceBetDemo from "./wizard/Hand/NewHand/NewHand10CardPlaceBetDemo.svelte";
    import NewHand20CardPlaceBetDemo from "./wizard/Hand/NewHand/NewHand20CardPlaceBetDemo.svelte";
    import NewHand20RandomCardDemo from "./wizard/Hand/NewHand/NewHand20RandomCardDemo.svelte";

    const tests = [
        ["RawBetInput15Demo - Alle 15 Einträge, 2 deaktiviert", RawBetInput15Demo],
        ["RawBetInput5Demo - 5 Einträge, 4 deaktiviert", RawBetInput5Demo],
        ["BetInputFirstRoundLocalPlayersTurnDemo", BetInputFirstRoundLocalPlayersTurnDemo],
        ["BetInputFirstRoundNotLocalPlayersTurnDemo", BetInputFirstRoundNotLocalPlayersTurnDemo],
        ["BetInputFirstRoundLocalPlayersTurn0NotAllowedDemo", BetInputFirstRoundLocalPlayersTurn0NotAllowedDemo],
        ["SelectTrumpInput - Auswahl der Trumpffarbe", SelectTrumpInputDemo],
        ["RoundEndedScreenFirstRoundDemo - 1. Runde beendet", RoundEndedScreenFirstRoundDemo],
        ["RoundEndedScreenSecondRoundDemo - 2. Runde beendet", RoundEndedScreenSecondRoundDemo],
        ["RoundEndedScreenDisabled", RoundEndedScreenDisabled],
        ["RawHand - 1. Runde - Spieler ist nicht an der Reihe", RawHandErsteRunde_KarteUebrigDemo],
        ["RawHand - 1. Runde - Spieler ist an der Reihe", RawHandErsteRunde_KarteUebrig_SpielerIstDranDemo],
        ["RawHand - 2. Runde - Spieler ist an der Reihe", RawHandZweiteRunde_SpielerIstDranDemo],
        ["CardHolderFirstRoundBettingPhaseDemo - 1 Runde - Vorschau auf Karten der anderen", CardHolderFirstRoundBettingPhaseDemo],
        ["CardHolderFirstRoundPlayingPhaseDemo - 1 Runde - Karten werden abgelegt", CardHolderFirstRoundPlayingPhaseDemo],
        ["CardHolderSecondRoundPlayingPhaseDemo - 2. Runde - Der erste Stich ist schon vergeben.", CardHolderSecondRoundPlayingPhaseDemo],
        ["DeckFirstRoundDemo - 1. Runde - Trumpffarbe bereits ausgewählt", DeckFirstRoundDemo],
        ["DeckFirstRoundTrumpUndecidedDemo - 1. Runde - Trumpffarbe noch nicht ausgewählt", DeckFirstRoundTrumpUndecidedDemo],
        ["HandFirstRoundCardNotPlayedDemo", HandFirstRoundCardNotPlayedDemo],
        ["HandFirstRoundCardPlayedDemo", HandFirstRoundCardPlayedDemo],
        ["HandNormalRoundNotPlayersTurn", HandNormalRoundNotPlayersTurn],
        ["HandNormalRoundPlayersTurn", HandNormalRoundPlayersTurn],
        ["WizardUI - 8", WizardUIDemo],

        ["NewHand1CardDemo", NewHand1CardDemo],
        ["NewHand10CardDemo", NewHand10CardDemo],
        ["NewHand20CardDemo", NewHand20CardDemo],
        ["NewHand10CardTrumpColorDemo", NewHand10CardTrumpColorDemo],
        ["NewHand20CardTrumpColorDemo", NewHand20CardTrumpColorDemo],
        ["NewHand10CardPlaceBetDemo", NewHand10CardPlaceBetDemo],
        ["NewHand20CardPlaceBetDemo", NewHand20CardPlaceBetDemo],
        ["NewHand20RandomCardDemo", NewHand20RandomCardDemo],

        ["EnterUserNameDemo", EnterUserNameDemo],
        ["ErrorForLobbyDemo", ErrorForLobbyDemo],
        ["HomepageDemo", HomepageDemo],
        ["HomepageErrorMessageDemo", HomepageErrorMessageDemo],
        ["HomepageLobbiesEmptyDemo", HomepageLobbiesEmptyDemo],
        ["HomepageLobbiesErrorDemo", HomepageLobbiesErrorDemo],
        ["HomepageLobbiesLoadingDemo", HomepageLobbiesLoadingDemo],
        ["HomepageInLobbyDemo", HomepageInLobbyDemo],
        //
        // ["TooltipDemo", TooltipDemo],
        // ["Tooltip2Demo", Tooltip2Demo],

    ]

    let currentComponent: any;

</script>
<div class="w-full flex h-screen">
    <div class="w-3/12 flex justify-center">
        <div class="m-5">
            <ul>
                {#each tests as test}
                    <li class="text-sm mt-2 p-1 hover:cursor-pointer hover:bg-black hover:text-white"
                        on:click={() => currentComponent = test[1]}>{test[0]}</li>
                {/each}
            </ul>
        </div>
    </div>
    <div class="w-9/12 h-full flex justify-center items-center">
        <svelte:component this={currentComponent}/>
    </div>
</div>
