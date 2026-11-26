import sys
from fontTools.ttLib import TTFont
from fontTools.pens.basePen import BasePen
class P(BasePen):
    def __init__(s, gs): super().__init__(gs); s.o=[]
    def f(s,*pts): return ' '.join('%s,%s'%(n(x),n(y)) for x,y in pts)
    def _moveTo(s,p): s.o.append('M'+s.f(p))
    def _lineTo(s,p): s.o.append('L'+s.f(p))
    def _qCurveToOne(s,a,b): s.o.append('Q'+s.f(a,b))
    def _curveToOne(s,a,b,c): s.o.append('C'+s.f(a,b,c))
    def _closePath(s): s.o.append('Z')
    def getCommands(s): return ''.join(s.o)
def n(v): return ('%.2f'%v).rstrip('0').rstrip('.')
from fontTools.pens.transformPen import TransformPen
from fontTools.pens.boundsPen import BoundsPen
f = TTFont('wenkai.ttf'); gs = f.getGlyphSet(); name = f.getBestCmap()[ord(sys.argv[1])]
bp = BoundsPen(gs); gs[name].draw(bp); x0, y0, x1, y1 = bp.bounds
cx, cy, size = float(sys.argv[2]), float(sys.argv[3]), float(sys.argv[4])  # centre et taille cible
s = size / max(x1 - x0, y1 - y0)
dx = cx - (x0 + x1) / 2 * s; dy = cy + (y0 + y1) / 2 * s
sp = P(gs)
gs[name].draw(TransformPen(sp, (s, 0, 0, -s, dx, dy)))
print(sp.getCommands())
