/* tslint:disable */
/* eslint-disable */
/**
 * Play with friends
 * Play with friends
 *
 * The version of the OpenAPI document: 1.0
 * Contact: theo.dm94@gmail.com
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { exists, mapValues } from '../runtime';
import {
    RsLobby,
    RsLobbyFromJSON,
    RsLobbyFromJSONTyped,
    RsLobbyToJSON,
} from './RsLobby';
import {
    WsLobbyResponse,
    WsLobbyResponseFromJSON,
    WsLobbyResponseFromJSONTyped,
    WsLobbyResponseToJSON,
} from './WsLobbyResponse';
import {
    WsLobbyUpdatedAllOf,
    WsLobbyUpdatedAllOfFromJSON,
    WsLobbyUpdatedAllOfFromJSONTyped,
    WsLobbyUpdatedAllOfToJSON,
} from './WsLobbyUpdatedAllOf';

/**
 * 
 * @export
 * @interface WsLobbyUpdated
 */
export interface WsLobbyUpdated extends WsLobbyResponse {
    /**
     * 
     * @type {RsLobby}
     * @memberof WsLobbyUpdated
     */
    newLobby?: RsLobby;
}

export function WsLobbyUpdatedFromJSON(json: any): WsLobbyUpdated {
    return WsLobbyUpdatedFromJSONTyped(json, false);
}

export function WsLobbyUpdatedFromJSONTyped(json: any, ignoreDiscriminator: boolean): WsLobbyUpdated {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        ...WsLobbyResponseFromJSONTyped(json, ignoreDiscriminator),
        'newLobby': !exists(json, 'newLobby') ? undefined : RsLobbyFromJSON(json['newLobby']),
    };
}

export function WsLobbyUpdatedToJSON(value?: WsLobbyUpdated | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        ...WsLobbyResponseToJSON(value),
        'newLobby': RsLobbyToJSON(value.newLobby),
    };
}

