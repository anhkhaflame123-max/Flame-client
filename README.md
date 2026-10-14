# FLAME CLIENT — Android Edition 1.2 (starter source)

This update adds the black/red/orange FLAME home screen, a draggable floating ClickGUI overlay, and the supplied Bedrock ChunkFinder v1.2.0 resource pack bundled in `app/src/main/assets/packs/`.

## Included working flows
- Home screen and floating overlay UI.
- Android overlay-permission request.
- Export ChunkFinder `.mcpack` through Android's document picker, then attempt to open it with Minecraft.
- ChunkFinder pack has been normalized so `manifest.json` is at the archive root. Original LICENSE and README are retained.

## Important limitations
- This is an Android Studio source project, not a built APK. It has not been compiled or tested on a physical phone in this environment.
- The floating menu is a separate Android overlay, not an injected Minecraft client; it cannot directly toggle in-game chunk borders or change Minecraft memory/game mechanics.
- ChunkFinder still must be imported and activated as a resource pack in Minecraft. Its settings are managed by Minecraft/pack itself.
- The pack manifest declares `min_engine_version` 1.26.0. The CurseForge release page lists support for 26.0 through 26.40; direct verification on 1.26.52 has not been performed.
- The embedded pack is licensed GPL-3.0; retain attribution and provide corresponding source when redistributing modified pack copies.

## Build
Open this folder in Android Studio with Android SDK 35 and a compatible Gradle setup. Build > Build APK(s).

## Build APK
See `BUILD_APK_GUIDE_VI.md`. The included GitHub Actions workflow builds a debug APK and uploads it as an artifact. This project ZIP is not itself an APK.

See `BUILD_STATUS_VI.md` for the exact implemented features and limitations.


## Build 1.3
Pack Manager includes user-supplied packs and exports them individually. See `PACK_INTEGRATION_REPORT_VI.md`. No in-game runtime test or APK build is claimed.
