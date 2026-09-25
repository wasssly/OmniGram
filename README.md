## OmniGram for Android

OmniGram is an independent Telegram client for Android focused on a clean, customizable experience.
Source code: [github.com/wasssly/OmniGram](https://github.com/wasssly/OmniGram).

## Creating your Telegram Application

We welcome all developers to use our API and source code to create applications on our platform.
There are several things we require from **all developers** for the moment.

1. [**Obtain your own api_id**](https://core.telegram.org/api/obtaining_api_id) for your application.
2. Please **do not** use the name Telegram for your app — or make sure your users understand that it is unofficial.
3. Kindly **do not** use our standard logo (white paper plane in a blue circle) as your app's logo.
3. Please study our [**security guidelines**](https://core.telegram.org/mtproto/security_guidelines) and take good care of your users' data and privacy.
4. Please remember to publish **your** code too in order to comply with the licences.

### API, Protocol documentation

Telegram API manuals: https://core.telegram.org/api

MTproto protocol manuals: https://core.telegram.org/mtproto

### Compilation Guide

**Security note**: release keystores and Firebase configuration are not stored in this repository. Keep them locally or provide them to CI through GitHub Actions Secrets.

You will require Android Studio 2025.1.4, Android NDK 27.2.12479018 and Android SDK 36.

1. Clone OmniGram with its submodules:
   ```bash
   git clone --recursive --shallow-submodules https://github.com/wasssly/OmniGram.git OmniGram
   ```
   In case you forgot the `--recursive` flag, change to the `OmniGram` directory and run:
   ```bash
   git submodule init && git submodule update --init --recursive --depth=1
   ```
2. Copy your release.keystore into TMessagesProj/config
3. Fill out RELEASE_KEY_PASSWORD, RELEASE_KEY_ALIAS, RELEASE_STORE_PASSWORD in gradle.properties to access your  release.keystore
4.  Go to https://console.firebase.google.com/, create an Android app with application ID `com.omnigram.app`, turn on Firebase Messaging and download `google-services.json` into `TMessagesProj_App/`. This file is ignored by Git.
5. Open the project in the Studio (note that it should be opened, NOT imported).
6. Fill out values in TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java – there’s a link for each of the variables showing where and which data to obtain.
7. You are ready to compile OmniGram with `bash ./gradlew :TMessagesProj_App:assembleAfatRelease`.

### Automatic APK builds with GitHub Actions

Every push to `main` runs [`.github/workflows/build-apk.yml`](.github/workflows/build-apk.yml) and uploads the APK as a downloadable workflow artifact. Add these repository secrets in **Settings → Secrets and variables → Actions**:

- `GOOGLE_SERVICES_JSON_BASE64` — base64 of `TMessagesProj_App/google-services.json`;
- `RELEASE_KEYSTORE_BASE64` — base64 of `TMessagesProj/config/release.keystore`;
- `RELEASE_STORE_PASSWORD` — keystore password;
- `RELEASE_KEY_ALIAS` — key alias;
- `RELEASE_KEY_PASSWORD` — key password.

Create the Firebase secret locally with:

```bash
base64 -w 0 TMessagesProj_App/google-services.json
```

Create the keystore secret locally with:

```bash
base64 -w 0 TMessagesProj/config/release.keystore
```

The workflow never commits Firebase configuration or the keystore. It recreates both only inside the temporary GitHub runner and removes them after the build.

### Localization

We moved all translations to https://translations.telegram.org/en/android/. Please use it.
