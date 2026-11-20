# Światło roślin

Android app: phone light sensor -> PPFD estimate, plus Polish care guide for common house plants.

## Release
1. Bump nothing by hand: `git tag v1.1 && git push origin v1.1`.
2. CI builds a signed APK (`versionCode` = run number) and attaches it to the GitHub Release.
3. Phone updates via Obtainium (source: this repo).

Signing key: OpenBao `kv/integrations/ppfd-meter/android-signing` (`keystore_b64`, `password`), alias `release`.
Losing it means a new app identity. It is mirrored to Bitwarden.
