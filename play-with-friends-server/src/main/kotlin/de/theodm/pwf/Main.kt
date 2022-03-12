package de.theodm.pwf

import com.google.inject.*
import de.theodm.*
import de.theodm.lobby.Lobby
import de.theodm.lobby.storage.InMemoryLobbyStorage
import de.theodm.pwf.routing.AuthHandler
import de.theodm.pwf.routing.SessionDoesNotExistException
import de.theodm.lobby.storage.LobbyStorage
import de.theodm.pwf.routing.lobby.LobbyEndpoints
import de.theodm.pwf.routing.lobby.LobbyParticipant
import de.theodm.pwf.routing.lobby.toRsLobby
import de.theodm.pwf.routing.lobby.toRsShortLobbyInfo
import de.theodm.pwf.routing.model.*
import de.theodm.pwf.routing.model.bot.RsGenome
import de.theodm.pwf.routing.model.bot.RsTestFFNRequest
import de.theodm.pwf.routing.model.wizard.*
import de.theodm.pwf.routing.user.UserEndpoints
import de.theodm.pwf.routing.wizard.WizardEndpoints
import de.theodm.pwf.user.User
import de.theodm.pwf.user.UserManager
import de.theodm.pwf.utils.repeatLastValueDuringSilence
import de.theodm.wizard.*
import de.theodm.wizard.game.card.TrumpColor
import de.theodm.wizard.game.players.WizardPlayer
import io.javalin.Javalin
import io.javalin.http.staticfiles.Location
import io.javalin.plugin.json.JavalinJackson
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.dsl.document
import io.javalin.plugin.openapi.dsl.documented
import io.javalin.plugin.openapi.ui.ReDocOptions
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsContext
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

private val log = KotlinLogging.logger { }

class AppInjector : AbstractModule() {
    @Provides
    @Singleton
    fun provideLobbyService(
        lobbyStorage: LobbyStorage,
        wizardService: WizardService
    ): LobbyService {
        return LobbyService(
            maxLobbies = 1000,
            lobbyStorage = lobbyStorage,
            game = object : Game {
                override fun isGameStartAllowed(lobby: Lobby) = lobby.participants.size >= 3

                override fun startGame(lobby: Lobby) =
                    wizardService.startGame(lobby.id, lobby.participants as List<WizardPlayer>)

                override fun gameFinishedStream(lobby: Lobby) =
                    wizardService.lobbyFinishedStream(lobby.id)
            }
        )
    }

    override fun configure() {
        bind(AuthHandler::class.java).asEagerSingleton()

        // Benutzerverwaltung
        bind(UserManager::class.java).asEagerSingleton()
        bind(UserEndpoints::class.java).asEagerSingleton()

        // Lobbyverwaltung
        bind(LobbyStorage::class.java).to(InMemoryLobbyStorage::class.java)
        bind(LobbyEndpoints::class.java).asEagerSingleton()

        // Wizard
        bind(WizardStorage::class.java).to(InMemoryWizardStore::class.java)
        bind(WizardEndpoints::class.java).asEagerSingleton()
        bind(WizardService::class.java).asEagerSingleton()

        bind(AppMain::class.java).asEagerSingleton()
    }
}

