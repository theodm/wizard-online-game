package de.theodm.pwf.routing.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = WsUpdateConnectivity::class, name = "WsUpdateConnectivity")
)
sealed class WsLobbyRequest

data class WsUpdateConnectivity(
    val type: String = "WsUpdateConnectivity",
    val newConnectivity: RsConnectivity?
): WsLobbyRequest()



