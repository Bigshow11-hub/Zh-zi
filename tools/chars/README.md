# Fiches des caractères

`app/src/main/java/com/zhuzi/app/data/Chars.kt` est fabriqué par `build_chars.py` :

- décomposition, clé et type (pictogramme, idéogramme, idéo-phonogramme) : **Make Me a Hanzi**
  (`dictionary.txt`, licence publique Arphic, voir `tools/strokes/ARPHICPL.TXT`) ;
- sens et astuce en français, anglais et russe : `glosses.txt` (écrits pour l'appli) ;
- nom des clés : `radicals.txt`.

Après avoir ajouté des mots :

```bash
curl -sL -o /tmp/dictionary.txt https://raw.githubusercontent.com/skishore/makemeahanzi/master/dictionary.txt
python3 tools/chars/build_chars.py /tmp/dictionary.txt   # dit quel caractère manque dans glosses.txt
```
puis refaire la police (`tools/font/README.md`).
