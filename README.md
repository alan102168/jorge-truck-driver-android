# 📱 Jorge the Truck Driver — Android Pixel Art

> **Este repositório contém apenas o aplicativo Android Pixel Art.** O Android Normal e os HTMLs têm repositórios próprios.

### ⬇️ Baixar

**[Ver Releases do Android Pixel Art](https://github.com/alan102168/jorge-truck-driver-android/releases)**

Procure um Release com nome `pixel-v2.0.x` e baixe o arquivo `Jorge-Android-Pixel.apk` (em versões antigas, `app-debug.apk`). Ele é uma **versão de teste**.

**[Ver compilações automáticas](https://github.com/alan102168/jorge-truck-driver-android/actions)**

### 🎮 Sobre a versão

- Visual pixelizado, com renderização interna de resolução reduzida e ampliação sem suavização.
- Caminhão, caixas, pontuação e obstáculos do jogo.
- Android em orientação horizontal, interface para celular e TV Box compatível.
- Controle por toque ou gamepad reconhecido pelo Android (USB/Bluetooth/receptor).
- O HDMI é somente a saída de imagem/som, não um sistema de jogos.
- **Identificador do app:** `br.com.jorgetruckdriver.android.pixel`, diferente do Android Normal.

**Importante:** use a branch `main` para novas alterações. As branches antigas `android-normal` e `android-pixel` são apenas histórico da migração.

### 📁 Estrutura

- `app/src/main/assets/index.html` — gráficos e mecânica HTML Pixel Edition.
- `app/src/main/java/br/com/jorgetruckdriver/android/MainActivity.java` — integração nativa com controles.
- `app/src/main/AndroidManifest.xml` — configurações Android.
- `app/build.gradle`, `build.gradle`, `settings.gradle` — dependências e compilação.
- `.github/workflows/android-apk.yml` — cria o APK e publica Releases.

### 🧪 Testes

A compilação confirma a criação do APK, mas **não comprova que todos os modelos de controles wireless, TVs ou celulares funcionem**. Teste a instalação e os comandos no aparelho.

### 📚 Outras versões

| Edição | Repositório |
|---|---|
| 🌐 HTML Normal | [Abrir](https://github.com/alan102168/jogo-sest-senat-2d-caminhao) |
| 🎨 HTML Pixel Art | [Abrir](https://github.com/alan102168/jorge-html-pixel) |
| 📱 Android Normal | [Abrir](https://github.com/alan102168/jorge-android-normal) |
| 📱 Android Pixel Art | [Abrir](https://github.com/alan102168/jorge-truck-driver-android) |
| 🎮 ROM Mega Drive Pixel Art | [Abrir](https://github.com/alan102168/jorge-megadrive-pixel) |
