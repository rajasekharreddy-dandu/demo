// src/environments/environment.ts

// This file can be replaced during build by using the `fileReplacements` array.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  // apiurl: window.location.origin + '/Pricing/', // Inferred
  apiUrl: window.location.origin + '',
  logOutUrl: 'https://login.microsoftonline.com/common/wsfederation?wa=wsignout1.0&wreply=' + window.location.origin, // Inferred and truncated
  allowedOrigins: [window.location.origin, 'https://login.microsoftonline.com'],
};