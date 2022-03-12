package de.theodm.pwf.routing.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import de.theodm.pwf.routing.model.wizard.RsWizardGameStateForPlayer

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = WsLobbyUpdated::class, name = "WsLobbyUpdated"),
    JsonSubTypes.Type(value = WsWizardUpdated::class, name = "WsWizardUpdated")
)
sealed class WsLobbyResponse

data class WsLobbyUpdated(
    val newLobby: RsLobby?
): WsLobbyResponse()

data class WsWizardUpdated(
    val newWizardState: RsWizardGameStateForPlayer
): WsLobbyResponse()
