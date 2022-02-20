<script lang="typescript">
    import type {
        RsFinishedWizardRound,
        RsNull,
        RsNumberColorCard,
        RsRoundStateForPlayerFirstRound,
        RsRoundStateForPlayerNormalRound,
        RsWizardCard,
    } from "../generated/client";

    import {onMount} from "svelte";

    import WizardUI from "./WizardUI.svelte";

    async function delay(ms: number) {
        return new Promise((resolve) => {
            setTimeout(resolve, ms);
        });
    }

    let wizardState: any = undefined;

    onMount(async () => {
        wizardState = (await import("./GameDemoFiles/1.json")).default;

        console.log(wizardState)
    })

    async function onSelectTrumpColor(trumpColor: any) {
        wizardState = (await import("./GameDemoFiles/2.json")).default
    }

    async function onBetPlaced(bet: number): Promise<void> {
        wizardState = (await import("./GameDemoFiles/3.json")).default

        await delay(2000);
        wizardState = (await import("./GameDemoFiles/4.json")).default

        await delay(2000);
        wizardState = (await import("./GameDemoFiles/5.json")).default
    }

    async function onCardPlayed(card: RsWizardCard | null): Promise<void> {
        wizardState = (await import("./GameDemoFiles/6.json")).default

        await delay(2000)
        wizardState = (await import("./GameDemoFiles/7.json")).default

        await delay(2000)
        wizardState = (await import("./GameDemoFiles/8.json")).default
    }

    let zweiteRunde = false;

    async function onNextRoundStarted(): Promise<void> {
        wizardState = (await import("./GameDemoFiles/9.json")).default
        zweiteRunde = true;

        await delay(2000)
        wizardState = (await import("./GameDemoFiles/10.json")).default

        await delay(2000)
        wizardState = (await import("./GameDemoFiles/11.json")).default

        await delay(2000)
        wizardState = (await import("./GameDemoFiles/12.json")).default
    }

    async function onSelectTrumpColorR2(trumpColor: any) {
    }

    async function onBetPlacedR2(bet: number) {
        wizardState = (await import("./GameDemoFiles/13.json")).default;

        await delay(2000)
        wizardState = (await import("./GameDemoFiles/14.json")).default

        await delay(2000)
        wizardState = (await import("./GameDemoFiles/15.json")).default
    }

    async function onCardPlayedR2(card: RsWizardCard | null) {
        wizardState = (await import("./GameDemoFiles/16.json")).default;

        await delay(2000)
        wizardState = (await import("./GameDemoFiles/17.json")).default

        await delay(2000)
        wizardState = (await import("./GameDemoFiles/18.json")).default

        await delay(2000)
        wizardState = (await import("./GameDemoFiles/19.json")).default
    }

    async function onNextRoundStartedR2() {
    }
</script>

{#if wizardState}
    {#if !zweiteRunde}
        <WizardUI
                {wizardState}
                localPlayerPublicID={"Sebastian123"}
                onPlaceBet={onBetPlaced}
                {onNextRoundStarted}
                {onCardPlayed}
                onSelectTrumpColor={onSelectTrumpColor}
        />
    {:else}
        <WizardUI
                {wizardState}
                localPlayerPublicID={"Sebastian123"}
                onPlaceBet={onBetPlacedR2}
                onNextRoundStarted={onNextRoundStartedR2}
                onCardPlayed={onCardPlayedR2}
                onSelectTrumpColor={onSelectTrumpColorR2}
        />
    {/if}
{/if}