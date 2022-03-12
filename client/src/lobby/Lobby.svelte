<script lang="typescript">
    import type {RsLobby, RsLobbySettings, RsParticipant} from "../generated/client";

    import Spinner from "../components/Spinner.svelte";
    import PlayerList from "./PlayerList/PlayerList.svelte";
    import type {LobbySocketStatus} from "./LobbySocket";
    import BaseLayout from "../components/BaseLayout.svelte";
    import Logo from "../components/Logo.svelte";
    import NButton from "../components/NButton.svelte";
    import Alert from "../homepage/components/Alert.svelte";
    import Dropdown from "./Dropdown.svelte";

    export let onParticipantsChanged: (participants: RsParticipant[]) => Promise<void>;
    export let onGameStarted: () => Promise<void>;
    export let onLeaveLobby: () => Promise<void>;
    export let onAddBot: (botKey: string) => Promise<void>;
    export let onSettingsChanged: (settings: RsLobbySettings) => void;

    export let currentError: undefined | {
        message: string,
        timestamp: Date
    }
    export let lobbyCode: string;
    export let ownPublicID: string;

    export let socketStatus: LobbySocketStatus;
    export let lobbyState: RsLobby;

    $: isHost = lobbyState.host.userPublicID === ownPublicID;
</script>


<BaseLayout>
    <Logo/>
    <div>
        <p class="py-1 text-sm">
            Sie befinden sich gerade in der Lobby mit dem Code {lobbyCode}. Ihre Freunde können Sie
            über den folgenden Link einladen. Klicken Sie auf den Link, um ihn in Ihre Zwischenablage zu kopieren.
        </p>
        <p class="py-1 text-md font-bold flex justify-center">
            <a class="cursor-pointer" on:click={() => {
                navigator.clipboard.writeText(window.location.href)
            }}>{window.location.href}</a>
        </p>
        {#if socketStatus === "Disconnected" || socketStatus === "Error"}
            <Alert
                    cssClass={undefined}
                    message="Es besteht zurzeit keine Verbindung zum Server."
            />
        {/if}
        <p class="py-1 text-sm">
            Ihre Freunde können jedoch auch über die Startseite dieser Lobby beitreten. Viel Spass!
        </p>

    </div>

    <Alert error={currentError} cssClass="mt-3"/>

    <div>
        <p class="flex justify-center py-2 pt-6">Aktuelle Spieler</p>


        <div class="flex items-center justify-end">
            <Dropdown caption="Bot hinzufügen" items={[{ label: "RandomBot", key: "RandomBot" }, { label: "MaxPointsBot", key: "MaxPointsBot" }]} onItemClicked={(botType) => onAddBot(botType)}>

            </Dropdown>
        </div>
        <PlayerList
                editable={isHost}
                host={lobbyState.host}
                items={lobbyState.participants}
                onItemsChanged={(participants) => onParticipantsChanged(participants)}
                participantsConnectivityInfo={lobbyState.participantsConnectivityInfo}
        />
    </div>


    <div class="w-full flex justify-center p-2 pt-6 mt-2">
        <NButton type="button"
                 onClick={() => {
            onLeaveLobby();
          }}
                 cssClass="mr-2 text-xs w-auto shadow-sm"
                 outline={true}
        >
            Spiel verlassen
        </NButton>
        <NButton type="button"
                 cssClass="ml-2"
                 onClick={() => onGameStarted()}
                 disabled={!isHost || lobbyState.participants.length < 3}
        >
            Spiel starten
        </NButton>

    </div>


</BaseLayout>