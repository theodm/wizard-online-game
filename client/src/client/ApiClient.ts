import.meta.hot;
import WebSocket, {CloseEvent, ErrorEvent, MessageEvent, OpenEvent,} from "isomorphic-ws";
import {
    Configuration,
    DefaultApi,
    ResponseContext,
    RsLobbiesResponseFromJSON,
    RsLobbySettings,
    RsShortLobbyInfo, RsUserInfoResponse, RsUserInfoResponseFromJSON,
    RsWizardCard,
    TrumpColorFromJSON,
    WsLobbyRequest,
    WsLobbyResponse,
} from "../generated/client";

export interface WWebSocket<TWebSocketRequest> {
    close(): void;

    send(message: TWebSocketRequest): void;
}

export interface WebSocketSession<TWebSocketRequest, TWebSocketResponse> {
    onOpen(webSocket: WWebSocket<TWebSocketRequest>): void;

    onError(webSocket: WWebSocket<TWebSocketRequest>): void;

    onClose(event: CloseEvent, webSocket: WWebSocket<TWebSocketRequest>): void;

    onMessage(
        webSocket: WWebSocket<TWebSocketRequest>,
        message: TWebSocketResponse
    ): void;
}

declare var __SNOWPACK_ENV__: any;

export class ApiClient {
    private readonly api: DefaultApi;

    static create(): ApiClient {
        if (__SNOWPACK_ENV__.MODE === 'development') {
            return new ApiClient("http://127.0.0.1:8088")
        }

        return new ApiClient("")
    }

    constructor(private readonly basePath: string) {
        console.log("basePath: ", basePath)
        this.api = new DefaultApi(
            new Configuration({
                basePath,
                middleware: [{
                    async post(context: ResponseContext): Promise<Response | void> {
                        if (context.response.status === 500) {
                            (context.response as any).errorData = await context.response.json()
                        }

                        return context.response;
                    }
                }]
            })
        );
    }

    async getLoggedInUserInformation(userPrivateID: string) {
        return await this.api.getLoggedInUserInformation({
            authorization: "Custom " + userPrivateID
        });
    }

    async createUser(userName: string) {
        return await this.api.createUser({
            userName: userName,
        });
    }

    async changeUserName(userPrivateID: string, userName: string) {
        return await this.api.changeName({
            userName: userName,
            authorization: "Custom " + userPrivateID,
        });
    }

    registerUserInfoUpdate(userPrivateID: string, onMessage: (userInfo: RsUserInfoResponse) => void): EventSource {
        const evtSource = new EventSource(this.basePath + "/api/user/sse/" + userPrivateID)

        evtSource.onmessage = function (event) {
            onMessage(RsUserInfoResponseFromJSON(JSON.parse(event.data)));
        }

        return evtSource
    }

    registerLobbiesUpdate(onMessage: (lobbies: Array<RsShortLobbyInfo>) => void): EventSource {
        const evtSource = new EventSource(this.basePath + "/api/lobby/sse")

        evtSource.onmessage = function (event) {
            onMessage(RsLobbiesResponseFromJSON(JSON.parse(event.data)).lobbies)
        }

        return evtSource
    }


