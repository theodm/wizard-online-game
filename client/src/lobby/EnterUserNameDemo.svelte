<script lang="ts">
    import EnterUserName from "./EnterUserName.svelte";
</script>

<EnterUserName onBackToHomepage={() => alert("back clicked")}
               onSubmit={(userName) => alert("submit clicked: " + userName)}/>