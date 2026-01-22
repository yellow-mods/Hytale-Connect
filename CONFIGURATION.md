# Configuration - ServerWebLink

Ce document détaille l'ensemble des options de configuration pour le plugin `ServerWebLink`. Le fichier de configuration principal est généré automatiquement lors du premier lancement sous : `mods/ServerWebLink/config.json`.

---

## Structure du Fichier
```json
{
  "serverApiKey": "hts_...",
  "apiUrl": "https://api.example.com/v1/server/...",
  "voteUrl": "https://example.com/vote/...",
  "voteEnabled": true,
  "commandName": "vote",
  "stackRewards": true,
  "rewards": {
    "message": "§aMerci pour votre vote ! Vous avez reçu {count} récompenses.",
    "commands": [
      "give {player} Rock_Gem_Diamond"
    ]
  },
  "messages": {
    "checkingVote": "§7Vérification en cours...",
    "voteBroadcast": "§6{player} §aa voté ! /{command}",
    "alreadyClaimed": "§eVote déjà réclamé.",
    "notVoted": "Veuillez voter sur : {url}",
    "nextVoteWait": "§cAttendez encore {time}.",
    "errorChecking": "§cErreur de connexion API.",
    "errorClaiming": "§cErreur lors du claim."
  },
  "stats": {
    "enabled": true,
    "intervalMinutes": 2,
    "privacy": {
       "showPlugins": true,
       "showPlayerNames": true,
       "showUptime": true,
       "showRAM": true,
       "showWorlds": true
    }
  },
  "reminder": {
    "enabled": true,
    "intervalMinutes": 30,
    "message": "§aN'oubliez pas de voter ! /{command}"
  }
}
```

---

## 1. Paramètres Globaux

### `voteEnabled`
- **Type** : `boolean`
- **Description** : Active ou désactive l'intégralité du module de vote (commandes, rappels, vérification). Si `false`, seul le module de statistiques peut rester actif.

### `commandName`
- **Type** : `string`
- **Défaut** : `"vote"`
- **Description** : Nom de la commande que les joueurs utiliseront en jeu.

### `serverApiKey`
- **Description** : Votre clé d'accès unique fournie par votre plateforme de listage (ex: HytaleTop).

---

## 2. Module de Vote & Récompenses

### `apiUrl` & `voteUrl`
- **apiUrl** : Point d'entrée de l'API pour les vérifications.
- **voteUrl** : Lien direct vers la page de vote (utilisé dans les messages via `{url}`).

### `stackRewards`
- **Description** : Si `true`, cumule les récompenses si le joueur vote plusieurs fois avant de faire sa commande en jeu.

### `rewards.commands`
- Liste des commandes console exécutées pour chaque vote valide.
- **Placeholder** : `{player}` est remplacé par le nom du joueur.

---

Le plugin envoie automatiquement les statistiques du serveur à la plateforme configurée via des requêtes HTTP POST (Push API). Cela ne nécessite aucune ouverture de port entrant sur votre pare-feu.

### `stats.enabled`
- Active l'envoi des statistiques.

### `stats.privacy`
Permet de masquer certaines données sensibles :
- `showPlugins` : Liste des plugins.
- `showPlayerNames` : Noms et UUIDs des joueurs.
- `showRAM` / `showUptime` / `showWorlds` : Données techniques.

---

## 4. Module de Rappel (Reminder)

### `reminder.enabled`
- Affiche un message périodique à tous les joueurs connectés.

### `reminder.intervalMinutes`
- Fréquence du rappel (ex: 30 pour toutes les demi-heures).

---

© 2026 ServerWebLink - Documentation de Configuration.
