import {ApiClient, WWebSocket} from "../client/ApiClient";
import {
    RsLobby,
    RsLobbySettings,
    RsWizardGameStateForPlayer,
    WsLobbyRequest,
    WsLobbyUpdated,
    WsWizardUpdated,
} from "../generated/client";
import {WizardSocket} from "../wizard/WizardSocket";
import {BehaviorSubject, ReplaySubject} from "rxjs";

export type LobbySocketStatus =
    | "Connecting"
    | "Connected"
    | "Error"
    | "Disconnected";

export class LobbySocket {
    private reconnectTimeoutID: any
    private pingIntervalID: any
    private websocket: WWebSocket<WsLobbyRequest> | null = null;

    private readonly wizardSubject: ReplaySubject<RsWizardGameStateForPlayer> = new ReplaySubject<RsWizardGameStateForPlayer>(1)

    wizardStream() {
        return this.wizardSubject.asObservable()
    }
    constructor(
        private apiClient: ApiClient,
        private sessionKey: string,
        private lobbyCode: string,
        private connectionStatusUpdated: (newStatus: LobbySocketStatus) => void,
        private lobbyUpdated: (lobby: RsLobby) => void
    ) {
        this.connect();
    }

    startGame() {
        return this.apiClient.startGame(this.sessionKey, this.lobbyCode);
    }

    leaveLobby() {
        return this.apiClient.leaveLobby(this.sessionKey, this.lobbyCode);
    }

    updateLobbySettings(newSettings: RsLobbySettings) {
        return this.apiClient.updateSettings(
            this.sessionKey,
            this.lobbyCode,
            newSettings
        );
    }

    async addBot(botType: string) {
        return await this.apiClient.addBot(
            this.sessionKey,
            this.lobbyCode,
            botType
        )
    }

    updatePlayerOrder(newPlayerOrder: string[]) {
        return this.apiClient.updatePlayerOrder(
            this.sessionKey,
            this.lobbyCode,
            newPlayerOrder
        );
    }

    private connect() {
        console.log("[LobbySocket] Connecting.");

        this.connectionStatusUpdated("Connecting");

        const that = this;

        this.websocket = this.apiClient.lobbyUpdates(
            {
                onMessage(webSocket, message) {
                    console.log("[LobbySocket] Message.");
                    if (message.type === "WsLobbyUpdated") {
                        const m = message as WsLobbyUpdated;

                        // ToDo: Was passiert, wenn die Lobby aufgelöst wird?
                        // Sollte eigentlich nicht passieren
                        that.lobbyUpdated(m.newLobby!);
                    } else if (message.type === "WsWizardUpdated") {
                        const m = message as WsWizardUpdated;

                        that.wizardSubject.next(m.newWizardState)
                    }
                },
                onClose(event, webSocket) {
                    console.log("[LobbySocket] Disconnected.");

                    that.connectionStatusUpdated("Disconnected");
                    clearInterval(that.pingIntervalID)

                    if (event.code !== 1006) {
                        that.reconnectTimeoutID = setTimeout(() => that.connect(), 1000);
                    }
                },
                onError(webSocket) {
                    console.log("[LobbySocket] Error.");

                    that.connectionStatusUpdated("Error");
                },
                onOpen(webSocket) {
                    console.log("[LobbySocket] Open.")

                    that.pingIntervalID = setInterval(() => {
                        webSocket.send({
                            type: "Ping"
                        })

                    }, 1000)

                    that.connectionStatusUpdated("Connected");
                },
            },
            this.lobbyCode,
            this.sessionKey
        );
    }

    wizard() {
        return new WizardSocket(this.apiClient, this.sessionKey, this.lobbyCode);
    }

    destroy() {
        clearTimeout(this.reconnectTimeoutID)
    }

}
