# HytaleConnect

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**HytaleConnect** is a versatile and lightweight server integration plugin designed for Hytale server owners. It provides a robust bridge between your game server and external platforms, offering real-time vote verification, automated rewards, and a high-performance statistics reporting engine.

---

## 🇫🇷 Français

### Pourquoi choisir HytaleConnect ?
HytaleConnect a été conçu pour être l'outil de liaison ultime pour votre serveur. Que vous souhaitiez automatiser vos récompenses de vote ou fournir des statistiques en temps réel à des listes de serveurs, ce plugin s'adapte à vos besoins sans impacter les performances de votre machine.

### Caractéristiques
- **Vérification en temps réel** : Les actions externes (comme les votes) sont vérifiées via une API asynchrone non-bloquante.
- **Système de Récompenses Dynamique** : Configurez des commandes illimitées (items, monnaie, permissions) déclenchées automatiquement.
- **Reporting de Stats Push API** : Utilise une méthode "Push" (HTTP) moderne pour envoyer les statistiques, évitant tout besoin d'ouvrir des ports entrants complexes.
- **Hautement Paramétrable** : Activez ou désactivez globalement le système de vote, les rappels ou les stats selon votre usage.

> [!TIP]
> **Ouvert & Versatile** : Bien que pré-configuré pour l'écosystème [HytaleTop](https://hytale-top-serveur.fr), HytaleConnect est un bridge générique. Toutes les URLs sont configurables, permettant au plugin de s'interfacer avec n'importe quelle plateforme ou API personnalisée respectant le protocole.

### Installation
Consultez le guide détaillé : [CONFIGURATION.md](CONFIGURATION.md)

---

## 🇺🇸 English

### Why HytaleConnect?
HytaleConnect is the essential bridge for server owners who want a professional and seamless integration with external services. It eliminates the need for multiple heavy plugins by merging statistics reporting and reward automation into one high-performance package.

### Key Features
- **Asynchronous Processing**: All API checks and stat pushes are performed in the background to ensure zero impact on tick rate.
- **Flexible Rewards**: Configure an unlimited number of console commands to execute upon successful verification.
- **Stats Reporting**: Outbound HTTP "Push API" for firewall-friendly tracking, no port forwarding required.
- **CurseForge Ready**: A modular, generic design that works with any compatible API (defaulting to HytaleTop).

> [!NOTE]
> **A Generic Bridge**: While HytaleConnect works out-of-the-box with [HytaleTop](https://hytale-top-serveur.fr), it is built as a generic integration layer. All endpoints are fully customizable in the configuration, making it compatible with any custom backend or community platform.

### Quick Start
1. Download `HytaleConnect.jar` and place it in your `mods/` folder.
2. Start the server to generate the default configuration.
3. Edit `mods/HytaleConnect/config.json` with your specific API credentials.
4. Customize your rewards and messages in the config.
5. Restart and you're connected!

### Commands
- `/vote`: Allows players to manually trigger a check and receive their pending rewards.

### Documentation
Check the full configuration guide: [CONFIGURATION.md](CONFIGURATION.md)

---

## Development & Building

1. Ensure JDK 25 (or compatible) is installed.
2. Place `HytaleServer.jar` in the `libs/` folder.
3. Run `compile.bat` to build the final JAR.

---

© 2026 HytaleConnect - Open source integration for the Hytale community.