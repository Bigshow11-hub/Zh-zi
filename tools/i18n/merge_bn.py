"""Assemble les traductions bengali : chaque lot <groupe>.json (liste anglaise) + <groupe>.bn.tsv (« n<TAB>texte »)
s'ajoute à tools/i18n/bn.json (anglais → bengali), puis copie dans app/src/main/assets/i18n/bn.json.
Usage : python3 merge_bn.py <dossier des lots> <groupe>…"""
import json, pathlib, sys

HERE = pathlib.Path(__file__).parent
store = HERE / 'bn.json'
data = json.loads(store.read_text(encoding='utf-8')) if store.exists() else {}
src = pathlib.Path(sys.argv[1])
for g in sys.argv[2:]:
    en = json.loads((src / f'{g}.json').read_text(encoding='utf-8'))
    n = 0
    for line in (src / f'{g}.bn.tsv').read_text(encoding='utf-8').splitlines():
        if '\t' not in line: continue
        i, bn = line.split('\t', 1)
        key = en[int(i)]
        bn = bn.strip().replace('\\n', '\n')
        if not bn: continue
        # on garde les espaces de début et de fin du texte anglais (morceaux de phrases)
        lead = key[:len(key) - len(key.lstrip())]
        trail = key[len(key.rstrip()):]
        data[key] = lead + bn + trail
        n += 1
    print(g, n, '/', len(en))
store.write_text(json.dumps(data, ensure_ascii=False, indent=0, sort_keys=True), encoding='utf-8')
out = HERE.parents[1] / 'app/src/main/assets/i18n/bn.json'
out.parent.mkdir(parents=True, exist_ok=True)
out.write_text(json.dumps(data, ensure_ascii=False, separators=(',', ':')), encoding='utf-8')
print('total', len(data))
