# Logo Zhúzi : figurine panda 3D

Le panda de l'avatar, modelé en 3D (three.js) comme un jouet verni, qui tient une tige de bambou.
Choisi par le propriétaire (option B : fond encre).

- `index.html` : la scène 3D (volumes, matières vernies, lumières, ombre douce).
- `render.js` : la photographie avec le Chromium de Playwright → `render.png` (1024 × 1024, fond transparent).
- `compose.py` : fabrique les icônes Android de chaque densité (`drawable-*dpi/`) :
  `ic_launcher_foreground.png`, `ic_launcher_monochrome.png` (icônes à thème) et `ic_notification.png`
  (silhouette blanche, yeux et nez évidés). Le fond encre est `drawable/ic_launcher_background.xml`.

Refaire le logo après une retouche de la scène :

```bash
cd tools/logo/panda3d && npm install three@0.169.0
python3 -m http.server 8765 &            # sert la scène
node render.js render.png                # rendu 3D
cd ../../.. && python3 tools/logo/panda3d/compose.py /tmp/apercu   # icônes + aperçu
```
