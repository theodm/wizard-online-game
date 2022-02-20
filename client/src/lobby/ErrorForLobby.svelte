<script lang="ts">
    import BaseLayout from "../components/BaseLayout.svelte";
    import Logo from "../components/Logo.svelte";
    import NButton from "../components/NButton.svelte";
    import Alert from "../homepage/components/Alert.svelte";

    export let currentError: {
        message: string,
        timestamp: Date
    }
    export let onBackToHomepage: () => Promise<void>;
</script>

<BaseLayout>
    <Logo/>
    <Alert error={currentError}/>
    <div>
        <p class="text-sm mt-2">Es gab einen Fehler beim Laden dieser Lobby. Das ist schade. Vielleicht finden Sie
            jedoch auf der Startseite ein anderes Spiel, welches fehlerfrei läuft.</p>
    </div>

    <div class="w-full flex justify-center pt-6">
        <NButton type="button"
                 onClick={onBackToHomepage}
        >
            Zurück
        </NButton>
    </div>


</BaseLayout>