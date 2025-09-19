const fs = require('fs');
const path = require('path');

// 주석 제거 함수
function removeComments(content, filePath) {
  let lines = content.split('\n');
  let result = [];
  let inMultiLineComment = false;
  let inTemplate = false;
  let inScript = false;
  
  for (let i = 0; i < lines.length; i++) {
    let line = lines[i];
    let originalLine = line;
    
    // Vue 파일의 섹션 감지
    if (line.trim().startsWith('<template>')) {
      inTemplate = true;
      inScript = false;
    } else if (line.trim().startsWith('</template>')) {
      inTemplate = false;
    } else if (line.trim().startsWith('<script')) {
      inScript = true;
      inTemplate = false;
    } else if (line.trim().startsWith('</script>')) {
      inScript = false;
    }
    
    // HTML 주석 제거 (template 섹션에서만)
    if (inTemplate && line.includes('<!--')) {
      // HTML 주석이 한 줄에 완전히 포함된 경우
      if (line.includes('-->')) {
        line = line.replace(/<!--.*?-->/g, '');
      } else {
        // 여러 줄 HTML 주석 시작
        inMultiLineComment = true;
        line = line.replace(/<!--.*$/, '');
      }
    } else if (inTemplate && inMultiLineComment) {
      if (line.includes('-->')) {
        // 여러 줄 HTML 주석 끝
        inMultiLineComment = false;
        line = line.replace(/^.*?-->/, '');
      } else {
        // HTML 주석 내부 라인
        line = '';
      }
    }
    
    // JavaScript 주석 제거 (script 섹션에서만)
    if (inScript) {
      // API 관련 주석은 보존
      if (line.includes('TODO:') || line.includes('API') || line.includes('백엔드')) {
        // API 관련 주석은 그대로 유지
      } else {
        // 한 줄 주석 제거
        line = line.replace(/\/\/.*$/, '');
      }
    }
    
    // CSS 주석 제거 (style 섹션에서만)
    if (line.trim().startsWith('<style')) {
      // style 섹션 시작
    } else if (line.trim().startsWith('</style>')) {
      // style 섹션 끝
    } else if (line.includes('/*') && line.includes('*/')) {
      // 한 줄 CSS 주석
      line = line.replace(/\/\*.*?\*\//g, '');
    }
    
    // 빈 줄이 아닌 경우만 추가
    if (line.trim() !== '' || originalLine.trim() === '') {
      result.push(line);
    }
  }
  
  return result.join('\n');
}

// 파일 처리 함수
function processFile(filePath) {
  try {
    const content = fs.readFileSync(filePath, 'utf8');
    const processedContent = removeComments(content, filePath);
    
    if (content !== processedContent) {
      fs.writeFileSync(filePath, processedContent, 'utf8');
      console.log(`Processed: ${filePath}`);
    }
  } catch (error) {
    console.error(`Error processing ${filePath}:`, error.message);
  }
}

// 디렉토리 재귀 탐색
function processDirectory(dirPath) {
  const items = fs.readdirSync(dirPath);
  
  for (const item of items) {
    const fullPath = path.join(dirPath, item);
    const stat = fs.statSync(fullPath);
    
    if (stat.isDirectory()) {
      processDirectory(fullPath);
    } else if (item.endsWith('.vue') || item.endsWith('.js') || item.endsWith('.css')) {
      processFile(fullPath);
    }
  }
}

// src 디렉토리부터 시작
const srcDir = path.join(__dirname, 'src');
if (fs.existsSync(srcDir)) {
  processDirectory(srcDir);
  console.log('주석 제거 완료!');
} else {
  console.error('src 디렉토리를 찾을 수 없습니다.');
}
