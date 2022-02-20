import {ApiClient} from "../client/ApiClient";
import {RsLobby, RsWizardCard, RsWizardGameStateForPlayer} from "../generated/client";
import {LobbySocketStatus} from "../lobby/LobbySocket";

export class WizardSocket {

    constructor(
        private readonly apiClient: ApiClient,
        private readonly sessionKey: string,
        private readonly lobbyCode: string,
    ) {

    }

    async selectTrumpColor(trumpColor: "Red" | "Blue" | "Yellow" | "Green") {
        await this.apiClient.wizardSelectTrumpColor(this.sessionKey, this.lobbyCode, trumpColor);
    }

    async playCard(wizardCard: RsWizardCard) {
        await this.apiClient.wizardPlayCard(this.sessionKey, this.lobbyCode, wizardCard);
    }

    async playSingleCard() {
        await this.apiClient.wizardPlaySingleCard(this.sessionKey, this.lobbyCode);
    }

    async placeBet(bet: number) {
        await this.apiClient.wizardPlaceBet(this.sessionKey, this.lobbyCode, bet);
    }

    async finishRound() {
        await this.apiClient.wizardFinishRound(this.sessionKey, this.lobbyCode);
    }

}