export class WizardSocket {
  constructor(apiClient, sessionKey, lobbyCode) {
    this.apiClient = apiClient;
    this.sessionKey = sessionKey;
    this.lobbyCode = lobbyCode;
  }
  async selectTrumpColor(trumpColor) {
    await this.apiClient.wizardSelectTrumpColor(this.sessionKey, this.lobbyCode, trumpColor);
  }
  async playCard(wizardCard) {
    await this.apiClient.wizardPlayCard(this.sessionKey, this.lobbyCode, wizardCard);
  }
  async playSingleCard() {
    await this.apiClient.wizardPlaySingleCard(this.sessionKey, this.lobbyCode);
  }
  async placeBet(bet) {
    await this.apiClient.wizardPlaceBet(this.sessionKey, this.lobbyCode, bet);
  }
  async finishRound() {
    await this.apiClient.wizardFinishRound(this.sessionKey, this.lobbyCode);
  }
}
