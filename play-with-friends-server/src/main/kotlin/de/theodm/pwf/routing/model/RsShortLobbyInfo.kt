/**
* play-with-friends
* No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
*
* The version of the OpenAPI document: 1.0
* 
*
* NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
* https://openapi-generator.tech
* Do not edit the class manually.
*/
package de.theodm.pwf.routing.model

/**
 * 
 * @param lobbyID 
 * @param players 
 * @param isInGame 
 */
data class RsShortLobbyInfo(
    val lobbyID: kotlin.String,
    val players: kotlin.collections.List<RsParticipant>,
    val isInGame: kotlin.Boolean
) 

