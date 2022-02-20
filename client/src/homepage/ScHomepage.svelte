<script lang="ts">
    import Homepage from "./Homepage.svelte";
    import {PlayerStore} from "../session/PlayerStore";
    import {ApiClient} from "../client/ApiClient";
    import {assertNotFalsy} from "../utils/Assertions";
    import {onDestroy, onMount} from "svelte";
    import {RsShortLobbyInfo, RsUserInfoResponse} from "../generated/client";
    import {createError} from "../components/CreateErrorObject";

    export let playerStore: PlayerStore | undefined;
    export let apiClient: ApiClient;

    export let onLobbyCreatedOrJoined: (lobbyCode: string) => string;

    async function joinLobby(lobbyCode: string) {
        assertNotFalsy(playerStore, "PlayerStore muss an dieser Stelle bereits geladen sein.")

        console.log("[Join Lobby] Trying to join lobby: ", lobbyCode);

        const userName = currentUserName;

        try {
            console.log("[Join Lobby] Ensure valid user and apply current user name: " + userName);
            const user = await playerStore.getOrCreateUserForName(userName);
            console.log("[Join Lobby] User object is: ", user);

            await apiClient.joinLobby(playerStore.getPrivateKey()!, lobbyCode);

            console.log("[Join Lobby] Joined Lobby: ", lobbyCode);

            onLobbyCreatedOrJoined(lobbyCode)
        } catch (e) {
            currentError = createError(e)

            throw e;
        }
    }

    async function createLobby() {
        assertNotFalsy(playerStore, "PlayerStore muss an dieser Stelle bereits geladen sein.")

        console.log("[Create Lobby] Trying to create new Lobby.");

        const userName = currentUserName;

        try {
            console.log("[Create Lobby] Ensure valid user and apply current user name: " + userName);
            const user = await playerStore.getOrCreateUserForName(userName);
            console.log("[Create Lobby] User object is: ", user);

            const lobby = await apiClient.createLobby(user.userKey);

            console.log("[Create Lobby] Lobby response is: ", lobby);

            onLobbyCreatedOrJoined(lobby.lobbyCode)
        } catch (e) {
            currentError = createError(e)

            throw e;
        }
    }

    async function leaveLobby(lobbyCode: string) {
        assertNotFalsy(playerStore, "PlayerStore muss an dieser Stelle bereits geladen sein.")

        console.log(`[Leave Lobby] Trying to leave lobby ${lobbyCode}.`);

        try {
            await apiClient.leaveLobby(playerStore.getPrivateKey(), lobbyCode);

            console.log(`[Leave Lobby] Left lobby ${lobbyCode}.`);
        } catch (e) {
            currentError = createError(e)

            throw e;
        }
    }

    let currentLobbies: RsShortLobbyInfo[] | "Loading" | "Error" = "Loading"
    let lobbiesUpdateEventSource: EventSource | undefined = undefined
    let userInfoUpdateEventSource: EventSource | undefined = undefined
    let currentUserInfo: RsUserInfoResponse | undefined

    onMount(() => {
        lobbiesUpdateEventSource = apiClient
            .registerLobbiesUpdate((newLobbies) => {
                currentLobbies = newLobbies
            })
    });

    onDestroy(() => {
        lobbiesUpdateEventSource?.close()
        userInfoUpdateEventSource?.close()
    })

    let currentUserName: string = "";

    $: {
        if (playerStore && playerStore.isLoggedIn()) {
            console.log("Aktualisiere Textfeld mit Benutzername aus den Session-Informationen: ", playerStore.getUserName())

            currentUserName = playerStore.getUserName()!;

            (async function() {
                console.log("Registriere Listener für Session-Aktualisierungen: ")

                userInfoUpdateEventSource = apiClient
                    .registerUserInfoUpdate(playerStore!.getPrivateKey(), (userInfo) => {
                        currentUserInfo = userInfo
                    });
            })();
        }
    }

    let currentError: { message: string, timestamp: Date } | undefined = undefined
</script>
<Homepage
        userCurrentLobbyID={currentUserInfo?.lobby?.lobbyID}
        onBackToCurrentLobby={() => joinLobby(currentUserInfo?.lobby?.lobbyID)}
        onLeaveCurrentLobby={() => leaveLobby(currentUserInfo?.lobby?.lobbyID)}
        currentError={currentError}
        currentName={currentUserName}
        onCurrentNameChanged={(newName) => currentUserName = newName}
        currentLobbies={currentLobbies}
        onCreateNewLobbyClicked={() => createLobby()}
        onLobbyJoinClicked={(lobbyCode) => joinLobby(lobbyCode)}
/>