<script lang="typescript">
    import type {RsParticipant} from "../../generated/client";

    import {dndzone} from "svelte-dnd-action/dist";

    export let onItemsChanged: (newItems: RsParticipant[]) => Promise<void>;
    export let items: RsParticipant[];
    export let host: RsParticipant;
    export let participantsConnectivityInfo: Record<string, "DISCONNECTED" | "ACTIVE" | "INACTIVE">;
    export let editable: boolean;

    let cachedItems: Array<RsParticipant & { id: string }>;

    function handleSort(e: any) {
        cachedItems = e.detail.items;
    }

    function handleFinish(e: any) {
        handleSort(e);

        (async () => {
            try {
                await onItemsChanged(e.detail.items);
            } catch (e) {
                cachedItems = items.map((it) => ({...it, id: it.userPublicID}));
            }
        })();
    }

    $: {
        cachedItems = items.map((it) => ({...it, id: it.userPublicID}));
    }
</script>

<div class="p-2 mt-4 text-sm text-gray-700 border border-b-0 rounded-t-md border-cool-gray-600 bg-gray-50">
    {cachedItems.length} / 6 Spieler
    <span class="text-gray-500">(3 Spieler erforderlich)</span>
</div>
<ul class="" use:dndzone={{ items: cachedItems, dragDisabled: !editable }} on:consider={handleSort}
    on:finalize={handleFinish}>
    {#each cachedItems as player, i (player.id)}
        <li
                class:rounded-b-md={i === cachedItems.length - 1}
                class:border-b-0={i !== cachedItems.length - 1}
                class:cursor-move={editable}
                class="p-2 text-gray-700 bg-white border border-cool-gray-600 hover:bg-blue-100"
        >
            <div class="flex h-full">
                <div class="flex items-center">
                    <div class="ml-1 grow mr-2 ">{player.userName}</div>
                    {#if player.userPublicID === host.userPublicID}
                        <div class="">👑</div>
                    {/if}
                </div>

                <div class="flex items-center justify-end w-full mr-1">
                    <div
                            class="w-4 h-4 rounded-full"
                            class:bg-green-500={participantsConnectivityInfo[player.userPublicID] === "ACTIVE"}
                            class:bg-yellow-500={participantsConnectivityInfo[player.userPublicID] === "INACTIVE"}
                            class:bg-red-500={participantsConnectivityInfo[player.userPublicID] === "DISCONNECTED"}
                    />
                </div>
            </div>
        </li>
    {/each}
</ul>
