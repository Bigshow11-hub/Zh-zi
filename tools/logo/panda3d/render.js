const { chromium } = require('playwright');
(async () => {
  const b = await chromium.launch({ args: ['--use-gl=angle', '--use-angle=swiftshader', '--enable-unsafe-swiftshader'] });
  const p = await b.newPage({ viewport: { width: 1024, height: 1024 }, deviceScaleFactor: 1 });
  p.on('console', m => console.log('console:', m.text()));
  p.on('pageerror', e => console.log('error:', e.message));
  await p.goto('http://localhost:8765/index.html' + (process.argv[3] || ''));
  await p.waitForFunction('window.done === true', null, { timeout: 120000 });
  const el = await p.$('canvas');
  await el.screenshot({ path: process.argv[2], omitBackground: true });
  await b.close();
})();
