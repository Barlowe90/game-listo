const fs = require('fs');
const path = require('path');
function walk(dir) {
  let results = [];
  const list = fs.readdirSync(dir);
  list.forEach(file => {
    file = dir + '/' + file;
    const stat = fs.statSync(file);
    if (stat && stat.isDirectory()) { 
      results = results.concat(walk(file));
    } else { 
      if(file.endsWith('.tsx') || file.endsWith('.ts')) results.push(file);
    }
  });
  return results;
}
const files = walk('c:/Users/rique/Documents/GitHub/game-listo/frontend/src/app');
files.forEach(f => {
  const content = fs.readFileSync(f, 'utf8');
  if(content.includes('httpClient') || content.includes('api.') || content.includes('fetch(') || content.includes('useQuery')) {
    console.log('--- ' + f);
    const lines = content.split('\n');
    lines.forEach((l, i) => {
      if(l.includes('httpClient') || l.includes('api.') || l.includes('fetch(') || l.includes('useQuery')) {
        console.log(i + ': ' + l.trim());
      }
    });
  }
});
