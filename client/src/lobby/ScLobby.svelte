<script lang="ts">
    import type {PlayerStore} from "../session/PlayerStore";
    import {ApiClient} from "../client/ApiClient";
    import type {RsLobby, RsWizardGameStateForPlayer} from "../generated/client";
    import Lobby from "./Lobby.svelte";
    import {LobbySocket, LobbySocketStatus} from "./LobbySocket";

    import {navigate} from "svelte-routing";
    import Wizard from "../wizard/Wizard.svelte";
    import {onDestroy, onMount} from "svelte";
    import EnterUserName from "./EnterUserName.svelte";
    import WaitingForLobby from "./WaitingForLobby.svelte";
    import ErrorForLobby from "./ErrorForLobby.svelte";
    import {createError} from "../components/CreateErrorObject";

    export let lobbyCode: string;
    export let apiClient: ApiClient;
    export let playerStore: PlayerStore;

    let currentSocketStatus: LobbySocketStatus = "Disconnected";
    let lobbyState: RsLobby | undefined = undefined;

    let lobbySocket: LobbySocket | undefined
    let currentError: undefined | {
        message: string,
        timestamp: Date
    } = undefined

    let playerIsLoggedIn = playerStore?.isLoggedIn();
    let loginOrJoinIsLoading: boolean = true;

    function startLobbySocket() {
        lobbySocket = new LobbySocket(
            apiClient,
            playerStore.getPrivateKey()!,
            lobbyCode,
            (newSocketStatus) => {
                currentSocketStatus = newSocketStatus;
            },
            (newLobbyState) => {
                lobbyState = newLobbyState;
            }
        );
    }

    onMount(async () => {
        if (playerStore.isLoggedIn()) {
            try {
                // Benutzer soll der Lobby beitreten
                await apiClient.joinLobby(playerStore.getPrivateKey(), lobbyCode);

                startLobbySocket();
            } catch (e) {
                currentError = createError(e)
            } finally {
                loginOrJoinIsLoading = false;
            }
        }
    })

    onDestroy(async () => {
        lobbySocket?.destroy()
    })

    function navigateToHomepage() {
        navigate(`/`);
    }

    async function login(userName: string) {
        loginOrJoinIsLoading = true;
        try {
            // Benutzer anlegen
            await playerStore.getOrCreateUserForName(userName);

            playerIsLoggedIn = playerStore.isLoggedIn();

            // Benutzer soll der Lobby beitreten
            await apiClient.joinLobby(playerStore.getPrivateKey(), lobbyCode);

            startLobbySocket()
        } catch (e) {
            currentError = createError(e)
        } finally {
            loginOrJoinIsLoading = false
        }
    }

    async function addBot(botType: string) {
        try {
            await lobbySocket?.addBot(botType);
        } catch (e) {
            currentError = createError(e);
        }
    }

    async function startGame() {
        try {
            await lobbySocket?.startGame()
        } catch (e) {
            currentError = createError(e);
        }
    }

    async function leaveLobby() {
        try {
            await lobbySocket?.leaveLobby();

            navigate(`/`);
        } catch (e) {
            currentError = createError(e)
        }
    }

    async function updatePlayerOrder(participants) {
        try {
            await lobbySocket?.updatePlayerOrder(participants.map((it) => it.userPublicID));
        } catch (e) {
            currentError = createError(e)
        }
    }
</script>
{#if !playerIsLoggedIn}
    <EnterUserName currentError={currentError} {lobbyCode} onBackToHomepage={() => navigateToHomepage()} onSubmit={(userName) => login(userName)} />
{:else if loginOrJoinIsLoading || (!lobbyState && currentSocketStatus === "Connecting") || (!lobbyState && currentSocketStatus === "Connected")}
    <WaitingForLobby {lobbyCode}  />
{:else if (!lobbyState && (currentSocketStatus === "Disconnected" || currentSocketStatus === "Error"))}
    <ErrorForLobby currentError={currentError} onBackToHomepage={() => navigateToHomepage()} />
{:else if lobbyState && !lobbyState.inGame}
    <Lobby
            {currentError}
            {lobbyCode}
            {currentSocketStatus}
            {lobbyState}
            onGameStarted={startGame}
            onLeaveLobby={leaveLobby}
            onParticipantsChanged={updatePlayerOrder}
            onSettingsChanged={() => {}}
            ownPublicID={playerStore.getPublicID()}
            onAddBot={addBot}
    />
{:else}
    <Wizard lobbySocket={lobbySocket} playerStore={playerStore}/>
{/if}