    webSocket<TWebSocketRequest, TWebSocketResponse>(
        webSocketSession: WebSocketSession<TWebSocketRequest, TWebSocketResponse>,
        url: string
    ) {
        if (this.basePath.startsWith("http")) {
            url = this.basePath
                .replace("http", "ws")
                .replace("https", "wss") + url;
        } else {
            const turl = new URL(url, window.location.href);

            turl.protocol = turl.protocol.replace('http', 'ws').replace('https', "wss");

            url = turl.href;
        }

        console.log(`[Websocket] Connecting to ${url}`);

        const ws = new WebSocket(url);

        const wsObject: WWebSocket<TWebSocketRequest> = {
            send(message: TWebSocketRequest) {
                ws.send(JSON.stringify(message));
            },
            close() {
                ws.close();
            },
        };

        ws.onopen = (event: OpenEvent) => {
            console.log(`[Websocket] Connected to \${url}`);

            webSocketSession.onOpen(wsObject);
        };
        ws.onclose = (event: CloseEvent) => {
            console.log(`[Websocket] Connection to ${url} closed`);
            console.log(`[Websocket] Code: ${event.code}`);
            console.log(`[Websocket] Reason: ${event.reason}`);
            console.log(`[Websocket] Was clean: ${event.wasClean}`);

            webSocketSession.onClose(event, wsObject);
        };
        ws.onmessage = (event: MessageEvent) => {
            console.log(`[Websocket] Received message from ${url}`);
            console.log(`[Websocket] Type: ${event.type}.`);
            console.log(`[Websocket] Message: ${event.data}.`);

            webSocketSession.onMessage(wsObject, JSON.parse(event.data.toString()));
        };
        ws.onerror = (event: ErrorEvent) => {
            console.log(`[Websocket] Received error from ${url}.`);
            console.log(`[Websocket] Error: ${event.error}.`);
            console.log(`[Websocket] Type: ${event.type}.`);
            console.log(`[Websocket] Message: ${event.message}.`);

            webSocketSession.onError(wsObject);
        };
        return wsObject;
    }

    lobbyUpdates(
        webSocketSession: WebSocketSession<WsLobbyRequest, WsLobbyResponse>,
        lobbyCode: string,
        sessionKey: string
    ) {
        return this.webSocket(
            webSocketSession,
            `/api/lobby/${lobbyCode}?Authorization=Custom ${sessionKey}`
        );
    }

    async startGame(sessionKey: string, lobbyCode: string) {
        return this.api.startGame({
            authorization: "Custom " + sessionKey,
            lobbyID: lobbyCode,
        });
    }

    async leaveLobby(sessionKey: string, lobbyCode: string) {
        return this.api.leaveLobby({
            authorization: "Custom " + sessionKey,
            lobbyID: lobbyCode,
        });
    }

    async updateSettings(
        sessionKey: string,
        lobbyCode: string,
        newSettings: RsLobbySettings
    ) {
        return this.api.updateSettings({
            authorization: "Custom " + sessionKey,
            lobbyID: lobbyCode,
            rsLobbySettings: newSettings,
        });
    }

    async updatePlayerOrder(
        sessionKey: string,
        lobbyCode: string,
        newPlayerOrder: string[]
    ) {
        return this.api.updatePlayerOrder({
            authorization: "Custom " + sessionKey,
            lobbyID: lobbyCode,
            rqUpdatePlayerOrder: {newPlayerOrder: newPlayerOrder},
        });
    }

    async createLobby(sessionKey: string) {
        return await this.api.createLobby({
            authorization: "Custom " + sessionKey,
        });
    }

    async joinLobby(sessionKey: string, lobbyCode: string) {
        return await this.api.joinLobby({
            lobbyID: lobbyCode,
            authorization: "Custom " + sessionKey,
        });
    }

    async wizardSelectTrumpColor(sessionKey: string, lobbyCode: string, trumpColor: "Red" | "Yellow" | "Green" | "Blue") {
        return this.api.selectTrumpColor({
            trumpColor: TrumpColorFromJSON(trumpColor),
            lobbyID: lobbyCode,
            authorization: "Custom " + sessionKey
        })
    }

    async wizardPlayCard(sessionKey: string, lobbyCode: string, wizardCard: RsWizardCard) {
        return this.api.playCard({
            lobbyID: lobbyCode,
            rsWizardCard: wizardCard,
            authorization: "Custom " + sessionKey
        })
    }

    async wizardPlaySingleCard(sessionKey: string, lobbyCode: string) {
        return this.api.playSingleCard({
            lobbyID: lobbyCode,
            authorization: "Custom " + sessionKey
        })
    }

    async wizardPlaceBet(sessionKey: string, lobbyCode: string, bet: number) {
        return this.api.placeBet({
            bet: bet,
            lobbyID: lobbyCode,
            authorization: "Custom " + sessionKey
        })
    }

    async wizardFinishRound(sessionKey: string, lobbyCode: string) {
        return this.api.finishRound({
            lobbyID: lobbyCode,
            authorization: "Custom " + sessionKey
        })
    }
}
