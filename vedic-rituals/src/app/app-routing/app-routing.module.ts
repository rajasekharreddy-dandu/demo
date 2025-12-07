import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// --- Components (Inferred and partially visible) ---
// Note: You would need to import all listed components.
import { HomeComponent } from './../views/home/home.component';
// import { AccessDeniedComponent } from './common/access-denied/access-denied.component';
// import { CurrencyConversionComponent } from './views/systemAdmin/currency-conversion/currency-conversion.component';
// import { PricingChannelMappingComponent } from './views/Admin-Pricing/pricing-channel-mapping/pricing-channel-mapping.component';
// import { PricingTerritoryMappingComponent } from './views/Admin-Pricing/pricing-territory-mapping/pricing-territory-mapping.component';
// import { PricingTerritoryCurrencyMasterComponent } from './views/Admin-Pricing/pricing-territory-currency-master/pricing-territory-currency-master.component';
// import { UserRolesComponent } from './views/Admin-User-Roles/user-roles/user-roles.component';
// import { InitiateApprovalWOPricelineComponent } from './views/opportunityTracking/initiate-approval-wopriceline/initiate-approval-wopriceline.component';
// import { TrackApprovalComponent } from './views/opportunityTracking/track-approval/track-approval.component';
// ... other components like RollupScreenComponent, etc.

// --- Route Guard Service ---
// This is the service used to check permissions before activating a route.
import { UserRouteAccessService } from './security/user-route-access.service';

// --- Angular Routes Definition ---
const routes: Routes = [
  // Home Route - Protected
  {
    path: 'home',
    component: HomeComponent,
    data: { page: 'Home Route' },
    canActivate: [UserRouteAccessService],
  },

  // --- Admin Routes with Page Data and Guard ---
//   {
//     path: 'Admin-CurrencyConversion',
//     component: CurrencyConversionComponent,
//     data: { page: 'CurrencyConversion' },
//     canActivate: [UserRouteAccessService],
//   },
//   {
//     path: 'Admin-PricingChannelMapping',
//     component: PricingChannelMappingComponent,
//     data: { page: 'PricingChannelMapping' },
//     canActivate: [UserRouteAccessService],
//   },
//   {
//     path: 'Admin-PricingTerritoryMapping',
//     component: PricingTerritoryMappingComponent,
//     data: { page: 'PricingTerritoryMapping' },
//     canActivate: [UserRouteAccessService],
//   },
//   {
//     path: 'Admin-PricingTerritoryCurrencyMaster',
//     component: PricingTerritoryCurrencyMasterComponent,
//     data: { page: 'PricingTerritoryCurrencyMaster' },
//     canActivate: [UserRouteAccessService],
//   },

//   // --- User Roles Route with 'authorities' (Role-Based Access Control) ---
//   {
//     path: 'Admin-UserRoles',
//     component: UserRolesComponent,
//     data: {
//       page: 'User Roles',
//       authorities: ['PRICING ADMINISTRATOR'], // Requires this specific role
//     },
//     canActivate: [UserRouteAccessService],
//   },

//   // --- Opportunity Tracking Routes with Multiple Authorities ---
//   {
//     path: 'OpportunityTracking-InitiateApprovalWOPricelineChange',
//     component: InitiateApprovalWOPricelineComponent,
//     data: {
//       page: 'InitiateApproval',
//       authorities: ['PRICING ADMINISTRATOR', 'ENGINE PRICER', 'OPTION ANALYST'],
//     },
//     canActivate: [UserRouteAccessService],
//   },
//   {
//     path: 'OpportunityTracking-TrackApproval',
//     component: TrackApprovalComponent,
//     data: {
//       page: 'TrackApproval',
//       authorities: ['PRICING ADMINISTRATOR', 'ENGINE PRICER', 'OPTION ANALYST', 'APPROVER'],
//     },
//     canActivate: [UserRouteAccessService],
//   },

//   // --- Access Denied Component (Landing page for unathorized users) ---
//   {
//     path: 'noAccess',
//     component: AccessDeniedComponent,
//     data: { page: 'Access Denied' },
//   },

  // --- Redirects ---
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: '**', redirectTo: 'home' }, // Catch-all for unknown routes
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule],
  providers: [UserRouteAccessService] // Ensure the guard is provided if not root-provided
})
export class AppRoutingModule {}