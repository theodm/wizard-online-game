<script lang="ts">

    import {fade} from 'svelte/transition';
    import {RsShortLobbyInfo} from "../generated/client";
    import LobbiesList from "./LobbiesList.svelte";
    import Alert from "./components/Alert.svelte";
    import TextField from "./components/TextField.svelte";
    import Spinner from "../components/Spinner.svelte";
    import Logo from "../components/Logo.svelte";
    import BaseLayout from "../components/BaseLayout.svelte";
    import {validateUserName} from "../client/UserNameValidation";
    import NButton from "../components/NButton.svelte";

    export let userCurrentLobbyID: string | undefined;
    export let onBackToCurrentLobby: () => Promise<void>;
    export let onLeaveCurrentLobby: () => Promise<void>;

    export let onCreateNewLobbyClicked: () => Promise<void>;
    export let onLobbyJoinClicked: (lobbyCode: string) => Promise<void>;

    export let onCurrentNameChanged: (newName: string) => void;
    export let currentName = ""

    export let currentLobbies: RsShortLobbyInfo[] | "IsLoading" | "Error";

    let joinLobbyLoading = false;
    let createLobbyLoading = false;

    export let currentError: undefined | {
        timestamp: Date,
        message: string,
    } = undefined;

    async function _onCreateNewLobbyClicked() {
        createLobbyLoading = true;

        try {
            await onCreateNewLobbyClicked()
        } finally {
            createLobbyLoading = false;
        }
    }

    async function _onLobbyJoinClicked(lobbyCode: string) {
        joinLobbyLoading = true;

        try {
            await onLobbyJoinClicked(lobbyCode)
        } finally {
            joinLobbyLoading = false;
        }
    }

    $: isLoadingLobby = joinLobbyLoading || createLobbyLoading;
    $: nameValidates = validateUserName(currentName).validates
</script>

<BaseLayout>
    <Logo/>
    <div>
        <p class="py-2 text-sm"> Wizard ist ein Kartenspiel, ein Stichspiel, bei welchem Wahrscheinlichkeiten die
            Strategie bestimmen.<sup><a href="https://de.wikipedia.org/wiki/Wizard_(Spiel)">[Wikipedia]</a></sup>
            Auf
            dieser Webseite können Sie das Spiel ohne Registrierung gemeinsam mit Ihren Freunden spielen.</p>

        <p class="py-2 text-sm">Geben Sie sich einen Namen und treten sie der Lobby Ihrer Freunde bei oder erstellen
            Sie
            eine eigene Lobby. Schon geht es los! Viel Spass!</p>
    </div>
    <div>
        <p class="flex justify-center py-2 pt-6">Name eingeben</p>
        <TextField disabled={isLoadingLobby} currentText={currentName} onCurrentTextChanged={onCurrentNameChanged}
                   placeHolder={"Dominik12"}
                   validationFunction={validateUserName}/>
    </div>

    <Alert error={currentError}/>
    {#if userCurrentLobbyID}
        <div transition:fade|local={{duration: 200}}>
            <div class="w-full flex justify-center mt-6 bg-yellow-100 p-5 text-md inline">
                <p>
                    Sie befinden sich aktuell in der Lobby {userCurrentLobbyID}. Wollen Sie in die Lobby <a
                        class="inline hover:cursor-pointer font-bold" on:click={onBackToCurrentLobby}> zurück </a> oder
                    sie <a
                        class="inline hover:cursor-pointer font-bold" on:click={onLeaveCurrentLobby}>verlassen</a>?
                </p>
            </div>
        </div>
    {/if}
    <div>
        <div class="w-full flex justify-center pt-6">
            <NButton type="button"
                    disabled={isLoadingLobby || userCurrentLobbyID || !nameValidates}
                    onClick={_onCreateNewLobbyClicked}
            >
                    Lobby erstellen
            </NButton>
        </div>

        <hr data-content="ODER" class="hr-text mt-6">

        <LobbiesList {currentLobbies} onLobbyJoinClicked={_onLobbyJoinClicked}
                     disabled={isLoadingLobby || userCurrentLobbyID || !nameValidates}/>
    </div>

</BaseLayout>

<style>
    hr.hr-text {
        position: relative;
        border: none;
        height: 1px;
        background: #000000;
    }

    hr.hr-text::before {
        content: attr(data-content);
        display: inline-block;
        background: #fff;
        font-weight: bold;
        font-size: 0.85rem;
        color: #000000;
        border-radius: 30rem;
        padding: 0.2rem 2rem;
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
    }

</style>