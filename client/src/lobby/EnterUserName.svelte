<script lang="ts">
    import BaseLayout from "../components/BaseLayout.svelte";
    import Logo from "../components/Logo.svelte";
    import TextField from "../homepage/components/TextField.svelte";
    import {validateUserName} from "../client/UserNameValidation";
    import NButton from "../components/NButton.svelte";
    import Alert from "../homepage/components/Alert.svelte";

    export let onBackToHomepage: () => Promise<void>;
    export let onSubmit: (selectedUserName: string) => Promise<void>;
    export let currentError: {
        message: string,
        timestamp: Date
    }

    export let lobbyCode: string;

    let currentName = "Test";


    $: userNameValidates = validateUserName(currentName).validates
</script>


<BaseLayout>
    <Logo/>
    <div>
        <p class="text-sm mt-2">Sie sind dabei der Lobby mit dem Code {lobbyCode} beizutreten. Um mitspielen zu können, müssen Sie jedoch
            einen Namen auswählen, unter dem Sie spielen möchten.</p>
    </div>
    <div>
        <p class="flex justify-center py-2 pt-6">Name eingeben</p>
        <TextField disabled={false}
                   currentText={currentName}
                   onCurrentTextChanged={(newName) => currentName = newName}
                   placeHolder={"Dominik12"}
                   validationFunction={validateUserName}
        />
    </div>
    <Alert error={currentError}/>
    <div class="w-full flex justify-center p-2 pt-6 mt-2">
        <NButton type="button"
                 onClick={() => onBackToHomepage()}
                 cssClass="mr-2 text-xs w-auto shadow-sm"
                 outline={true}
        >
            Verlassen
        </NButton>
        <NButton type="button"
                 cssClass="ml-2"
                 disabled={!userNameValidates}
                 onClick={() => onSubmit(currentName)}
        >
            Beitreten
        </NButton>

    </div>



</BaseLayout>