class InLobbyWebSocket @Inject constructor(
    private val lobbyService: LobbyService,
    private val wizardService: WizardService
) {
    data class UserInLobby(
        val user: User,
        val lobbyID: String
    )

    private val sessions = ConcurrentHashMap<WsContext, UserInLobby>()

    fun onConnect(wsContext: WsContext, authUser: User, lobbyCode: String) {
        log.debug { "$authUser connected to lobby $lobbyCode." }

        sessions[wsContext] = UserInLobby(authUser, lobbyCode)

        lobbyService.updateConnectivity(
            caller = LobbyParticipant.of(authUser),
            lobbyID = lobbyCode,
            newConnectivity = Connectivity.Active
        )

        lobbyService
            .lobbyStream(lobbyCode)
            .map { lobby -> WsLobbyUpdated(lobby.toRsLobby()) }
            .repeatLastValueDuringSilence()
            .subscribe ({ lobbyUpdate ->
                log.debug { "Send lobby update to ${authUser.userName} with data $lobbyUpdate" }

                wsContext.send(lobbyUpdate)
            }, {}, {
                log.debug { "Send lobby update to ${authUser.userName} with data null" }

                wsContext.send(WsLobbyUpdated(null))
            })

        wizardService
            .wizardStream(lobbyCode)
            .map { WsWizardUpdated(it.getRsWizardGameStateForPlayer(LobbyParticipant.of(authUser))) }
            .repeatLastValueDuringSilence()
            .subscribe { wizardUpdate ->
                log.debug { "Send wizard update to ${authUser.userName} with data ${wizardUpdate}" }
                wsContext.send(wizardUpdate)
            }
    }


//
//    fun onMessage(wsContext: WsMessageContext) {
//        val (user, lobbyID) = sessions[wsContext]!!
//
//        when (val message = wsContext.messageAsClass<WsLobbyRequest>()) {
//            is WsUpdateConnectivity -> {
//                lobbyService.updateConnectivity(
//                    LobbyParticipant.of(user),
//                    lobbyID,
//                    message.newConnectivity.toConnectivity()
//                )
//            }
//        }
//    }

    fun onClose(wsContext: WsCloseContext) {
        val (user, lobbyID) = sessions[wsContext]!!

        sessions.remove(wsContext)

        lobbyService.updateConnectivity(LobbyParticipant.of(user), lobbyID, Connectivity.Disconnected)
    }
}


