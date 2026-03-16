/**
 * Feature Flags — toggle modules on/off without removing code.
 *
 * To re-enable a postponed module, flip its flag to `true`.
 * Navigation, routes, and backend endpoints all respect these flags.
 */
export interface FeatureFlags {
  plantManagement: boolean
  poultry: boolean
  accounting: boolean
  inventory: boolean
  iotSensors: boolean
}

export const features: FeatureFlags = {
  plantManagement: false, // postponed — plant monitoring module
  poultry: true,          // main module
  accounting: true,
  inventory: true,
  iotSensors: false,      // postponed — ESP32 sensor hardware
}
