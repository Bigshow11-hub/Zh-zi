# Relais Zhúzi

## État (25/11/2026)

- Version 0.2.0 installée par le propriétaire sur son téléphone : elle marche.
- Refero (outil de design, serveur MCP) : sa clé est un secret réseau de l'environnement cloud « Default » pour `api.refero.design`. Dans une nouvelle session cloud, l'ajouter avec `claude mcp add --transport http refero https://api.refero.design/mcp --header "Authorization: Bearer proxy-injected"` (le réseau remplace la fausse clé par la vraie).

- Dépôt GitHub : `Bigshow11-hub/Zh-zi` (public).
- HSK 1 : 151 mots en 11 leçons thématiques (`data/Hsk1.kt`).
- Chaque leçon : cartes (toucher pour retourner, bouton 🔊 Écouter) et quiz (caractère → 4 traductions).
- Meilleur score de quiz gardé sur le téléphone, affiché dans la liste des leçons.
- Audio : voix chinoise du téléphone (synthèse vocale Android), rien à télécharger.
- Quiz en 3 modes : Sens (caractère → français), Écrit (français → caractère), Écoute (on entend le mot). Score par mode.
- Révision espacée (0.3.0, `data/Srs.kt`) : 6 boîtes, revoir après 1, 2, 4, 7, 15, 30 jours. Un mot entre quand on retourne sa carte ou qu'on le rencontre en quiz ; une erreur en quiz le renvoie en boîte 1. Carte « Révision du jour » en haut de l'accueil.
- Rappel quotidien (0.4.0, `reminder/Reminders.kt`, WorkManager) : notification vers 19 h seulement si on n'a pas encore étudié ce jour-là. Accord des notifications demandé une fois au lancement (Android 13+), sinon bouton « Activer le rappel quotidien » sur l'accueil.
- Série de jours d'affilée (`data/Streak.kt`) : compte un jour dès qu'on retourne une carte, répond à un quiz ou révise. Affichée « 🔥 Série » sur l'accueil.
- Look « Cahier d'écriture » (0.5.0) : papier blanc, encre bleu-noir #1E2A3A, chaque caractère dans une case d'écriture rouge (田字格/米字格, `ui/Components.kt`). Le rouge #E04A3A sert seulement à dessiner les cases. Caractères en police pinceau LXGW WenKai (licence OFL), réduite aux mots de l'appli (`res/font/wenkai.ttf`, 61 Ko) : **à refaire si on ajoute des mots** (script de découpe dans l'historique de la session du 26/11/2026, avec fontTools). Pas d'emoji dans l'interface.
- Icône : 竹 au pinceau dans une case 米字格 (`drawable/ic_launcher_foreground.xml`), aussi en icône de notification.
- Higgsfield (génération d'images) : connecté, mais l'offre gratuite ne permet aucune génération (abonnement Basic requis).
- Lien de téléchargement fixe : https://github.com/Bigshow11-hub/Zh-zi/releases/latest/download/zhuzi.apk
- Construction automatique de l'APK par GitHub Actions.

## Idées de suite

1. Quiz des tons.
2. Tracé des caractères.
3. Phrases d'exemple pour chaque mot.
4. HSK 2.
5. Choisir l'heure du rappel.

## Décisions

- Langue de l'application : français. Public : débutants.
