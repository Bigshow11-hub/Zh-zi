# Ordre des traits

`app/src/main/assets/strokes.json` contient, pour chaque caractère de l'appli, le contour de chaque trait
(`s`, chemins SVG) et sa ligne médiane dans le sens d'écriture (`m`, points), dans un carré de 1024
(axe y vers le haut, à afficher avec y' = 900 − y).

Source : projet **Make Me a Hanzi**, paquet npm `hanzi-writer-data` (2.0.1), sous licence publique Arphic
(voir `ARPHICPL.TXT`). Après avoir ajouté des mots, refaire le fichier :

```bash
mkdir -p /tmp/hw && cd /tmp/hw && npm pack hanzi-writer-data && tar xzf hanzi-writer-data-*.tgz
```
puis, depuis la racine du dépôt : `python3 tools/strokes/build_strokes.py /tmp/hw/package`
(il lit les caractères de `Hsk1.kt` et regroupe `package/<caractère>.json` en un seul fichier :
`{"字": {"s": [...], "m": [...]}}`).
