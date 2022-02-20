import {assertNotFalsy} from "../utils/Assertions.js";
const _PlayerStore = class {
  constructor(getLoggedInUserInfo, changeUserNameAPI, createUserAPI, currentData) {
    this.getLoggedInUserInfo = getLoggedInUserInfo;
    this.changeUserNameAPI = changeUserNameAPI;
    this.createUserAPI = createUserAPI;
    this.currentData = currentData;
  }
  updateCurrentData(newData) {
    localStorage.setItem(_PlayerStore.storageKey, JSON.stringify(newData));
    this.currentData = newData;
    console.log("Lokale Session-Informationen aktualisiert: ", newData);
  }
  isLoggedIn() {
    return !!this.currentData;
  }
  getPrivateKey() {
    assertNotFalsy(this.currentData, "Die Methode getPrivateKey wurde aufgerufen; es existieren jedoch keine Session-Informationen.");
    return this.currentData?.userKey;
  }
  getPublicID() {
    assertNotFalsy(this.currentData, "Die Methode getPublicID wurde aufgerufen; es existieren jedoch keine Session-Informationen.");
    return this.currentData?.userPublicID;
  }
  getUserName() {
    assertNotFalsy(this.currentData, "Die Methode getUserName wurde aufgerufen; es existieren jedoch keine Session-Informationen.");
    return this.currentData?.userName;
  }
  async getOrCreateUserForName(userName) {
    console.log("Erstelle oder ändere Session mit Benutzername: " + userName);
    if (!this.isLoggedIn()) {
      console.log("Es existiert noch keine Session für den lokalen Benutzer.");
      const user = await this.createUserAPI(userName);
      console.log("Session erstellt: ", user);
      this.updateCurrentData(user);
    }
    assertNotFalsy(this.currentData);
    if (userName !== this.currentData.userName) {
      console.log("Benutzername des lokalen Benutzers muss aktualisiert werden: ");
      console.log("War: ", this.currentData.userName, " Wird: ", userName);
      await this.changeUserNameAPI(this.currentData.userKey, userName);
      this.updateCurrentData({...this.currentData, userName});
    }
    return this.currentData;
  }
  static async loadFromWebStorage(getLoggedInUserInfo, changeUserNameAPI, createUserAPI) {
    const currentData = localStorage.getItem(this.storageKey);
    console.log("Lokale Session-Informationen: ", currentData);
    if (!currentData) {
      console.log("Keine lokalen Session-Informationen gefunden.");
      return new _PlayerStore(getLoggedInUserInfo, changeUserNameAPI, createUserAPI);
    }
    let currentDataParsed;
    try {
      currentDataParsed = JSON.parse(currentData);
    } catch (e) {
      console.log("Lokale Session-Informationen konnten nicht deserialisiert werden. Setze lokale Session-Informationen zürck.");
      console.log(e);
      return new _PlayerStore(getLoggedInUserInfo, changeUserNameAPI, createUserAPI);
    }
    console.log("Lokale Session-Informationen (deserialisiert): ", currentDataParsed);
    console.log("Frage korrespondierende Session-Informationen des Servers an.");
    try {
      const sessionInfo = await getLoggedInUserInfo(currentDataParsed.userKey);
      if (!sessionInfo) {
        console.log("Session-Informationen auf dem Server nicht mehr vorhanden. Setze lokale Session-Informationen zurück.");
        return new _PlayerStore(getLoggedInUserInfo, changeUserNameAPI, createUserAPI);
      }
      return new _PlayerStore(getLoggedInUserInfo, changeUserNameAPI, createUserAPI, sessionInfo);
    } catch (e) {
      console.log("Session-Informationen auf dem Server konnten nicht ermittelt werden. Setze alte Session-Informationen zurück.");
      console.log(e);
      return new _PlayerStore(getLoggedInUserInfo, changeUserNameAPI, createUserAPI);
    }
  }
};
export let PlayerStore = _PlayerStore;
PlayerStore.storageKey = "playerSessionData";
