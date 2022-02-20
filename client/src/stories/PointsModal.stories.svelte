<script lang="ts">
    import { Meta, Template, Story } from "@storybook/addon-svelte-csf";
    import PointsModal from "../wizard/PointsModal/PointsModal.svelte";

    import "../styles/style.css";

    import {Player, PlayerResultInRound, Round} from "../wizard/PointsModal/FnPointsModal";
    import {bool, integer, MersenneTwister19937} from "random-js";
    import {generatePlayers, generateRounds} from "./PointsModal.logic";


</script>

<Meta
        title="Wizard/PointsModal"
        argTypes={{
    numberOfPlayers: { control: "number" },
    numberOfRounds: { control: "number" },
  }}
/>

<Template let:args>
    <PointsModal
            players={generatePlayers(args.numberOfPlayers)}
            data={generateRounds(args.numberOfRounds)}
    />
</Template>

<Story
        name="_3 Spieler 20 Runden"
        args={{
            numberOfPlayers: 3,
            numberOfRounds: 20
        }}
/>

<Story
        name="_4 Spieler 15 Runden"
        args={{
            numberOfPlayers: 4,
            numberOfRounds: 15
        }}
/>

<Story
        name="_5 Spieler 12 Runden"
        args={{
            numberOfPlayers: 5,
            numberOfRounds: 12
        }}
/>


<Story
        name="_6 Spieler 10 Runden"
        args={{
            numberOfPlayers: 6,
            numberOfRounds: 10
        }}
/>
<!--<Story name="LoggedOut" args={{}} />-->
