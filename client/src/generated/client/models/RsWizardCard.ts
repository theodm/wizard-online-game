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
 * @interface RsWizardCard
 */
export interface RsWizardCard {
    /**
     * 
     * @type {string}
     * @memberof RsWizardCard
     */
    type: string;
    /**
     * 
     * @type {string}
     * @memberof RsWizardCard
     */
    cardID: string;
    /**
     * 
     * @type {string}
     * @memberof RsWizardCard
     */
    cmpTrumpColor?: RsWizardCardCmpTrumpColorEnum;
    /**
     * 
     * @type {string}
     * @memberof RsWizardCard
     */
    color?: RsWizardCardColorEnum;
    /**
     * 
     * @type {number}
     * @memberof RsWizardCard
     */
    number?: number;
}

/**
* @export
* @enum {string}
*/
export enum RsWizardCardCmpTrumpColorEnum {
    Red = 'Red',
    Yellow = 'Yellow',
    Green = 'Green',
    Blue = 'Blue',
    None = 'None'
}/**
* @export
* @enum {string}
*/
export enum RsWizardCardColorEnum {
    Red = 'Red',
    Yellow = 'Yellow',
    Green = 'Green',
    Blue = 'Blue'
}

export function RsWizardCardFromJSON(json: any): RsWizardCard {
    return RsWizardCardFromJSONTyped(json, false);
}

export function RsWizardCardFromJSONTyped(json: any, ignoreDiscriminator: boolean): RsWizardCard {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'type': json['type'],
        'cardID': json['cardID'],
        'cmpTrumpColor': !exists(json, 'cmpTrumpColor') ? undefined : json['cmpTrumpColor'],
        'color': !exists(json, 'color') ? undefined : json['color'],
        'number': !exists(json, 'number') ? undefined : json['number'],
    };
}

export function RsWizardCardToJSON(value?: RsWizardCard | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'type': value.type,
        'cardID': value.cardID,
        'cmpTrumpColor': value.cmpTrumpColor,
        'color': value.color,
        'number': value.number,
    };
}

