import glob, re, sys
from fontTools.ttLib import TTFont
from fontTools import subset
from fontTools.merge import Merger
src, kt, out = sys.argv[1], sys.argv[2], sys.argv[3]
# kt : un fichier .kt, ou un dossier (tous ses .kt : mots, exemples, proverbes…)
files = [kt] if kt.endswith('.kt') else sorted(glob.glob(kt + '/*.kt'))
text = ''.join(open(f, encoding='utf-8').read() for f in files)
# Caractères chinois + ponctuation chinoise, et ceux de l'interface (onglets, sceaux)
chars = set(c for c in text if '一' <= c <= '鿿' or '　' <= c <= '〿' or '＀' <= c <= '￯')
chars |= set('竹子记家学字好')
print(len(chars), 'caractères')
need = {}
for f in sorted(glob.glob(src + '/lxgwwenkai-regular-subset-*.woff2')):
    cmap = TTFont(f).getBestCmap()
    hit = {c for c in chars if ord(c) in cmap and c not in sum(need.values(), [])}
    if hit: need[f] = list(hit)
missing = chars - set(sum(need.values(), []))
print('fichiers', len(need), 'manquants', missing)
parts = []
for i, (f, cs) in enumerate(need.items()):
    font = TTFont(f)
    opts = subset.Options(); opts.flavor = None; opts.layout_features = []
    sub = subset.Subsetter(opts); sub.populate(text=''.join(cs)); sub.subset(font)
    p = f'{out}.part{i}.ttf'; font.flavor = None; font.save(p); parts.append(p)
merged = Merger().merge(parts)
merged.save(out)
print('ok', len(merged.getBestCmap()))
