# Zhúzi : consignes pour Claude (PC et téléphone)

Zhúzi (竹子, « bambou ») est une application Android pour apprendre le chinois mandarin, en français.

Le propriétaire est francophone et ne code pas. Il travaille avec Claude depuis son PC et depuis son téléphone (onglet **Code** de l'application Claude). **Avant toute tâche, lire `RELAIS.md`.**

## L'essentiel

- Toujours répondre **en français**, simplement. Aller droit au but.
- S'il faut lui demander quelque chose : une seule question, avec une option recommandée.
- Ne jamais afficher ni demander une clé ou un mot de passe. Ne jamais créer de compte ni se connecter à sa place.
- Publication sur le Play Store : seulement après un « Oui, publie » explicite.

## Technique

- Kotlin + Jetpack Compose (Material 3), un seul module `app`, paquet `com.zhuzi.app`.
- Versions centralisées dans `gradle/libs.versions.toml`.
- Dans le cloud, les serveurs Android de Google sont bloqués : on ne construit pas ici. **GitHub Actions** (`.github/workflows/build.yml`) construit l'APK à chaque envoi. Le propriétaire le télécharge dans l'onglet Actions (artefact `zhuzi-apk`) et l'installe sur son téléphone.
- Sur `main`, la construction publie aussi l'APK au lien fixe :
  https://github.com/Bigshow11-hub/Zh-zi/releases/latest/download/zhuzi.apk
- `app/debug.keystore` est une clé de test fixe, sans secret (mots de passe « android ») : elle permet d'installer chaque version par-dessus l'ancienne. Augmenter `versionCode` à chaque version donnée au propriétaire.
- Après un envoi, vérifier que la construction GitHub réussit ; sinon lire le journal et corriger.

## Git

- Travailler sur `main`. À la fin de chaque tâche : commit puis `git pull --rebase origin main && git push origin HEAD:main`.
- Messages de commit en anglais. Jamais de vraie clé de signature (Play Store) ni de `.env*` dans Git ; seule `app/debug.keystore` est permise.
