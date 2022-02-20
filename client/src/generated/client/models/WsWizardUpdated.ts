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
    RsWizardGameStateForPlayer,
    RsWizardGameStateForPlayerFromJSON,
    RsWizardGameStateForPlayerFromJSONTyped,
    RsWizardGameStateForPlayerToJSON,
} from './RsWizardGameStateForPlayer';
import {
    WsLobbyResponse,
    WsLobbyResponseFromJSON,
    WsLobbyResponseFromJSONTyped,
    WsLobbyResponseToJSON,
} from './WsLobbyResponse';
import {
    WsWizardUpdatedAllOf,
    WsWizardUpdatedAllOfFromJSON,
    WsWizardUpdatedAllOfFromJSONTyped,
    WsWizardUpdatedAllOfToJSON,
} from './WsWizardUpdatedAllOf';

/**
 * 
 * @export
 * @interface WsWizardUpdated
 */
export interface WsWizardUpdated extends WsLobbyResponse {
    /**
     * 
     * @type {RsWizardGameStateForPlayer}
     * @memberof WsWizardUpdated
     */
    newWizardState: RsWizardGameStateForPlayer;
}

export function WsWizardUpdatedFromJSON(json: any): WsWizardUpdated {
    return WsWizardUpdatedFromJSONTyped(json, false);
}

export function WsWizardUpdatedFromJSONTyped(json: any, ignoreDiscriminator: boolean): WsWizardUpdated {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        ...WsLobbyResponseFromJSONTyped(json, ignoreDiscriminator),
        'newWizardState': RsWizardGameStateForPlayerFromJSON(json['newWizardState']),
    };
}

export function WsWizardUpdatedToJSON(value?: WsWizardUpdated | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        ...WsLobbyResponseToJSON(value),
        'newWizardState': RsWizardGameStateForPlayerToJSON(value.newWizardState),
    };
}

