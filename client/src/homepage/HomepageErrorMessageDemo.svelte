<script lang="ts">
    import Homepage from "./Homepage.svelte";
    import {RsShortLobbyInfo} from "../generated/client";

    let name = "Theo";

    const lobbies: RsShortLobbyInfo[] = [{
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
</script>

<Homepage currentLobbies={lobbies} currentName={name} onCreateNewLobbyClicked={() => alert("new lobby")}
          onCurrentNameChanged={(newName) => name = newName} currentError={{ message: "Es ist ein unbekannter Fehler aufgetreten."}}/>