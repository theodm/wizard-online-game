<script lang="ts">
    import {Menu, MenuButton, MenuItem, MenuItems} from "@rgossiaux/svelte-headlessui";

    export let caption: string;
    export let items: Array<{
        label: string,
        key: string
    }>;

    export let onItemClicked: (key: string) => Promise<void>;
</script>

<Menu class="m-2">
    <MenuButton class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-sm px-4 py-2.5 text-center inline-flex items-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
        {caption} <svg class="ml-2 w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path></svg>
    </MenuButton>
    <MenuItems class="absolute my-2 z-10 w-44 text-base list-none bg-white rounded divide-y divide-gray-100 shadow dark:bg-gray-700">
        {#each items as item}
            <MenuItem class="block py-2 px-4 text-sm text-gray-700 hover:bg-gray-100 dark:hover:bg-gray-600 dark:text-gray-200 dark:hover:text-white" on:click={() => onItemClicked(item.key)}>{item.label}</MenuItem>
        {/each}
    </MenuItems>
</Menu>
