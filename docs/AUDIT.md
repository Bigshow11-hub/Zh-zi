# Audit de Zhúzi (branche `design-ios`, 27/11/2026)

Ce document liste ce qui peut être amélioré dans l'application, **du plus important au moins important**.
Pour chaque point : ce que c'est, où (fichier), pourquoi c'est important, ce qu'on propose, et si c'est
**déjà corrigé** sur la branche `design-ios`.

Légende : ✅ corrigé sur `design-ios` · 🟡 en partie · ⬜ pas encore.

---

## A. À régler avant une sortie sur le Play Store

### 1. Pas de son si le téléphone n'a pas de voix chinoise ⬜
- **Où** : `ui/Speaker.kt`.
- **Pourquoi** : si la voix chinoise n'est pas installée, `speak()` ne fait rien, en silence. L'élève touche
  « Écouter », rien ne se passe, et le quiz « Écoute » devient impossible. C'est la première cause probable
  de mauvaises notes sur le Play Store.
- **Proposition** : retenir l'état « voix absente », l'afficher (petit bandeau « Installe la voix chinoise »
  avec un bouton qui ouvre `TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA`), et masquer le quiz Écoute tant
  qu'il n'y a pas de voix.

### 2. L'application est signée avec la clé de test ⬜
- **Où** : `app/build.gradle.kts` (`signingConfigs.debug`, `app/debug.keystore`).
- **Pourquoi** : le Play Store refuse une appli signée avec une clé de test. Il faut une vraie clé (gardée
  hors de Git) et publier un « App Bundle » (`.aab`), pas un APK.
- **Proposition** : créer la clé de publication (le propriétaire la garde), l'ajouter comme secret GitHub,
  ajouter une tâche `bundleRelease` dans `.github/workflows/build.yml`.

### 3. Pas de réduction du code pour la version publiée ⬜
- **Où** : `app/build.gradle.kts` (`isMinifyEnabled = false`).
- **Pourquoi** : l'APK est plus lourd et un peu plus lent au démarrage. R8 enlève le code inutile.
- **Proposition** : `isMinifyEnabled = true` + `isShrinkResources = true` en version publiée, tester que ML Kit
  et pinyin4j marchent toujours (règles ProGuard si besoin).

### 4. Les progrès peuvent se perdre en changeant de téléphone ⬜
- **Où** : `AndroidManifest.xml` (`allowBackup="true"` sans règles), `data/Srs.kt`, `data/Streak.kt`.
- **Pourquoi** : la sauvegarde Android n'est pas réglée (pas de `dataExtractionRules`), et il n'y a ni export
  ni compte. Perdre sa série de 100 jours est la pire expérience pour une appli d'apprentissage.
- **Proposition** : ajouter `res/xml/data_extraction_rules.xml` qui inclut les préférences `srs`, `streak`,
  `progress`, `settings`, `mywords` ; plus tard, un export/import de fichier.

### 5. Politique de confidentialité et fiche « Sécurité des données » ⬜
- **Où** : à créer (page web) ; permissions dans `AndroidManifest.xml`.
- **Pourquoi** : l'appli utilise l'appareil photo, la galerie, la lecture de texte de Google (ML Kit) et les
  notifications. Le Play Store exige une politique de confidentialité et le formulaire « Data safety ».
- **Proposition** : une page simple (« rien ne quitte le téléphone ; ML Kit fonctionne sur l'appareil »).

---

## B. Expérience et design

### 6. Look iOS « premium » et cohérent ✅
- **Où** : `ui/theme/Theme.kt`, nouveau `ui/Kit.kt`, `ui/Components.kt`, `ui/Navigation.kt`.
- **Pourquoi** : chaque écran avait ses propres tailles, ombres et rayons ; les icônes étaient des
  caractères ou des « › » tapés au clavier.
- **Fait** : fond « groupé » gris (noir la nuit), cartes sans ombre aux coins continus (`SmoothShape`),
  échelle de texte iOS (grand titre 34, corps 17, note 13) en Manrope, pictogrammes dessinés au trait
  (`ZIcon`), listes groupées avec filets décalés et chevrons (`GroupedSection`, `GroupedRow`), sélecteur
  segmenté iOS (`Segmented`), grands titres qui laissent place à une barre compacte en défilant
  (`CompactTitleBar`), panneaux avec poignée (`ZhuziSheet`), barre du bas en pilule translucide.

### 7. Écran Progrès : les réglages ressemblaient à une page de texte ✅
- **Où** : `ui/ProgressScreen.kt`.
- **Fait** : sections groupées comme l'appli Réglages (Apparence, Langue avec coche, Rappel avec
  interrupteur et heure, À propos avec la version). La ligne « Rappel quotidien » entière bascule
  l'interrupteur (et TalkBack l'annonce comme un interrupteur).

