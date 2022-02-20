<script lang="ts">
    import Homepage from "./Homepage.svelte";
    import {RsShortLobbyInfo} from "../generated/client";
    import {delay} from "../utils/delay";
    import {onMount} from "svelte";

    let name = "Theo";

    const lobbies: RsShortLobbyInfo[] = [{
        lobbyID: "1234",
        isInGame: false,
        players: [
            {
                userPublicID: "123",
                userName: "Theo"
            },
            {
                userPublicID: "456",
                userName: "Tabea"
            },
            {
                userPublicID: "789",
                userName: "Dominik"
            },
            {
                userPublicID: "789",
                userName: "Michelle"
            },
            {
                userPublicID: "789",
                userName: "Alice"
            },
            {
                userPublicID: "789",
                userName: "Andrea"
            },
        ]

    }, {
        lobbyID: "5123",
        isInGame: false,
        players: [
            {
                userPublicID: "456",
                userName: "Anna"
            },
            {
                userPublicID: "789",
                userName: "Marc"
            },
        ]

    }, {
        lobbyID: "2255",
        isInGame: true,
        players: [
            {
                userPublicID: "123",
                userName: "Damian"
            },
            {
                userPublicID: "456",
                userName: "Christian"
            },
            {
                userPublicID: "789",
                userName: "Nico"
            },
            {
                userPublicID: "101",
                userName: "Barbara"
            },
        ]

    }]

    let userCurrentLobbyID = undefined;
    let currentError = undefined
    onMount(async () => {
        await delay(2000);

        userCurrentLobbyID = "2202";

        await delay(5000);

        currentError = {
            message: "Es ist ein Fehler aufgetreten."
        }
    })
</script>
<Homepage currentError={currentError} userCurrentLobbyID={userCurrentLobbyID} onBackToCurrentLobby={() => alert("back to current lobby")}
          onLeaveCurrentLobby={() => alert("leave current lobby")} currentLobbies={lobbies} currentName={name}
          onCreateNewLobbyClicked={async () => { await delay(2000); alert("new lobby"); }}
          onLobbyJoinClicked={async (lobbyCode) => { await delay(2000); alert("join lobby " + lobbyCode); }}
          onCurrentNameChanged={(newName) => name = newName}/>