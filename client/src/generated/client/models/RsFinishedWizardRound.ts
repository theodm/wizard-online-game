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
/**
 * 
 * @export
 * @interface RsFinishedWizardRound
 */
export interface RsFinishedWizardRound {
    /**
     * 
     * @type {number}
     * @memberof RsFinishedWizardRound
     */
    numberOfCards: number;
    /**
     * 
     * @type {{ [key: string]: number; }}
     * @memberof RsFinishedWizardRound
     */
    bets: { [key: string]: number; };
    /**
     * 
     * @type {{ [key: string]: number; }}
     * @memberof RsFinishedWizardRound
     */
    sticheOfPlayer: { [key: string]: number; };
    /**
     * 
     * @type {{ [key: string]: number; }}
     * @memberof RsFinishedWizardRound
     */
    cmpSumPointsOfPlayers: { [key: string]: number; };
}

export function RsFinishedWizardRoundFromJSON(json: any): RsFinishedWizardRound {
    return RsFinishedWizardRoundFromJSONTyped(json, false);
}

export function RsFinishedWizardRoundFromJSONTyped(json: any, ignoreDiscriminator: boolean): RsFinishedWizardRound {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'numberOfCards': json['numberOfCards'],
        'bets': json['bets'],
        'sticheOfPlayer': json['sticheOfPlayer'],
        'cmpSumPointsOfPlayers': json['cmpSumPointsOfPlayers'],
    };
}

export function RsFinishedWizardRoundToJSON(value?: RsFinishedWizardRound | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'numberOfCards': value.numberOfCards,
        'bets': value.bets,
        'sticheOfPlayer': value.sticheOfPlayer,
        'cmpSumPointsOfPlayers': value.cmpSumPointsOfPlayers,
    };
}
