<script lang="ts">
    import {PlayerStore} from "./session/PlayerStore";
    import {Router, Route, Link} from "svelte-routing";

    import {navigate} from "svelte-routing";
    import Tester from "./Tester.svelte";

    import "./styles/style.css";
    import 'animate.css';

    import WizardTester from "./wizard/WizardTester.svelte";
    import WizardTesterFull from "./wizard/WizardTesterFull.svelte";
    import {ApiClient} from "./client/ApiClient";
    import {onMount} from "svelte";
    import WizardTesterLastRound from "./wizard/WizardTesterLastRound.svelte";
    import Homepage from "./homepage/Homepage.svelte";
    import ScHomepage from "./homepage/ScHomepage.svelte";
    import ScLobby from "./lobby/ScLobby.svelte";
    import ScLobbyWrapper from "./lobby/ScLobbyWrapper.svelte";

    const api = ApiClient.create()
    let playerStore: PlayerStore | undefined = undefined

    onMount(async () => {
        playerStore = await PlayerStore.loadFromWebStorage(
            async (privateKey) => await api.getLoggedInUserInformation(privateKey),
            async (privateKey, newUserName) => await api.changeUserName(privateKey, newUserName),
            async (userName) => await api.createUser(userName)
        )
    })
</script>

<Router url="">
    <Route path="/tester" component={Tester}/>
    <Route path="/wizard" component={WizardTester}/>
    <Route path="/wizard2" component={WizardTesterFull}/>
    <Route path="/wizard3" component={WizardTesterLastRound}/>

    <Route path="/lobby/:lobbyCode" let:params>
        <ScLobbyWrapper {playerStore} apiClient={api} lobbyCode={params.lobbyCode}/>
    </Route>

    <Route path="/">
        <ScHomepage {playerStore} apiClient={api}
                    onLobbyCreatedOrJoined={(lobbyCode) => {
        // Workaround: setTimeout, sonst kommt es zu folgendem Problem:
        // https://github.com/EmilTholin/svelte-routing/issues/223
        navigate(`/lobby/${lobbyCode}`)

      }}/>
    </Route>
</Router>
