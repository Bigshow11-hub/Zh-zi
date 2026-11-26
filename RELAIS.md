# Relais Zhúzi

## État (25/11/2026)

- Version 0.2.0 installée par le propriétaire sur son téléphone : elle marche.
- Refero (outil de design, serveur MCP) : sa clé est un secret réseau de l'environnement cloud « Default » pour `api.refero.design`. Dans une nouvelle session cloud, l'ajouter avec `claude mcp add --transport http refero https://api.refero.design/mcp --header "Authorization: Bearer proxy-injected"` (le réseau remplace la fausse clé par la vraie).

- Dépôt GitHub : `Bigshow11-hub/Zh-zi` (public).
- HSK 1 : 151 mots en 11 leçons thématiques (`data/Hsk1.kt`).
- Chaque leçon : cartes (toucher pour retourner, lien « Écouter ») et quiz.
- Meilleur score de quiz gardé sur le téléphone, affiché dans la liste des leçons.
- Audio : voix chinoise du téléphone (synthèse vocale Android), rien à télécharger.
- Quiz en 3 modes : Sens (caractère → français), Écrit (français → caractère), Écoute (on entend le mot). Score par mode.
- Révision espacée (0.3.0, `data/Srs.kt`) : 6 boîtes, revoir après 1, 2, 4, 7, 15, 30 jours. Un mot entre quand on retourne sa carte ou qu'on le rencontre en quiz ; une erreur en quiz le renvoie en boîte 1. Carte « Révision du jour » en haut de l'accueil.
- Rappel quotidien (0.4.0, `reminder/Reminders.kt`, WorkManager) : notification vers 19 h seulement si on n'a pas encore étudié ce jour-là. Accord des notifications demandé une fois au lancement (Android 13+), sinon bouton « Activer le rappel quotidien » sur l'accueil.
- Série de jours d'affilée (`data/Streak.kt`) : compte un jour dès qu'on retourne une carte, répond à un quiz ou révise. Affichée en haut à droite de l'accueil (« 12 jours d'affilée »).
- Look « Cahier d'écriture » (0.5.0) : papier blanc, encre bleu-noir #1E2A3A, chaque caractère dans une case d'écriture rouge (田字格/米字格, `ui/Components.kt`). Le rouge #E04A3A sert seulement à dessiner les cases. Caractères en police pinceau LXGW WenKai (licence OFL), réduite aux mots de l'appli (`res/font/wenkai.ttf`, 61 Ko) : **à refaire si on ajoute des mots** (voir `tools/font/README.md`). Pas d'emoji dans l'interface.
- Logo (choisi le 26/11/2026 parmi 6 propositions, canevas https://claude.ai/artifact/R87J3Vefmn92FaQsqxt4fD) : « coup de pinceau », 竹 calligraphié noir (Ma Shan Zheng) signé d'un petit sceau rouge 子 (0.5.1, `drawable/ic_launcher_foreground.xml`). En notification : le 竹 seul, en blanc.
- Refonte « papier, encre et sceau » (0.6.0) : accueil avec panneau « Aujourd'hui » (mots à revoir en grand, anneau des mots découverts), carte « Continuer », parcours avec barres de progression et sceau 记 sur les leçons maîtrisées ; cartes qui se retournent en 3D ; quiz avec barre de progression, panneau de réponse qui glisse, vibration, secousse si faux ; sceau rouge qui se tamponne en fin de quiz et de révision. Panneaux arrondis et boutons en pilule, cases d'écriture nettes. Rôles des couleurs dans `ui/theme/Theme.kt`.
- Photos d'écran automatiques : `app/src/test/.../ScreenshotTest.kt` (Robolectric + Roborazzi), lancées par GitHub ; les PNG sont publiés avec l'APK (ex. https://github.com/Bigshow11-hub/Zh-zi/releases/latest/download/1-accueil.png). Les regarder après chaque changement de design.
- Refero : skill installé, mais l'abonnement Refero (MCP) est inactif : recherche faite sur le web à la place.
- Mode nuit (0.6.1) : l'appli suit le réglage du téléphone (`DayColors` / `NightColors` dans `ui/theme/Theme.kt`, logo de nuit dans `drawable-night/`). Boutons d'action fixés en bas de l'écran (sous le pouce) dans les cartes et la révision.
- Réseau de l'environnement cloud « Default » : sites de design ouverts le 26/11/2026 (muz.li, m3.material.io, blog/design.duolingo.com, apps.apple.com, androidauthority, uxdesign.cc, tech.yahoo.com). L'outil de lecture de pages ne les voit pas : les lire avec `curl` dans la session.
- Barre de navigation en bas (0.7.0, `ui/Navigation.kt`) : 4 onglets dont l'icône est un caractère — 家 Accueil (aujourd'hui, leçon à reprendre, mot du jour), 学 Leçons (parcours HSK 1), 字 Caractères (tous les mots, recherche sans accents ni tons, filtres Tous/Vus/Retenus/Nouveaux, fiche en panneau du bas), 记 Progrès (série, chiffres clés, interrupteur du rappel du soir, à propos). Leçons et révision s'ouvrent en plein écran, sans la barre.
- Couleurs des tons (0.8.0, `data/Tones.kt` + `tones` dans `Theme.kt`) : 1er ton rouge, 2e ambre, 3e sarcelle, 4e bleu, neutre gris ; appliquées aux caractères (cases) et au pinyin. Choisies pour différer aussi en clarté (daltonisme). Cases d'écriture redessinées : un cadre continu aux coins doux, guides en tirets gris.
- Ordre des traits animé (0.8.0, `ui/StrokeOrder.kt`) : données Make Me a Hanzi (`assets/strokes.json`, licence Arphic, voir `tools/strokes/`). Dans la fiche d'un mot (onglet Caractères) et via le lien « Ordre des traits » des cartes et de la révision. Trait en cours en rouge, point rouge = départ du trait ; boutons « ‹ Trait », « Rejouer », « Trait › ».
- Langues (0.9.0) : français, anglais, russe, au choix dans l'onglet Progrès (drapeaux dessinés, `ui/I18n.kt`). Traduit : le sens des 151 mots (`Word.meaning(lang)`), les titres des leçons, toute l'interface (`tr(fr, en, ru)`), les notifications, la date. Pluriels russes gérés (`ruPlural`). Par défaut : langue du téléphone si anglais ou russe, sinon français. **Tout nouveau texte d'interface doit passer par `tr(...)` avec les trois langues.**
- Version 1.0.0 « premium et personnalité » : police d'interface Manrope (`res/font/manrope_*.ttf`, latin + cyrillique ; le pinyin reste en police du téléphone car Manrope n'a pas ǎ ǐ ǒ ǔ) ; carte « Aujourd'hui » en encre profonde avec bambou dessiné (`BambooArt`) ; salut selon l'heure (早上好/下午好/晚上好) ; proverbe du jour (12 proverbes, `data/Persona.kt`) ; accroche par leçon ; encouragements variés au quiz ; phrase d'exemple pour chacun des 151 mots en fr/en/ru (`data/Examples.kt`), le mot ressort en couleurs de tons. La police pinceau couvre désormais 222 caractères (mots + exemples + proverbes) : `tools/font/subset_wenkai.py` lit tout le dossier `data/`.
- Version 1.1.0 « lavande » (inspirée d'une maquette choisie par le propriétaire le 26/11/2026) : fond en dégradé lavande, cartes blanches flottantes (`softCard`, ombre teintée), accent indigo → violet (`accent`/`accent2` dans `Theme.kt`) pour boutons, sélection, progression ; accueil avec boutons ronds recherche/rappel, avatar au logo, 3 tuiles chiffres, carte « Objectif du jour » en dégradé, liste « Au programme » à cocher ; barre du bas flottante avec bouton central « séance du jour » (révision s'il y en a, sinon la leçon à reprendre). Mode nuit en indigo très sombre.
- Mascotte personnalisable (1.2.0) : un petit panda en pull, dessiné en code avec dégradés et reflets (rendu « pâte à modeler 3D », `ui/Avatar.kt`). 5 niveaux selon les mots découverts (Pousse 0, Jeune tige 10, Bambou 30, Bosquet 60, Forêt 100 ; la tige de bambou qu'il tient grandit). Tenue (6 couleurs), fond (5), accessoires à débloquer : écharpe (série 3 jours), chapeau de paille (20 mots), casque (quiz d'écoute ≥ 80 %), bonnet de lettré (1 leçon maîtrisée), couronne de jade (100 mots). Écran « Mon avatar » (`ui/AvatarScreen.kt`), accessible depuis l'accueil et l'onglet Progrès. Choix gardés dans `settings` (`data/Avatar.kt`). Un vrai modèle 3D demanderait Higgsfield payant + une bibliothèque 3D.
- Tracé des caractères (1.3.0, `ui/Writing.kt`) : onglet « Écrire » dans chaque leçon (Cartes / Écrire / Quiz). On trace chaque trait au doigt ; `StrokeJudge` compare au tracé médian (départ, arrivée, sens, distance moyenne < 115, longueur 0,4–2,6×, points espacés ≥ 20 pour ignorer les tremblements). Testé sur les 1 179 traits : 99 % des traits tremblés/décalés acceptés, 0 % des traits à l'envers, 0,1 % des mauvais traits. Indice après 2 erreurs, interrupteur « Modèle », sceau 好 à la fin du mot ; le mot compte comme découvert.
- 1.4.0 : fond dégradé sous la barre du bas (le contenu ne déborde plus jusqu'aux boutons du téléphone) ; pinyin en police Noto Sans intégrée (`res/font/pinyin_*.ttf`, `PinyinFont`) — **jamais `FontFamily.Default`** : sur le téléphone du propriétaire la police système est décorative ; choix Apparence Automatique / Clair / Sombre dans Progrès (`ThemePref`, clé `theme`) ; onglet Leçons refait (carte de synthèse en dégradé, cartes de leçon avec pastille colorée, accroche, pilules de quiz, statut, leçon en cours liserée). Constaté sur le téléphone : la police pinceau s'affiche bien (seul l'outil de photo ne l'affiche pas).
- Higgsfield (génération d'images) : connecté, mais l'offre gratuite ne permet aucune génération (abonnement Basic requis).
- Lien de téléchargement fixe : https://github.com/Bigshow11-hub/Zh-zi/releases/latest/download/zhuzi.apk
- Construction automatique de l'APK par GitHub Actions.

## Idées de suite

1. Quiz des tons.
3. Phrases d'exemple pour chaque mot.
4. HSK 2.
5. Choisir l'heure du rappel.

## Décisions

- Langue de l'application : français. Public : débutants.
