// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
    production: false,
    apiUrl: window.location.origin + '',
    logOutUrl: 'https://login.microsoftonline.com/common/wsfederation?wa=wsignout1.0',
    allowedOrigins: [window.location.origin, 'https://login.microsoftonline.com']
};