<script lang="ts">
    import Homepage from "./Homepage.svelte";
    import {RsShortLobbyInfo} from "../generated/client";

    let name = "Theo";
</script>

<Homepage currentLobbies={"Loading"} currentName={name} onCreateNewLobbyClicked={() => alert("new lobby")}
          onCurrentNameChanged={(newName) => name = newName}/>