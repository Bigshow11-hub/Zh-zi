# Relais Zhúzi

## État (25/11/2026)

- Version 0.2.0 installée par le propriétaire sur son téléphone : elle marche.

- Dépôt GitHub : `Bigshow11-hub/Zh-zi` (public).
- HSK 1 : 151 mots en 11 leçons thématiques (`data/Hsk1.kt`).
- Chaque leçon : cartes (toucher pour retourner, bouton 🔊 Écouter) et quiz (caractère → 4 traductions).
- Meilleur score de quiz gardé sur le téléphone, affiché dans la liste des leçons.
- Audio : voix chinoise du téléphone (synthèse vocale Android), rien à télécharger.
- Quiz en 3 modes : Sens (caractère → français), Écrit (français → caractère), Écoute (on entend le mot). Score par mode.
- Révision espacée (0.3.0, `data/Srs.kt`) : 6 boîtes, revoir après 1, 2, 4, 7, 15, 30 jours. Un mot entre quand on retourne sa carte ou qu'on le rencontre en quiz ; une erreur en quiz le renvoie en boîte 1. Carte « Révision du jour » en haut de l'accueil.
- Lien de téléchargement fixe : https://github.com/Bigshow11-hub/Zh-zi/releases/latest/download/zhuzi.apk
- Construction automatique de l'APK par GitHub Actions.

## Idées de suite

1. Rappel quotidien (notification) et série de jours.
2. Quiz des tons.
3. Tracé des caractères.
4. Phrases d'exemple pour chaque mot.
5. HSK 2.

## Décisions

- Langue de l'application : français. Public : débutants.
