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
 * @interface RsWizardGameAndSettings
 */
export interface RsWizardGameAndSettings {
    /**
     * 
     * @type {string}
     * @memberof RsWizardGameAndSettings
     */
    dummy?: string;
}

export function RsWizardGameAndSettingsFromJSON(json: any): RsWizardGameAndSettings {
    return RsWizardGameAndSettingsFromJSONTyped(json, false);
}

export function RsWizardGameAndSettingsFromJSONTyped(json: any, ignoreDiscriminator: boolean): RsWizardGameAndSettings {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'dummy': !exists(json, 'dummy') ? undefined : json['dummy'],
    };
}

export function RsWizardGameAndSettingsToJSON(value?: RsWizardGameAndSettings | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'dummy': value.dummy,
    };
}

