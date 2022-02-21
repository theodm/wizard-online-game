<script lang="ts">
    import { Meta, Template, Story } from "@storybook/addon-svelte-csf";

    import "../styles/style.css";

    import Dropdown from "../lobby/Dropdown.svelte";
    import {generateItems} from "./Dropdown.logic";


</script>

<Meta
        title="Components/Dropdown"
        argTypes={{
    caption: { control: "text" },
    onItemClicked: { action: "onItemClicked" },
    numberOfItems: { control: "number" }
  }}
/>

<Template let:args>
    <Dropdown caption={args.caption} items={generateItems(args.numberOfItems)} onItemClicked={args.onItemClick}/>
</Template>

<Story
        name="_2 Einträge"
        args={{
            caption: "2 Einträge",
            numberOfItems: 2
        }}
/>

<Story
        name="_4 Einträge"
        args={{
            caption: "4 Einträge",
            numberOfItems: 4
        }}
/>