class AppMain @Inject constructor(
    private val lobbyService: LobbyService,
    private val userEndpoints: UserEndpoints,
    private val lobbyEndpoints: LobbyEndpoints,
    private val authHandler: AuthHandler,
    private val inLobbyWebSocket: InLobbyWebSocket,
    private val wizardEndpoints: WizardEndpoints
) {

    fun start(port: Int) {
        val openApiOptions = OpenApiOptions(
            Info()
                .title("Play with friends")
                .version("1.0")
                .description("Play with friends")
                .contact(
                    Contact()
                        .name("Theodor Diesner-Mayer")
                        .email("theo.dm94@gmail.com")
                )
        )
            .path("/swagger-docs")
            .activateAnnotationScanningFor("de.theodm.pwf")
            .swagger(SwaggerOptions("/swagger").title("Swagger Documentation")) // Activate the swagger ui
            .reDoc(ReDocOptions("/redoc").title("ReDoc Documentation")) // Active the ReDoc UI


        val app = Javalin
            .create { config ->
                config.registerPlugin(OpenApiPlugin(openApiOptions))
                config.requestLogger { ctx, executionTimeMs ->
                    if (ctx.header("Authorization") != null) {
                        try {
                            val authUser = authHandler.handleRoute(ctx)

                            log.info {
                                """${authUser.userName}: ${
                                    ctx.method().toUpperCase()
                                } ${ctx.path()} | Took ${executionTimeMs}ms"""
                            }
                        } catch (ex: Exception) {
                            log.info {
                                """User could not be determined: ${
                                    ctx.method().toUpperCase()
                                } ${ctx.path()} | Took ${executionTimeMs}ms"""
                            }
                        }
                    } else {
                        log.info {
                            """Not logged in: ${
                                ctx.method().toUpperCase()
                            } ${ctx.path()} | Took ${executionTimeMs}ms"""
                        }
                    }
                }
                config.enableCorsForAllOrigins()
                config.jsonMapper(JavalinJackson(objectMapper))
                config.addStaticFiles { staticFiles ->
                    staticFiles.hostedPath = "/"                    // change to host files on a subpath, like '/assets'
                    staticFiles.directory = "/public"               // the directory where your files are located
                    staticFiles.location =
                        Location.CLASSPATH       // Location.CLASSPATH (jar) or Location.EXTERNAL (file system)
                    staticFiles.precompress =
                        false                 // if the files should be pre-compressed and cached in memory (optimization)
                    staticFiles.aliasCheck =
                        null                   // you can configure this to enable symlinks (= ContextHandler.ApproveAliases())
                    staticFiles.headers = mapOf()                // headers that will be set for the files
                }
                config.addSinglePageRoot("/", "/public/index.html")
            }
            .start(port)

        app.routes {

            app.exception(Exception::class.java) { exception, ctx ->
                log.underlyingLogger.error("error: ", exception)

                ctx.json(exception.toRsException())
                ctx.status(500)
            }

            app.get("/api/dummy", documented(
                document()
                    .result("200", WsLobbyRequest::class.java)
                    .result("200", WsLobbyResponse::class.java)
                    .result("200", RsWizardGameStateForPlayer::class.java)
                    .result("200", RsLobbiesResponse::class.java)
                    .result("500", RsException::class.java)
                    .result("200", RsUserInfoResponse::class.java)
            ) {

            })

            app.post("/api/lobby", documented(document()
                .operation {
                    it.description("Erstellt eine neue Lobby.")
                    it.operationId("createLobby")
                }
                .header<String>("Authorization")
                .result("200", CreateLobbyResponse::class.java)) {

                it.json(lobbyEndpoints.createLobby(authHandler.handleRoute(it)))
            })

            app.sse("/api/user/sse/{userPrivateKey}") { sseClient ->
                val privateKey = sseClient.ctx.pathParam("userPrivateKey")
                val authUser = authHandler
                    .handlePrivateKey(privateKey)

                lobbyService
                    .lobbiesStream()
                    .map { lobbies -> lobbies.filter { it.participants.contains(LobbyParticipant.of(authUser)) } }
                    .map { lobbies -> RsUserInfoResponse(lobbies.singleOrNull()?.toRsLobby()) }
                    .repeatLastValueDuringSilence()
                    .subscribe { userInfoResponse ->
                        log.debug { "Aktualisierung an Benutzer ${authUser.userName} gesendet." }
                        sseClient.sendEvent(userInfoResponse)
                    }

            }

            app.sse("/api/lobby/sse") { sseClient ->
                lobbyService
                    .lobbiesOverviewStream()
                    .map { lobbies -> lobbies.map { it.toRsShortLobbyInfo() } }
                    .repeatLastValueDuringSilence()
                    .subscribe { lobbies ->
                        sseClient.sendEvent(RsLobbiesResponse(lobbies))
                    }
            }
//
//            app.post("/api/testFFN") {
//                val rq = it.bodyAsClass<RsTestFFNRequest>()
//
//                val ffNetwork = createFFNetwork(
//                    rq.genome.toGenome(),
//                    rq.genome.inputNodeKeys.toSet(),
//                    rq.genome.outputNodeKeys.toSet()
//                )
//
//                it.json(ffNetwork.activate(rq.inputs))
//            }
//
//            app.post("/api/FFNFirstRound") {
//                val genome = it.bodyAsClass<RsGenome>()
//
//                it.json(evalGameFirstRound(genome))
//            }

            //
//            app.get("/api/lobby", documented(document()
//                .operation {
//                    it.description("Gibt die aktuell laufenden Lobbies zurück.")
//                    it.operationId("getLobbies")
//                }
//                .result("200", RsLobbiesResponse::class.java)) {
//
//                it.json(lobbyEndpoints.getLobbies())
//            })

            app.post("/api/lobby/{lobbyID}/updateConnectivity", documented(
                document()
                    .operation {
                        it.description("Aktualisiert den Status des aktuell eingeloggten Benutzers.")
                        it.operationId("updateConnectivity")
                    }
                    .header<String>("Authorization")
                    .pathParam<String>("lobbyID")
                    .body<RqUpdateConnectivity>()
                    .result("200", Unit::class.java)
            ) {
                lobbyEndpoints.updateConnectivity(
                    authHandler.handleRoute(it),
                    it.pathParam("lobbyID"),
                    it.bodyAsClass()
                )
            })

            app.post("/api/lobby/{lobbyID}/addBot", documented(
                document()
                    .operation {
                        it.description("Fügt einen Bot zur Lobby hinzu.")
                        it.operationId("addBotToLobby")
                    }
                    .header<String>("Authorization")
                    .queryParam<String>("botType")
                    .pathParam<String>("lobbyID")
                    .result("200", Unit::class.java)
            ) {
                lobbyEndpoints.addBot(
                    authHandler.handleRoute(it),
                    it.pathParam("lobbyID"),
                    it.queryParam("botType")!!
                )
            })

            app.post("/api/lobby/{lobbyID}/updateSettings", documented(
                document()
                    .operation {
                        it.description("Aktualisiert den Status des aktuell eingeloggten Benutzers.")
                        it.operationId("updateSettings")
                    }
                    .header<String>("Authorization")
                    .pathParam<String>("lobbyID")
                    .body<RsLobbySettings>()
                    .result("200", Unit::class.java)
            ) {
                lobbyEndpoints.updateLobbySettings(
                    authHandler.handleRoute(it),
                    it.pathParam("lobbyID"),
                    it.bodyAsClass()
                )
            })

            app.post("/api/lobby/{lobbyID}/leaveLobby", documented(
                document()
                    .operation {
                        it.description("Die Lobby verlassen.")
                        it.operationId("leaveLobby")
                    }
                    .header<String>("Authorization")
                    .pathParam<String>("lobbyID")
                    .result("200", Unit::class.java)
            ) {
                lobbyEndpoints.leaveLobby(authHandler.handleRoute(it), it.pathParam("lobbyID"))
            })

            app.post("/api/lobby/{lobbyID}/startGame", documented(
                document()
                    .operation {
                        it.description("Startet das Spiel.")
                        it.operationId("startGame")
                    }
                    .header<String>("Authorization")
                    .pathParam<String>("lobbyID")
                    .result("200", Unit::class.java)
            ) {
                lobbyEndpoints.startGame(authHandler.handleRoute(it), it.pathParam("lobbyID"))
            })

            app.post("/api/lobby/{lobbyID}/updatePlayerOrder", documented(
                document()
                    .operation {
                        it.description("Aktualisiert die Reihenfolge der Benutzer.")
                        it.operationId("updatePlayerOrder")
                    }
                    .header<String>("Authorization")
                    .pathParam<String>("lobbyID")
                    .body<RqUpdatePlayerOrder>()
                    .result("200", Unit::class.java)
            ) {
                lobbyEndpoints.updatePlayerOrder(
                    authHandler.handleRoute(it),
                    it.pathParam("lobbyID"),
                    it.bodyAsClass()
                )
            })

            app.post("/api/lobby/{lobbyID}/wizard/playSingleCard", documented(
                document()
                    .operation {
                        it.description("Spielt die letzte Karte des Spielers.")
                        it.operationId("playSingleCard")
                    }
                    .header<String>("Authorization")
                    .pathParam<String>("lobbyID")
                    .result("200", Unit::class.java)
            ) {
                wizardEndpoints.playSingleCard(
                    authUser = authHandler.handleRoute(it),
                    lobbyCode = it.pathParam("lobbyID")
                )
            })

            app.post("/api/lobby/{lobbyID}/wizard/playCard", documented(
                document()
                    .operation {
                        it.description("Spielt eine Karte des Spielers.")
                        it.operationId("playCard")
                    }
                    .body<RsWizardCard>()
                    .header<String>("Authorization")
                    .pathParam<String>("lobbyID")
                    .result("200", Unit::class.java)
            ) {
                wizardEndpoints.playCard(
                    authUser = authHandler.handleRoute(it),
                    lobbyCode = it.pathParam("lobbyID"),
                    wizardCard = it.bodyAsClass(RsWizardCard::class.java)
                )
            })

            app.post("/api/lobby/{lobbyID}/wizard/placeBet", documented(
                document()
                    .operation {
                        it.description("Spieler gibt seinen Tipp ab.")
                        it.operationId("placeBet")
                    }
                    .header<String>("Authorization")
                    .queryParam("bet", Int::class.java)
                    .pathParam<String>("lobbyID")
                    .result("200", Unit::class.java)
            ) {
                wizardEndpoints.placeBet(
                    authUser = authHandler.handleRoute(it),
                    lobbyCode = it.pathParam("lobbyID"),
                    bet = it.queryParam("bet")!!.toInt()
                )
            })

            app.post("/api/lobby/{lobbyID}/wizard/selectTrumpColor", documented(
                document()
                    .operation {
                        it.description("Spieler wählt die Trumpffarbe aus.")
                        it.operationId("selectTrumpColor")
                    }
                    .header<String>("Authorization")
                    .queryParam("trumpColor", TrumpColor::class.java)
                    .pathParam<String>("lobbyID")
                    .result("200", Unit::class.java)
            ) {
                wizardEndpoints.selectTrumpColor(
                    authUser = authHandler.handleRoute(it),
                    lobbyCode = it.pathParam("lobbyID"),
                    trumpColor = TrumpColor.valueOf(it.queryParam("trumpColor")!!)
                )
            })

            app.post("/api/lobby/{lobbyID}/wizard/finishRound", documented(
                document()
                    .operation {
                        it.description("Spieler beendet die Runde.")
                        it.operationId("finishRound")
                    }
                    .header<String>("Authorization")
                    .pathParam<String>("lobbyID")
                    .result("200", Unit::class.java)
            ) {
                wizardEndpoints.finishRound(
                    authUser = authHandler.handleRoute(it),
                    lobbyCode = it.pathParam("lobbyID")
                )
            })

            app.ws("/api/lobby/{lobbyCode}") { ws ->
                ws.onConnect {
                    val user = authHandler.handleWebsocket(it)

                    inLobbyWebSocket.onConnect(it, user, it.pathParam("lobbyCode"))
                }

                ws.onMessage {
                    //inLobbyWebSocket.onMessage(it)
                }

                ws.onClose {
                    inLobbyWebSocket.onClose(it)
                }
            }

//            app.get("/api/lobby/{lobbyCode}", documented(document()
//                .operation {
//                    it.description("Gibt die Informationen zur Lobby zurück, wenn man Teilnehmer dieser Lobby ist.")
//                    it.operationId("getLobbyIfParticipant")
//                }
//                .header<String>("Authorization")
//                .pathParam<String>("lobbyCode")
//                .result("200", RsLobby::class.java)) {
//
//                it.json(lobbyEndpoints.getLobby(it.attribute<User>("authUser")!!, it.pathParam("lobbyCode")))
//            })

            app.post("/api/lobby/joinLobby", documented(document()
                .operation {
                    it.description("Tritt einer Lobby bei...")
                    it.operationId("joinLobby")
                }
                .header<String>("Authorization")
                .queryParam<String>("lobbyID")
                .result("200", Unit::class.java)
            ) {
                lobbyEndpoints.joinLobby(authHandler.handleRoute(it), it.queryParam("lobbyID")!!)
            })

            app.get("/api/user", documented(document()
                .operation {
                    it.description("Gibt an ob es für den übergebenen Session-Key eine gültige Session gibt.")
                    it.operationId("getLoggedInUserInformation")
                }
                .header<String>("Authorization")
                .result("200", RsUser::class.java)
                .result("404", Unit::class.java)
            ) {
                try {
                    val user = authHandler.handleRoute(it)

                    it.json(user.toRsUser())
                } catch (ex: SessionDoesNotExistException) {
                    it.status(404)
                    it.json(false)
                }
            })

            app.post("/api/user/createUser", documented(document()
                .operation {
                    it.description("Erstellt einen neuen Benutzer.")
                    it.operationId("createUser")
                }
                .queryParam<String>("userName")
                .result("200", RsUser::class.java)
            ) {
                it.json(userEndpoints.createUser(it.queryParam("userName")!!))
            })

            app.post("/api/user/changeName", documented(
                document()
                    .operation {
                        it.description("Ändert den Namen des akutell eingeloggten Benutzers.")
                        it.operationId("changeName")
                    }
                    .queryParam<String>("userName")
                    .header<String>("Authorization")
                    .result("200", Unit::class.java)
            ) {
                userEndpoints.changeName(authHandler.handleRoute(it), it.queryParam("userName")!!)
            })
        }
    }
}

fun main(args: Array<String>) {
    val injector: Injector = Guice.createInjector(AppInjector())

    val app: AppMain = injector
        .getInstance(AppMain::class.java)

    app.start((System.getenv("PORT") ?: "8088").toInt())
}