### 8. Pas d'accueil pour un nouvel élève ⬜
- **Où** : `MainActivity.kt` (aucun écran de premier lancement).
- **Pourquoi** : à la première ouverture, on arrive directement sur l'accueil, sans choisir son niveau
  (un élève qui connaît déjà le HSK 1 doit tout « découvrir »), son objectif du jour ni l'heure du rappel.
  La demande d'autorisation des notifications arrive sans explication.
- **Proposition** : 3 écrans courts : langue → niveau de départ → objectif et heure du rappel (avec
  explication, puis la demande d'autorisation).

### 9. Rien ne remplace le geste de glisser les cartes pour TalkBack… sauf les boutons ✅ (vérifié)
- **Où** : `ui/LessonScreen.kt` (`Flashcards`).
- **Pourquoi** : le glissement n'est pas accessible au lecteur d'écran.
- **État** : les boutons « Précédent / Suivant » sont toujours là en bas : rien à corriger.

### 10. Le tracé des caractères n'a pas d'alternative ⬜
- **Où** : `ui/Writing.kt`.
- **Pourquoi** : impossible pour une personne qui ne peut pas tracer au doigt (TalkBack, motricité).
- **Proposition** : bouton « Passer ce caractère » et lecture du nombre de traits.

### 11. Cartes de hauteur fixe ⬜
- **Où** : `ui/LessonScreen.kt` (`FlipCard` : `height(360.dp)`, pile de 380 dp), `Quiz` (marge basse fixe de 220 dp).
- **Pourquoi** : avec une grande taille de police (réglage du téléphone à 130 %) ou un long sens en russe,
  le dos de la carte peut être coupé ; sur un petit téléphone, la carte prend presque tout l'écran.
- **Proposition** : hauteur minimale (`heightIn(min = …)`) plutôt que fixe, et marge basse du quiz calculée
  d'après la hauteur réelle du panneau de réponse.

### 12. Pas de mise en page tablette ou paysage ⬜
- **Où** : `ui/Navigation.kt`, tous les écrans.
- **Pourquoi** : sur tablette, la barre du bas et les cartes s'étirent sur toute la largeur.
- **Proposition** : largeur de lecture limitée (~600 dp, centrée) ; plus tard une barre latérale.

### 13. Les onglets n'ont plus de caractère comme icône ✅ (choix à valider)
- **Où** : `ui/Navigation.kt`.
- **Pourquoi** : 家 学 字 记 étaient jolis mais se lisent mal à 20 sp pour un débutant, et ne ressemblaient
  à rien de connu. Remplacés par des icônes dessinées : maison, livre, case d'écriture 田字格, barres.
  Le caractère reste dans le code (`Tab.glyph`) si le propriétaire préfère revenir en arrière.

### 14. Liens soulignés et « › » tapés au clavier ✅
- **Où** : `ListenLink` (`ui/LessonScreen.kt`), `StrokeLink` (`ui/StrokeOrder.kt`), `ExampleBlock`
  (`ui/Components.kt`), accueil.
- **Fait** : lien = pictogramme + texte en accent (haut-parleur, pinceau), zone tactile de 48 dp ;
  chevrons dessinés.

### 15. Une seule couleur d'accent, le rouge réservé au « validé » ✅
- **Où** : `ui/ExamScreen.kt` (`ExamEntry`).
- **Fait** : la tuile « Examen blanc » et la pastille 知 de « Le savais-tu ? » étaient rouge sceau ; elles
  passent à l'accent. Le rouge sert aux sceaux, à la série et à la pastille de la cloche.
- **Reste** : dans les fiches de caractères (`ui/Library.kt`), la clé (radical) et les pièges sont en rouge.
  C'est une convention de manuel, mais elle brouille le sens « rouge = validé ». À trancher par le propriétaire.

---

## C. Accessibilité

### 16. Contraste du texte gris ✅
- **Où** : `ui/theme/Theme.kt`.
- **Fait** : gris secondaire `#6C6C72` (5,2:1 sur blanc, 4,7:1 sur le fond gris), nuit `#98989F`
  (5,9:1). L'accent de nuit est séparé en deux : `accent` `#7D7AFF` pour le texte (4,9:1 sur carte) et
  `accentFill` `#5E5CE6` sous du texte blanc (5,1:1). Avant, le blanc sur l'accent de nuit tombait sous 4,5:1.

### 17. Zones tactiles trop petites 🟡
- **Où** : `ui/Kit.kt`, `ui/StrokeOrder.kt`, `ui/AvatarScreen.kt` (pastilles de couleur 44 dp),
  `ui/LessonsScreen.kt` (« Tout voir »).
- **Fait** : sélecteur segmenté 48 dp de haut (dessin de 36 dp), boutons ronds du haut 48 dp, bouton
  retour 48 dp, liens 48 dp, choix du caractère dans l'ordre des traits 44 → 48 dp, « Tout voir » 48 dp.
- **Reste** : pastilles de couleur de « Mon avatar » (44 dp).

### 18. Textes trop petits 🟡
- **Où** : `ui/CharactersScreen.kt` (10 sp), `ui/Library.kt`, `ui/MyWordsScreen.kt`, `ui/ProgressScreen.kt`
  (chiffres du graphique 10 sp).
- **Fait** : étiquettes des cartes de mots passées de 10 à 11 sp.
- **Reste** : `Library.kt` (« STRUCTURE » 10 sp), `MyWordsScreen.kt` (« photo » 10 sp), graphique de la semaine.

### 19. Rôles pour TalkBack ✅
- **Où** : `ui/Kit.kt`, `ui/HomeScreen.kt`, `ui/LessonsScreen.kt`, `ui/LessonScreen.kt`.
- **Fait** : grands titres annoncés comme titres (`heading`), boutons et lignes annoncés comme boutons
  (`Role.Button`), onglets du sélecteur comme onglets, choix de langue comme boutons radio, interrupteur
  du rappel comme interrupteur, bouton « Effacer » dans la recherche.

### 20. « Retour arrière » prédictif d'Android 14 ✅
- **Où** : `AndroidManifest.xml`.
- **Fait** : `enableOnBackInvokedCallback="true"` : l'aperçu du geste retour fonctionne ; les `BackHandler`
  existants continuent de gérer chaque écran.

### 21. Réduire les animations ⬜
- **Où** : `ui/Notifications.kt` (cloche qui se balance, halo), `PandaHead` (clignement), `Celebration.kt`.
- **Pourquoi** : les boucles écrites avec `delay()` ne suivent pas le réglage « Supprimer les animations »
  du téléphone.
- **Proposition** : lire `Settings.Global.ANIMATOR_DURATION_SCALE` et couper les boucles à 0.

---

## D. Débordements de texte (français, anglais, russe)

### 22. Libellés de la barre du bas en russe 🟡
- **Où** : `ui/Navigation.kt`.
- **État** : « Иероглифы » tient à 10 sp dans ~70 dp ; avec une très grande police, il est coupé (pas de
  points de suspension). Proposition : texte plus court « Знаки » si on veut de la marge.

### 23. Sélecteurs à 4 choix ✅ (vérifié)
- **Où** : `ui/LessonScreen.kt` (Cartes / Écrire / Grammaire / Quiz), `ui/CharactersScreen.kt` (filtres).
- **État** : « Грамматика » tient (13 sp dans ~88 dp) ; texte coupé proprement avec « … » sinon.

### 24. Chiffres de l'accueil ✅
- **Où** : `ui/HomeScreen.kt` (`StatsCard`).
- **Fait** : libellés courts (« Série », « Streak », « Серия »), une ligne avec « … » de secours.

### 25. Barre du haut des écrans pleins 🟡
- **Où** : `ui/Kit.kt` (`TopBar`).
- **État** : le repère de droite (« Пробный экзамен HSK 3 ») est coupé avec « … » s'il manque de place ; à
  vérifier sur un petit téléphone (360 dp).

---

## E. Performances

### 26. Calculs répétés à chaque affichage ⬜
- **Où** : `MainActivity.kt` (`srs.dueWords(reviewPool)`, `knownCount`, `masteredCount`, `dueTomorrow`
  appelés à chaque recomposition de l'onglet, sur ~600 mots ; `progress.bestScore` pour 41 leçons).
- **Pourquoi** : chaque appel relit et découpe ~600 chaînes ; pendant les transitions d'onglet (fondu),
  cela peut faire sauter des images sur un téléphone d'entrée de gamme.
- **Proposition** : calculer un instantané `remember(version) { … }` une fois par retour sur les onglets.

### 27. Recherche des caractères ✅ en partie
- **Où** : `ui/CharactersScreen.kt`.
- **Fait** : l'expression régulière qui enlève les accents n'est plus recréée ~1 800 fois par frappe.
- **Reste** : filtrer dans un `remember(query, filter, version)` plutôt qu'à chaque recomposition.

### 28. Coins continus ⬜ (à surveiller)
- **Où** : `ui/theme/Theme.kt` (`SmoothShape`).
- **Pourquoi** : une forme « tracé » est un peu plus coûteuse à découper qu'un arrondi simple, surtout dans la
  grille de ~600 mots. Rien de visible sur les photos d'écran, mais à vérifier sur un vieux téléphone.
- **Proposition** : si ça saccade, garder `SmoothShape` pour les grandes cartes et `RoundedCornerShape` dans
  la grille.

### 29. Animations infinies ⬜
- **Où** : `ui/Notifications.kt` (halo de la pastille, `rememberInfiniteTransition`).
- **Pourquoi** : redessine l'écran en continu tant qu'il y a quelque chose à faire (batterie).
- **Proposition** : jouer le halo 3 fois puis s'arrêter.

### 30 bis. Panneau du bas la nuit ✅
- **Où** : `ui/Kit.kt` (`ZhuziSheet`), `ui/theme/Theme.kt` (`sheet`).
- **Fait** : la nuit, le panneau des notifications était aussi noir que la page derrière ; il a maintenant
  un fond un cran plus clair, comme les panneaux d'iOS.

### 30. Pas de vrai flou derrière la barre du bas ⬜ (choix)
- **Où** : `ui/Navigation.kt`.
- **Pourquoi** : Compose ne sait pas flouter ce qui est derrière un élément sans bibliothèque (Haze). La barre
  est en « matière » translucide (90 %) avec un filet clair : rendu proche, sans dépendance de plus.

---

## F. Santé du code

### 31. `MainActivity` fait tout ⬜
- **Où** : `MainActivity.kt` (≈ 400 lignes, navigation par numéros négatifs `REVIEW = -1`, `EXAM_BASE = -10`…).
- **Pourquoi** : chaque nouvel écran rend la logique plus fragile (un examen HSK 4 = -14 à ajouter à la main).
- **Proposition** : une petite classe `Screen` (sealed) et un `ViewModel` qui lit les préférences une fois.

### 32. Noms complets partout ⬜
- **Où** : presque tous les fichiers `ui/*.kt` (`com.zhuzi.app.ui.theme.Accent`, `androidx.compose…`).
- **Pourquoi** : le code est difficile à lire et à relire.
- **Proposition** : des `import` en haut de fichier (aucun changement de comportement).

### 33. Pas de tests de la révision espacée ⬜
- **Où** : `data/Srs.kt`, `data/Streak.kt`.
- **Pourquoi** : seules les photos d'écran sont testées. Une erreur dans le calcul des jours casserait la
  révision de tout le monde sans que rien ne le signale.
- **Proposition** : tests unitaires simples (boîte qui monte, retour en boîte 1, série qui casse à minuit).

### 34. Mots en double entre leçons et « Mes mots » ⬜
- **Où** : `data/Srs.kt` (clé = les caractères du mot).
- **Pourquoi** : un mot ajouté dans « Mes mots » avec les mêmes caractères qu'un mot HSK partage sa boîte ;
  pas grave, mais à savoir (et à afficher : « déjà dans la leçon 3 »).

### 35. Petites incohérences de design restantes ⬜
- **Où** : `ui/Library.kt`, `ui/ExamScreen.kt`, `ui/MyWordsScreen.kt`, `ui/AvatarScreen.kt`, `ui/ToneQuiz.kt`,
  `ui/Writing.kt`.
- **Pourquoi** : ces écrans profitent du nouveau système (couleurs, boutons, sélecteur, barre du haut,
  panneaux) mais gardent quelques étiquettes en capitales et des marges de 20 dp au lieu de 16 dp.
- **Proposition** : même passe que pour Accueil / Leçons / Progrès.

---

## Ce qui a été changé sur `design-ios` (résumé)

- `ui/theme/Theme.kt` : couleurs iOS (fond groupé, cartes, remplissage translucide, accent texte/aplat),
  échelle de texte iOS en Manrope, coins continus `SmoothShape`, en-têtes de section discrets.
- `ui/Kit.kt` (nouveau) : pictogrammes, listes groupées, grand titre + barre compacte, barre de retour,
  sélecteur segmenté, panneaux à poignée, liens d'action, boutons ronds de barre d'outils.
- `ui/Components.kt` : cartes plates, boutons plein / teinté de 52 dp qui s'assombrissent à l'appui.
- Accueil, Leçons, Progrès, Caractères, panneau des notifications, leçon : refaits avec ces briques.
- Photos d'écran ajoutées : nuit (Leçons, Progrès, Caractères, carte, notifications, accueil défilé),
  réglages avec barre compacte, recherche avec bouton « Effacer ».
