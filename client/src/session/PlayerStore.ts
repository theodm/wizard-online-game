import type {RsUser} from "../generated/client";
import {assertNotFalsy} from "../utils/Assertions";

export class PlayerStore {
    static readonly storageKey = "playerSessionData";

    // Aktueller Benutzer ist eingeloggt, wenn
    // currentData != undefined ist
    private currentData?: RsUser;

    private constructor(
        private readonly getLoggedInUserInfo: (
            privateKey: string
        ) => Promise<RsUser | undefined>,
        private readonly changeUserNameAPI: (
            privateKey: string,
            newUserName: string
        ) => Promise<void>,
        private readonly createUserAPI: (
            userName: string
        ) => Promise<RsUser>,
        currentData?: RsUser
    ) {
        this.currentData = currentData;
    }

    private updateCurrentData(newData?: RsUser) {
        localStorage.setItem(PlayerStore.storageKey, JSON.stringify(newData));
        this.currentData = newData;

        console.log("Lokale Session-Informationen aktualisiert: ", newData);
    }

    isLoggedIn() {
        return !!this.currentData;
    }

    getPrivateKey(): string {
        assertNotFalsy(this.currentData, "Die Methode getPrivateKey wurde aufgerufen; es existieren jedoch keine Session-Informationen.")

        return this.currentData?.userKey;
    }

    getPublicID(): string {
        assertNotFalsy(this.currentData, "Die Methode getPublicID wurde aufgerufen; es existieren jedoch keine Session-Informationen.")

        return this.currentData?.userPublicID;
    }

    getUserName(): string {
        assertNotFalsy(this.currentData, "Die Methode getUserName wurde aufgerufen; es existieren jedoch keine Session-Informationen.")

        return this.currentData?.userName;
    }

    async getOrCreateUserForName(userName: string): Promise<RsUser> {
        console.log("Erstelle oder ändere Session mit Benutzername: " + userName);

        // Der Benutzer ist noch nicht eingeloggt bzw. existiert
        // noch nicht? Dann erstellen wir einen neuen Benutzer!
        if (!this.isLoggedIn()) {
            console.log("Es existiert noch keine Session für den lokalen Benutzer.");

            const user = await this.createUserAPI(userName);

            console.log("Session erstellt: ", user);

            this.updateCurrentData(user);
        }

        assertNotFalsy(this.currentData);

        // Hat der Benutzer seinen Namen geändert?
        // Dann müssen wir den Namen ändern.

        if (userName !== this.currentData.userName) {
            console.log("Benutzername des lokalen Benutzers muss aktualisiert werden: ")
            console.log("War: ", this.currentData.userName, " Wird: ", userName)

            await this.changeUserNameAPI(this.currentData.userKey, userName);

            this.updateCurrentData({...this.currentData, userName: userName});
        }

        return this.currentData;
    }

    static async loadFromWebStorage(
        getLoggedInUserInfo: (
            privateKey: string
        ) => Promise<RsUser>,
        changeUserNameAPI: (
            privateKey: string,
            newUserName: string
        ) => Promise<void>,
        createUserAPI: (
            userName: string
        ) => Promise<RsUser>
    ) {
        const currentData = localStorage.getItem(this.storageKey);

        console.log("Lokale Session-Informationen: ", currentData)

        if (!currentData) {
            console.log("Keine lokalen Session-Informationen gefunden.")

            return new PlayerStore(getLoggedInUserInfo, changeUserNameAPI, createUserAPI);
        }

        let currentDataParsed: RsUser

        try {
            currentDataParsed = JSON.parse(currentData)
        } catch (e) {
            console.log("Lokale Session-Informationen konnten nicht deserialisiert werden. Setze lokale Session-Informationen zürck.")
            console.log(e);

            return new PlayerStore(getLoggedInUserInfo, changeUserNameAPI, createUserAPI);
        }

        console.log("Lokale Session-Informationen (deserialisiert): ", currentDataParsed);

        console.log("Frage korrespondierende Session-Informationen des Servers an.");
        try {
            const sessionInfo = await getLoggedInUserInfo(currentDataParsed.userKey);

            if (!sessionInfo) {
                // Session-Key ist auf dem Server nicht mehr vorhanden.
                console.log("Session-Informationen auf dem Server nicht mehr vorhanden. Setze lokale Session-Informationen zurück.")
                return new PlayerStore(getLoggedInUserInfo, changeUserNameAPI, createUserAPI);
            }

            return new PlayerStore(getLoggedInUserInfo, changeUserNameAPI, createUserAPI, sessionInfo);
        } catch (e) {
            // Aktuelle Session-Informationen konnten nicht ermittelt werden.
            console.log("Session-Informationen auf dem Server konnten nicht ermittelt werden. Setze alte Session-Informationen zurück.")
            console.log(e);

            return new PlayerStore(getLoggedInUserInfo, changeUserNameAPI, createUserAPI);
        }
    }
}
