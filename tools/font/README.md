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
`python3 tools/font/glyph_path.py 竹 54 54 40 [police]` (caractère, centre x, centre y, taille, police : `wenkai.ttf` par défaut).
Le logo (竹 au pinceau + sceau 子) vient de **Ma Shan Zheng** (`npm pack @fontsource/ma-shan-zheng`) et **Noto Serif SC** gras (`@fontsource/noto-serif-sc`), toutes deux sous licence OFL ; seuls les tracés sont dans l’appli.
