# Police pinceau de Zhúzi

`app/src/main/res/font/wenkai.ttf` est la police **LXGW WenKai** (licence SIL OFL, voir `OFL-LXGW-WenKai.txt`),
réduite aux seuls caractères des mots de l'appli pour rester légère.

**Après avoir ajouté des mots**, refaire la police :

```bash
pip install fonttools brotli
mkdir -p /tmp/wk && cd /tmp/wk && npm pack lxgw-wenkai-webfont && tar xzf lxgw-wenkai-webfont-*.tgz
python3 tools/font/subset_wenkai.py /tmp/wk/package/files app/src/main/java/com/zhuzi/app/data/Hsk1.kt app/src/main/res/font/wenkai.ttf
```
(lancer la dernière commande depuis la racine du dépôt ; si les mots sont dans plusieurs fichiers, les concaténer d'abord.)

`glyph_path.py` sort le tracé d'un caractère au format `pathData` Android (utilisé pour l'icône) :
`python3 tools/font/glyph_path.py 竹 54 54 40` (caractère, centre x, centre y, taille), à lancer à côté de `wenkai.ttf`.
