#!/usr/bin/env bash
################################################################################
# BUZZ AI - Claude Code 一键安装脚本
#
# 功能: 安装 Claude Code + 配置 API + 安装余额显示插件
# 用法: curl -fsSL https://buzzai.cc/sh/claudecode.sh | bash
#
# 不需要 sudo，所有操作在用户目录下完成
################################################################################

set -euo pipefail
export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8

################################################################################
# 常量定义
################################################################################

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
GRAY='\033[0;90m'
BOLD='\033[1m'
GOLD='\033[38;5;220m'
NC='\033[0m'

CLAUDE_DIR="$HOME/.claude"
SETTINGS_FILE="$CLAUDE_DIR/settings.json"
OS_TYPE=""
IS_WSL=false

################################################################################
# 工具函数
################################################################################

print_banner() {
  echo ""
  echo -e "  ${BOLD}${GOLD} ██████  ██    ██ ███████ ███████    ${WHITE} █████  ██${NC}"
  echo -e "  ${BOLD}${GOLD} ██   ██ ██    ██    ██     ██       ${WHITE}██   ██ ██${NC}"
  echo -e "  ${BOLD}${GOLD} ██████  ██    ██   ██     ██        ${WHITE}███████ ██${NC}"
  echo -e "  ${BOLD}${GOLD} ██   ██ ██    ██  ██     ██         ${WHITE}██   ██ ██${NC}"
  echo -e "  ${BOLD}${GOLD} ██████   ██████  ███████ ███████    ${WHITE}██   ██ ██${NC}"
  echo ""
  echo -e "  ${GRAY}───────────────────────────────────────────────────────${NC}"
  echo -e "  ${WHITE}Claude Code 一键安装${NC}  ${GRAY}|${NC}  ${CYAN}主站${NC} ${BOLD}${GOLD}buzzai.cc${NC}  ${GRAY}|${NC}  ${CYAN}镜像${NC} ${BOLD}${GOLD}buzzai.top${NC}"
  echo -e "  ${GRAY}───────────────────────────────────────────────────────${NC}"
  echo ""
}

print_status() {
  local type="$1"
  local message="$2"
  case "$type" in
    ok)    echo -e "  ${GREEN}[OK]${NC} $message" ;;
    info)  echo -e "  ${CYAN}[*]${NC} $message" ;;
    warn)  echo -e "  ${YELLOW}[!]${NC} $message" ;;
    error) echo -e "  ${RED}[x]${NC} $message" ;;
    *)     echo "  $message" ;;
  esac
}

detect_os() {
  local uname_s
  uname_s="$(uname -s)"
  IS_WSL=false

  case "$uname_s" in
    Darwin)
      OS_TYPE="macos"
      ;;
    Linux)
      OS_TYPE="linux"
      if grep -qiE "(microsoft|wsl)" /proc/version 2>/dev/null; then
        IS_WSL=true
      fi
      ;;
    MINGW*|MSYS*|CYGWIN*)
      OS_TYPE="windows"
      ;;
    *)
      OS_TYPE="unknown"
      print_status "warn" "未知操作系统: ${uname_s}，将尝试继续"
      ;;
  esac
}

detect_shell_configs() {
  SHELL_CONFIGS=()

  if [[ "$OS_TYPE" == "windows" ]]; then
    for f in "$HOME/.bashrc" "$HOME/.bash_profile" "$HOME/.profile"; do
      if [[ -f "$f" ]]; then
        SHELL_CONFIGS+=("$f")
      fi
    done
    if [[ ${#SHELL_CONFIGS[@]} -eq 0 ]]; then
      SHELL_CONFIGS=("$HOME/.bashrc")
    fi
  else
    for f in "$HOME/.bashrc" "$HOME/.zshrc" "$HOME/.bash_profile" "$HOME/.profile"; do
      if [[ -f "$f" ]]; then
        SHELL_CONFIGS+=("$f")
      fi
    done
    if [[ ${#SHELL_CONFIGS[@]} -eq 0 ]]; then
      if [[ "$OS_TYPE" == "macos" ]]; then
        SHELL_CONFIGS=("$HOME/.zshrc")
      else
        SHELL_CONFIGS=("$HOME/.bashrc")
      fi
    fi
  fi
}

native_path() {
  local p="$1"
  if [[ "$OS_TYPE" == "windows" ]] && command -v cygpath >/dev/null 2>&1; then
    cygpath -m "$p"
  else
    echo "$p"
  fi
}

sed_inplace() {
  if [[ "$OS_TYPE" == "macos" ]]; then
    sed -i '' "$@"
  else
    sed -i "$@"
  fi
}

set_env_var() {
  local var_name="$1"
  local var_value="$2"

  export "$var_name=$var_value"

  for config_file in "${SHELL_CONFIGS[@]}"; do
    if [[ -f "$config_file" ]]; then
      sed_inplace "/^export ${var_name}=/d" "$config_file" 2>/dev/null || true
    fi
    echo "export ${var_name}=\"${var_value}\"" >> "$config_file"
  done

  if [[ "$OS_TYPE" == "windows" ]]; then
    if command -v setx.exe >/dev/null 2>&1; then
      setx.exe "$var_name" "$var_value" >/dev/null 2>&1 || true
    elif command -v setx >/dev/null 2>&1; then
      setx "$var_name" "$var_value" >/dev/null 2>&1 || true
    fi
  fi
}

read_input() {
  local prompt="$1"
  local varname="$2"
  local silent="${3:-false}"

  echo -en "  $prompt" >&2
  if [[ "$silent" == "true" ]]; then
    read -rs "$varname" < /dev/tty
    echo "" >&2
  else
    read -r "$varname" < /dev/tty
  fi
}

# Detect available JSON runtime (for merge_json and other operations)
# Returns: "jq", "node", "python3", "python", or ""
detect_json_runtime() {
  if command -v jq >/dev/null 2>&1; then
    echo "jq"
  elif command -v node >/dev/null 2>&1; then
    echo "node"
  elif command -v python3 >/dev/null 2>&1; then
    echo "python3"
  elif command -v python >/dev/null 2>&1; then
    echo "python"
  else
    echo ""
  fi
}

# Safely merge a key-value pair into a JSON file.
# Uses temp files for value passing to avoid all shell escaping issues.
# All branches use native_path for Windows compatibility.
merge_json() {
  local file="$1"
  local key="$2"
  local value="$3"
  local native_file
  native_file="$(native_path "$file")"

  local json_rt
  json_rt="$(detect_json_runtime)"

  if [[ -z "$json_rt" ]]; then
    print_status "error" "无法合并 JSON (需要 jq/node/python3/python 之一)"
    return 1
  fi

  # Write value to temp file to avoid shell escaping issues entirely
  local val_tmp
  val_tmp="$(mktemp)"
  printf '%s' "$value" > "$val_tmp"
  local native_val_tmp
  native_val_tmp="$(native_path "$val_tmp")"

  local success=false

  case "$json_rt" in
    jq)
      local out_tmp
      out_tmp="$(mktemp)"
      if jq --argjson val "$value" ".$key = \$val" "$native_file" > "$out_tmp" 2>/dev/null; then
        mv "$out_tmp" "$native_file"
        success=true
      else
        rm -f "$out_tmp"
        print_status "warn" "jq 合并失败，回退到其他方式 ..."
        # Fallback: try node or python
        if command -v node >/dev/null 2>&1; then
          json_rt="node"
        elif command -v python3 >/dev/null 2>&1; then
          json_rt="python3"
        elif command -v python >/dev/null 2>&1; then
          json_rt="python"
        fi
      fi
      ;;
  esac

  if [[ "$success" == "false" && ("$json_rt" == "node") ]]; then
    # Use temp file for value to avoid any quoting issues
    if node -e "
      const fs = require('fs');
      const filePath = fs.readFileSync('${native_val_tmp}', 'utf8').trim();
      void filePath; // unused, value is read below
      const data = JSON.parse(fs.readFileSync('${native_file}', 'utf8'));
      const val = JSON.parse(fs.readFileSync('${native_val_tmp}', 'utf8'));
      data['${key}'] = val;
      fs.writeFileSync('${native_file}', JSON.stringify(data, null, 2) + '\n');
    " 2>/dev/null; then
      success=true
    else
      print_status "warn" "Node 合并失败 ..."
    fi
  fi

  if [[ "$success" == "false" && ("$json_rt" == "python3" || "$json_rt" == "python") ]]; then
    if $json_rt -c "
import json, sys
try:
    with open('${native_file}', 'r', encoding='utf-8') as f:
        data = json.load(f)
    with open('${native_val_tmp}', 'r', encoding='utf-8') as f:
        val = json.load(f)
    data['${key}'] = val
    with open('${native_file}', 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
        f.write('\n')
except Exception as e:
    print(f'Error: {e}', file=sys.stderr)
    sys.exit(1)
" 2>/dev/null; then
      success=true
    else
      print_status "warn" "Python 合并失败 ..."
    fi
  fi

  rm -f "$val_tmp"

  if [[ "$success" == "true" ]]; then
    return 0
  fi

  print_status "error" "所有 JSON 合并方式均失败"
  return 1
}

################################################################################
# 功能 1: 安装 Claude Code
################################################################################

check_claude_installed() {
  if command -v claude >/dev/null 2>&1; then
    local ver
    ver="$(claude --version 2>/dev/null || echo "unknown")"
    echo "$ver"
    return 0
  fi
  return 1
}

install_claude_code() {
  echo ""
  print_status "info" "正在安装 Claude Code ..."
  echo ""

  if [[ "$IS_WSL" == "true" ]]; then
    print_status "info" "检测到 WSL 环境"
  fi
  if [[ "$OS_TYPE" == "windows" ]]; then
    print_status "info" "检测到 Windows (Git Bash) 环境"
  fi

  local ver
  if ver="$(check_claude_installed)"; then
    print_status "ok" "Claude Code 已安装 ($ver)"
    echo ""
    local answer
    echo -en "  ${YELLOW}是否重新安装? (y/N): ${NC}" >&2
    read -r answer < /dev/tty
    if [[ "$answer" != "y" && "$answer" != "Y" ]]; then
      print_status "info" "跳过安装"
      return 0
    fi
  fi

  if [[ "$OS_TYPE" == "windows" ]]; then
    if ! command -v npm >/dev/null 2>&1; then
      print_status "error" "未找到 npm，请先安装 Node.js: https://nodejs.org/"
      return 1
    fi
    print_status "info" "使用 npm 安装 ..."
    echo ""
    npm install -g @anthropic-ai/claude-code
  else
    print_status "info" "调用官方安装器 ..."
    echo ""
    if ! curl -fsSL https://claude.ai/install.sh | sh; then
      print_status "warn" "官方安装器失败，尝试 npm 安装 ..."
      if command -v npm >/dev/null 2>&1; then
        npm install -g @anthropic-ai/claude-code
      else
        print_status "error" "Claude Code 安装失败 (官方安装器和 npm 均不可用)"
        return 1
      fi
    fi
  fi

  echo ""
  export PATH="$HOME/.local/bin:$HOME/.claude/local/bin:$PATH"
  if ver="$(check_claude_installed)"; then
    print_status "ok" "Claude Code 安装成功 ($ver)"
  else
    print_status "warn" "安装已完成，但未在 PATH 中找到 claude"
    print_status "info" "请重新打开终端后再试"
  fi
}

################################################################################
# 功能 2: 配置 API
################################################################################

validate_api_key() {
  local base_url="$1"
  local api_key="$2"

  print_status "info" "正在验证 API Key ..."

  local http_code
  http_code="$(curl -s -o /dev/null -w '%{http_code}' \
    --max-time 10 \
    -H "x-api-key: ${api_key}" \
    -H "anthropic-version: 2023-06-01" \
    "${base_url}/v1/models" 2>/dev/null || echo "000")"

  if [[ "$http_code" == "200" ]]; then
    print_status "ok" "API Key 验证通过"
    return 0
  elif [[ "$http_code" == "401" ]]; then
    print_status "error" "API Key 无效 (401 Unauthorized)"
    return 1
  elif [[ "$http_code" == "000" ]]; then
    print_status "warn" "无法连接到 ${base_url} (网络错误)"
    return 1
  else
    print_status "warn" "API 返回状态码: ${http_code} (可能仍可用)"
    return 0
  fi
}

configure_api() {
  echo ""
  print_status "info" "配置 API 密钥和地址"
  echo ""

  local existing_key="${ANTHROPIC_API_KEY:-}"
  local existing_url="${ANTHROPIC_BASE_URL:-}"

  if [[ -n "$existing_key" ]]; then
    local masked="${existing_key:0:8}...${existing_key: -4}"
    print_status "info" "当前 API Key: ${masked}"
  fi
  if [[ -n "$existing_url" ]]; then
    print_status "info" "当前 BASE URL: ${existing_url}"
  fi
  echo ""

  local api_key=""
  echo -en "  ${WHITE}请输入 ANTHROPIC_API_KEY: ${NC}" >&2

  # Read password with * masking
  while IFS= read -r -s -n1 char < /dev/tty; do
    if [[ $char == $'\0' ]]; then
      break
    elif [[ $char == $'\177' ]] || [[ $char == $'\b' ]]; then
      if [ -n "$api_key" ]; then
        api_key="${api_key%?}"
        echo -en "\b \b" >&2
      fi
    else
      api_key+="$char"
      echo -n "*" >&2
    fi
  done
  echo "" >&2

  if [[ -z "$api_key" ]]; then
    print_status "error" "API Key 不能为空，已取消"
    return 1
  fi

  local base_url
  echo ""
  echo -e "  ${WHITE}请选择 API 地址:${NC}"
  echo -e "    ${CYAN}1)${NC} 主站 - https://buzzai.cc"
  echo -e "    ${CYAN}2)${NC} 镜像 - https://buzzai.top"
  echo ""
  echo -en "  ${WHITE}请输入选项 (1/2): ${NC}" >&2
  local choice
  read -r choice < /dev/tty

  case "$choice" in
    1)
      base_url="https://buzzai.cc"
      ;;
    2)
      base_url="https://buzzai.top"
      ;;
    *)
      print_status "error" "无效选项，已取消"
      return 1
      ;;
  esac

  echo ""
  print_status "info" "已选择: ${base_url}"
  echo ""

  if validate_api_key "$base_url" "$api_key"; then
    :
  else
    local answer
    echo -en "  ${YELLOW}验证未通过，是否仍要保存? (y/N): ${NC}" >&2
    read -r answer < /dev/tty
    if [[ "$answer" != "y" && "$answer" != "Y" ]]; then
      print_status "info" "已取消配置"
      return 1
    fi
  fi

  set_env_var "ANTHROPIC_API_KEY" "$api_key"
  set_env_var "ANTHROPIC_BASE_URL" "$base_url"

  local claude_json="$HOME/.claude.json"
  local native_claude_json
  native_claude_json="$(native_path "$claude_json")"
  if [[ ! -f "$claude_json" ]]; then
    cat > "$claude_json" << 'EOFJ'
{
  "hasCompletedOnboarding": true
}
EOFJ
    print_status "info" "已创建 ~/.claude.json"
  else
    local tmp_json
    tmp_json="$(mktemp)"
    if command -v jq >/dev/null 2>&1; then
      jq '.hasCompletedOnboarding = true' "$claude_json" > "$tmp_json" && mv "$tmp_json" "$claude_json"
    elif command -v python3 >/dev/null 2>&1; then
      python3 -c "
import json
with open('$native_claude_json', 'r') as f:
    data = json.load(f)
data['hasCompletedOnboarding'] = True
with open('$native_claude_json', 'w') as f:
    json.dump(data, f, indent=2)
    f.write('\n')
" 2>/dev/null || cat > "$claude_json" << 'EOFJ'
{
  "hasCompletedOnboarding": true
}
EOFJ
    else
      cat > "$claude_json" << 'EOFJ'
{
  "hasCompletedOnboarding": true
}
EOFJ
    fi
    print_status "info" "已更新 ~/.claude.json"
  fi

  echo ""
  local masked="${api_key:0:8}...${api_key: -4}"
  print_status "ok" "API Key 已保存: ${masked}"
  print_status "ok" "BASE URL 已保存: ${base_url}"

  for config_file in "${SHELL_CONFIGS[@]}"; do
    print_status "info" "  -> ${config_file}"
  done
}

################################################################################
# 功能 3: 安装 StatusLine 余额显示插件
################################################################################

detect_statusline_runtime() {
  if command -v node >/dev/null 2>&1; then
    echo "node"
  elif command -v python3 >/dev/null 2>&1; then
    echo "python3"
  elif command -v python >/dev/null 2>&1; then
    # Verify it's Python 3, not Python 2
    local pyver
    pyver="$(python -c 'import sys; print(sys.version_info.major)' 2>/dev/null || echo "2")"
    if [[ "$pyver" == "3" ]]; then
      echo "python"
    elif command -v jq >/dev/null 2>&1 && command -v curl >/dev/null 2>&1; then
      echo "bash"
    else
      echo ""
    fi
  elif command -v jq >/dev/null 2>&1 && command -v curl >/dev/null 2>&1; then
    echo "bash"
  else
    echo ""
  fi
}

install_statusline_script() {
  local runtime="$1"
  local script_file=""
  local script_cmd=""

  mkdir -p "$CLAUDE_DIR"

  local native_home
  native_home="$(native_path "$HOME")"

  case "$runtime" in
    node)
      script_file="$CLAUDE_DIR/statusline.js"
      script_cmd="node ${native_home}/.claude/statusline.js"
      cat > "$script_file" << 'NODESCRIPT'
#!/usr/bin/env node
const https = require("https");
const http = require("http");
const fs = require("fs");
const os = require("os");
const path = require("path");

const ALLOWED_HOSTS = ["buzzai.cc", "buzzai.top"];
const ALLOWED_SUFFIX = ".buzz7.top";
const CACHE_FILE = path.join(os.tmpdir(), "buzzai-balance-cache.json");
const CACHE_MAX_AGE = 60;

const GREEN = "\x1b[32m";
const YELLOW = "\x1b[33m";
const RED = "\x1b[31m";
const BOLD = "\x1b[1m";
const GOLD = "\x1b[38;5;220m";
const RESET = "\x1b[0m";
const LOGO = `${BOLD}${GOLD}BUZZ \u00b7 AI${RESET}`;

/**
 * Resolve API config with multi-source fallback:
 *   1. Environment variables (ANTHROPIC_API_KEY / ANTHROPIC_AUTH_TOKEN)
 *   2. ~/.claude/settings.json -> env field
 *   3. ~/.claude/settings.local.json -> env field
 */
function getConfig() {
  let apiKey = process.env.ANTHROPIC_API_KEY
    || process.env.ANTHROPIC_AUTH_TOKEN
    || "";
  let baseUrl = (process.env.ANTHROPIC_BASE_URL || "").replace(/\/+$/, "");

  if (apiKey && baseUrl) return { apiKey, baseUrl };

  // Fallback: read from settings files
  const candidates = [
    path.join(os.homedir(), ".claude", "settings.json"),
    path.join(os.homedir(), ".claude", "settings.local.json"),
  ];
  for (const settingsPath of candidates) {
    if (apiKey && baseUrl) break;
    try {
      const settings = JSON.parse(fs.readFileSync(settingsPath, "utf8"));
      const env = settings.env || {};
      if (!apiKey) {
        apiKey = env.ANTHROPIC_API_KEY || env.ANTHROPIC_AUTH_TOKEN || "";
      }
      if (!baseUrl) {
        baseUrl = (env.ANTHROPIC_BASE_URL || "").replace(/\/+$/, "");
      }
    } catch {}
  }

  return { apiKey, baseUrl };
}

function isAllowedHost(baseUrl) {
  try {
    const host = new URL(baseUrl).hostname;
    if (ALLOWED_HOSTS.includes(host)) return true;
    if (host.endsWith(ALLOWED_SUFFIX)) return true;
    return false;
  } catch { return false; }
}

function fetchJson(url, apiKey) {
  return new Promise((resolve) => {
    const mod = url.startsWith("https") ? https : http;
    const req = mod.get(url, {
      headers: { Authorization: `Bearer ${apiKey}`, "Content-Type": "application/json" },
      timeout: 3000,
    }, (res) => {
      let data = "";
      res.on("data", (c) => (data += c));
      res.on("end", () => { try { resolve(JSON.parse(data)); } catch { resolve(null); } });
    });
    req.on("error", () => resolve(null));
    req.on("timeout", () => { req.destroy(); resolve(null); });
  });
}

async function getBalance(baseUrl, apiKey) {
  try {
    const stat = fs.statSync(CACHE_FILE);
    if ((Date.now() - stat.mtimeMs) / 1000 < CACHE_MAX_AGE)
      return JSON.parse(fs.readFileSync(CACHE_FILE, "utf8"));
  } catch {}
  const [sub, usage] = await Promise.all([
    fetchJson(`${baseUrl}/v1/dashboard/billing/subscription`, apiKey),
    fetchJson(`${baseUrl}/v1/dashboard/billing/usage`, apiKey),
  ]);
  if (!sub || !usage) return null;
  const total = sub.hard_limit_usd || 0;
  const used = (usage.total_usage || 0) / 100;
  const remaining = total - used;
  const result = { total, used, remaining };
  try { fs.writeFileSync(CACHE_FILE, JSON.stringify(result)); } catch {}
  return result;
}

async function main() {
  let input = "";
  for await (const chunk of process.stdin) input += chunk;
  let info = {};
  try { info = JSON.parse(input); } catch {}
  const model = info.model?.display_name || "Unknown";

  const { apiKey, baseUrl } = getConfig();
  const showBalance = apiKey && baseUrl && isAllowedHost(baseUrl);

  if (showBalance) {
    const b = await getBalance(baseUrl, apiKey);
    if (b) {
      const color = b.remaining < 10 ? RED : b.remaining < 30 ? YELLOW : GREEN;
      console.log(`${LOGO} ${BOLD}${model}${RESET} | ${color}\u4f59\u989d: ${b.remaining.toFixed(2)} \u5143${RESET}`);
    } else {
      console.log(`${LOGO} ${BOLD}${model}${RESET} | \u4f59\u989d: \u67e5\u8be2\u5931\u8d25`);
    }
  } else {
    console.log(`${BOLD}${model}${RESET}`);
  }
}
main();
NODESCRIPT
      ;;

    python3|python)
      script_file="$CLAUDE_DIR/statusline.py"
      script_cmd="${runtime} ${native_home}/.claude/statusline.py"
      cat > "$script_file" << 'PYTHONSCRIPT'
#!/usr/bin/env python3
import sys, os, json, time, tempfile, urllib.request, ssl
from pathlib import Path

ALLOWED_HOSTS = ["buzzai.cc", "buzzai.top"]
ALLOWED_SUFFIX = ".buzz7.top"
CACHE_FILE = os.path.join(tempfile.gettempdir(), "buzzai-balance-cache.json")
CACHE_MAX_AGE = 60

GREEN = "\x1b[32m"
YELLOW = "\x1b[33m"
RED = "\x1b[31m"
BOLD = "\x1b[1m"
GOLD = "\x1b[38;5;220m"
RESET = "\x1b[0m"
LOGO = f"{BOLD}{GOLD}BUZZ \u00b7 AI{RESET}"


def get_config():
    """
    Resolve API config with multi-source fallback:
      1. Environment variables (ANTHROPIC_API_KEY / ANTHROPIC_AUTH_TOKEN)
      2. ~/.claude/settings.json -> env field
      3. ~/.claude/settings.local.json -> env field
    """
    api_key = os.environ.get("ANTHROPIC_API_KEY") or os.environ.get("ANTHROPIC_AUTH_TOKEN") or ""
    base_url = os.environ.get("ANTHROPIC_BASE_URL", "").rstrip("/")

    if api_key and base_url:
        return api_key, base_url

    home = Path.home()
    candidates = [
        home / ".claude" / "settings.json",
        home / ".claude" / "settings.local.json",
    ]
    for settings_path in candidates:
        if api_key and base_url:
            break
        try:
            with open(settings_path, "r", encoding="utf-8") as f:
                settings = json.load(f)
            env = settings.get("env", {})
            if not api_key:
                api_key = env.get("ANTHROPIC_API_KEY") or env.get("ANTHROPIC_AUTH_TOKEN") or ""
            if not base_url:
                base_url = env.get("ANTHROPIC_BASE_URL", "").rstrip("/")
        except Exception:
            pass

    return api_key, base_url


def is_allowed_host(base_url):
    try:
        from urllib.parse import urlparse
        host = urlparse(base_url).hostname or ""
        if host in ALLOWED_HOSTS:
            return True
        if host.endswith(ALLOWED_SUFFIX):
            return True
    except Exception:
        pass
    return False


def fetch_json(url, api_key):
    try:
        req = urllib.request.Request(url, headers={
            "Authorization": f"Bearer {api_key}",
            "Content-Type": "application/json",
        })
        ctx = ssl.create_default_context()
        with urllib.request.urlopen(req, timeout=3, context=ctx) as resp:
            return json.loads(resp.read().decode())
    except Exception:
        return None


def get_balance(base_url, api_key):
    try:
        mtime = os.path.getmtime(CACHE_FILE)
        if time.time() - mtime < CACHE_MAX_AGE:
            with open(CACHE_FILE, "r") as f:
                return json.load(f)
    except Exception:
        pass
    sub = fetch_json(f"{base_url}/v1/dashboard/billing/subscription", api_key)
    usage = fetch_json(f"{base_url}/v1/dashboard/billing/usage", api_key)
    if not sub or not usage:
        return None
    total = sub.get("hard_limit_usd", 0)
    used = usage.get("total_usage", 0) / 100
    remaining = total - used
    result = {"total": total, "used": used, "remaining": remaining}
    try:
        with open(CACHE_FILE, "w") as f:
            json.dump(result, f)
    except Exception:
        pass
    return result


def main():
    raw = sys.stdin.read()
    try:
        info = json.loads(raw)
    except Exception:
        info = {}
    model = (info.get("model") or {}).get("display_name", "Unknown")

    api_key, base_url = get_config()
    show_balance = bool(api_key and base_url and is_allowed_host(base_url))

    if show_balance:
        b = get_balance(base_url, api_key)
        if b:
            r = b["remaining"]
            color = RED if r < 10 else YELLOW if r < 30 else GREEN
            print(f"{LOGO} {BOLD}{model}{RESET} | {color}\u4f59\u989d: {r:.2f} \u5143{RESET}")
        else:
            print(f"{LOGO} {BOLD}{model}{RESET} | \u4f59\u989d: \u67e5\u8be2\u5931\u8d25")
    else:
        print(f"{BOLD}{model}{RESET}")


if __name__ == "__main__":
    main()
PYTHONSCRIPT
      ;;

    bash)
      script_file="$CLAUDE_DIR/statusline.sh"
      script_cmd="${native_home}/.claude/statusline.sh"
      cat > "$script_file" << 'BASHSCRIPT'
#!/usr/bin/env bash
INPUT="$(cat)"

MODEL="$(printf '%s' "$INPUT" | jq -r '.model.display_name // "Unknown"' 2>/dev/null || echo "Unknown")"

RESET=$'\x1b[0m'
BOLD=$'\x1b[1m'
GOLD=$'\x1b[38;5;220m'
GREEN=$'\x1b[32m'
YELLOW=$'\x1b[33m'
RED=$'\x1b[31m'
LOGO="${BOLD}${GOLD}BUZZ · AI${RESET}"

# Multi-source config resolution:
#   1. Environment variables
#   2. ~/.claude/settings.json env field (via jq)
API_KEY="${ANTHROPIC_API_KEY:-${ANTHROPIC_AUTH_TOKEN:-}}"
BASE_URL="$(printf '%s' "${ANTHROPIC_BASE_URL:-}" | sed 's|/\+$||')"

# Fallback: read from settings.json
if { [ -z "$API_KEY" ] || [ -z "$BASE_URL" ]; } && command -v jq >/dev/null 2>&1; then
  for _sf in "$HOME/.claude/settings.json" "$HOME/.claude/settings.local.json"; do
    [ -f "$_sf" ] || continue
    [ -n "$API_KEY" ] && [ -n "$BASE_URL" ] && break
    if [ -z "$API_KEY" ]; then
      API_KEY="$(jq -r '.env.ANTHROPIC_API_KEY // .env.ANTHROPIC_AUTH_TOKEN // empty' "$_sf" 2>/dev/null || true)"
    fi
    if [ -z "$BASE_URL" ]; then
      BASE_URL="$(jq -r '.env.ANTHROPIC_BASE_URL // empty' "$_sf" 2>/dev/null | sed 's|/\+$||' || true)"
    fi
  done
fi

is_allowed() {
  local host
  host="$(printf '%s' "$1" | sed -E 's|^https?://||;s|[/:].*||')"
  case "$host" in
    buzzai.cc|buzzai.top) return 0 ;;
    *.buzz7.top) return 0 ;;
    *) return 1 ;;
  esac
}

SHOW=0
[ -n "$API_KEY" ] && [ -n "$BASE_URL" ] && is_allowed "$BASE_URL" && SHOW=1

if [ "$SHOW" -eq 1 ]; then
  CACHE="/tmp/buzzai-balance-cache.json"
  NEED_REFRESH=1
  if [ -f "$CACHE" ]; then
    MTIME="$(stat -f %m "$CACHE" 2>/dev/null || stat -c %Y "$CACHE" 2>/dev/null || echo 0)"
    AGE=$(( $(date +%s) - MTIME ))
    [ "$AGE" -lt 60 ] && NEED_REFRESH=0
  fi
  if [ "$NEED_REFRESH" -eq 1 ]; then
    SUB="$(curl -sf --max-time 3 -H "Authorization: Bearer $API_KEY" "$BASE_URL/v1/dashboard/billing/subscription" 2>/dev/null || echo "")"
    USE="$(curl -sf --max-time 3 -H "Authorization: Bearer $API_KEY" "$BASE_URL/v1/dashboard/billing/usage" 2>/dev/null || echo "{}")"
    TOTAL="$(printf '%s' "$SUB" | jq -r '.hard_limit_usd // 0')"
    USED_RAW="$(printf '%s' "$USE" | jq -r '.total_usage // 0')"
    REMAINING="$(awk "BEGIN { printf \"%.2f\", ${TOTAL} - ${USED_RAW}/100 }")"
    printf '{"remaining":%s}' "$REMAINING" > "$CACHE" 2>/dev/null || true
  fi
  REMAINING="$(jq -r '.remaining // 0' "$CACHE" 2>/dev/null || echo "0")"
  COLOR="$GREEN"
  awk "BEGIN { exit (${REMAINING}+0 < 10) ? 0 : 1 }" && COLOR="$RED"
  awk "BEGIN { exit (${REMAINING}+0 >= 10 && ${REMAINING}+0 < 30) ? 0 : 1 }" && COLOR="$YELLOW"
  if [ "$REMAINING" = "0" ] && [ ! -f "$CACHE" ]; then
    echo "${LOGO} ${BOLD}${MODEL}${RESET} | 余额: 查询失败"
  else
    echo "${LOGO} ${BOLD}${MODEL}${RESET} | ${COLOR}余额: ${REMAINING} 元${RESET}"
  fi
else
  echo "${BOLD}${MODEL}${RESET}"
fi
BASHSCRIPT
      ;;
  esac

  chmod +x "$script_file"
  echo "$script_cmd"
}

# Write statusLine config into settings.json with robust error handling.
# Won't silently crash under set -e; provides clear guidance on failure.
configure_statusline_settings() {
  local script_cmd="$1"

  mkdir -p "$CLAUDE_DIR"

  if [[ ! -f "$SETTINGS_FILE" ]]; then
    echo '{}' > "$SETTINGS_FILE"
  fi

  local value="{\"type\": \"command\", \"command\": \"${script_cmd}\"}"

  # Try merge_json first (preserves existing settings)
  if merge_json "$SETTINGS_FILE" "statusLine" "$value" 2>/dev/null; then
    return 0
  fi

  # Fallback: read existing, merge in memory, write back via node
  print_status "warn" "merge_json 失败，尝试备用方案 ..."
  local native_file
  native_file="$(native_path "$SETTINGS_FILE")"

  if command -v node >/dev/null 2>&1; then
    # Use a here-document to pass script_cmd, avoiding all quoting issues
    local tmp_cmd
    tmp_cmd="$(mktemp)"
    printf '%s' "$script_cmd" > "$tmp_cmd"
    local native_tmp_cmd
    native_tmp_cmd="$(native_path "$tmp_cmd")"

    if node -e "
      const fs = require('fs');
      const fp = '${native_file}';
      const cmd = fs.readFileSync('${native_tmp_cmd}', 'utf8').trim();
      let data = {};
      try { data = JSON.parse(fs.readFileSync(fp, 'utf8')); } catch {}
      data.statusLine = { type: 'command', command: cmd };
      fs.writeFileSync(fp, JSON.stringify(data, null, 2) + '\n');
    " 2>/dev/null; then
      rm -f "$tmp_cmd"
      return 0
    fi
    rm -f "$tmp_cmd"
  fi

  # Last resort: tell user exactly what to do
  print_status "error" "自动写入 settings.json 失败"
  echo ""
  print_status "info" "请手动编辑 ${SETTINGS_FILE}，添加以下内容:"
  echo ""
  echo -e "    ${WHITE}\"statusLine\": {${NC}"
  echo -e "    ${WHITE}  \"type\": \"command\",${NC}"
  echo -e "    ${WHITE}  \"command\": \"${script_cmd}\"${NC}"
  echo -e "    ${WHITE}}${NC}"
  echo ""
  return 1
}

# Detect existing statusline installation and its runtime
detect_existing_statusline() {
  local found=""
  if [[ -f "$CLAUDE_DIR/statusline.js" ]]; then
    found="node"
  elif [[ -f "$CLAUDE_DIR/statusline.py" ]]; then
    found="python"
  elif [[ -f "$CLAUDE_DIR/statusline.sh" ]]; then
    found="bash"
  fi
  echo "$found"
}

# Check if the installed statusline script is an old version (lacks getConfig/get_config)
is_old_statusline_version() {
  local existing_rt="$1"
  case "$existing_rt" in
    node)
      # Old version reads process.env.ANTHROPIC_API_KEY directly without getConfig()
      if grep -q "getConfig" "$CLAUDE_DIR/statusline.js" 2>/dev/null; then
        return 1  # new version
      fi
      return 0  # old version
      ;;
    python)
      if grep -q "get_config" "$CLAUDE_DIR/statusline.py" 2>/dev/null; then
        return 1
      fi
      return 0
      ;;
    bash)
      # Old version doesn't read from settings.json
      if grep -q "settings.json" "$CLAUDE_DIR/statusline.sh" 2>/dev/null; then
        return 1
      fi
      return 0
      ;;
  esac
  return 0
}

# Clean up old statusline scripts when switching runtime
cleanup_old_statusline() {
  local new_runtime="$1"
  local old_runtime="$2"

  if [[ -z "$old_runtime" || "$new_runtime" == "$old_runtime" ]]; then
    return
  fi

  # Map runtime to script extension
  local old_file=""
  case "$old_runtime" in
    node)   old_file="$CLAUDE_DIR/statusline.js" ;;
    python) old_file="$CLAUDE_DIR/statusline.py" ;;
    bash)   old_file="$CLAUDE_DIR/statusline.sh" ;;
  esac

  if [[ -n "$old_file" && -f "$old_file" ]]; then
    rm -f "$old_file"
    print_status "info" "已清理旧版脚本: $(basename "$old_file")"
  fi
}

install_statusline() {
  echo ""
  print_status "info" "安装余额显示插件 (StatusLine)"
  echo ""

  mkdir -p "$CLAUDE_DIR"

  # Detect existing installation
  local existing_rt
  existing_rt="$(detect_existing_statusline)"
  local is_upgrade=false

  if [[ -n "$existing_rt" ]]; then
    if is_old_statusline_version "$existing_rt"; then
      is_upgrade=true
      print_status "warn" "检测到旧版插件 (${existing_rt})，将自动升级"
    else
      print_status "info" "检测到已安装的插件 (${existing_rt})，将重新安装"
    fi
  fi

  local runtime
  runtime="$(detect_statusline_runtime)"

  if [[ -z "$runtime" ]]; then
    print_status "error" "未找到可用的运行环境 (需要 node/python3/bash+jq+curl 之一)"
    return 1
  fi

  print_status "info" "[1/3] 检测到运行环境: ${runtime}"

  # Clean up old script if runtime changed
  cleanup_old_statusline "$runtime" "$existing_rt"

  local script_cmd
  script_cmd="$(install_statusline_script "$runtime")"
  print_status "ok" "[2/3] 脚本已创建"

  if configure_statusline_settings "$script_cmd"; then
    print_status "ok" "[3/3] 配置已写入: ${SETTINGS_FILE}"
  else
    print_status "warn" "[3/3] 自动配置失败，请按上述提示手动配置"
    return 0
  fi

  # Clear balance cache so fresh data is fetched after upgrade
  rm -f "/tmp/buzzai-balance-cache.json" 2>/dev/null || true

  echo ""
  if [[ "$is_upgrade" == "true" ]]; then
    print_status "ok" "插件已从旧版升级到新版，重启 Claude Code 即可生效"
    print_status "info" "新版改进: 支持从 settings.json 读取 API 配置，无需设置环境变量"
  else
    print_status "ok" "余额插件安装完成，重启 Claude Code 即可生效"
  fi
}

################################################################################
# 状态检测与菜单
################################################################################

show_status_summary() {
  echo -e "  ${WHITE}当前状态:${NC}"

  local os_label="$OS_TYPE"
  if [[ "$IS_WSL" == "true" ]]; then
    os_label="linux (WSL)"
  fi
  echo -e "    系统环境:     ${CYAN}${os_label}${NC}"

  local claude_ver
  if claude_ver="$(check_claude_installed 2>/dev/null)"; then
    echo -e "    Claude Code:  ${GREEN}已安装${NC} (${claude_ver})"
  else
    echo -e "    Claude Code:  ${RED}未安装${NC}"
  fi

  if [[ -n "${ANTHROPIC_API_KEY:-}" ]]; then
    local masked="${ANTHROPIC_API_KEY:0:8}...${ANTHROPIC_API_KEY: -4}"
    echo -e "    API 配置:     ${GREEN}已配置${NC} (${masked})"
  else
    echo -e "    API 配置:     ${RED}未配置${NC}"
  fi

  if [[ -f "$SETTINGS_FILE" ]] && grep -q "statusLine" "$SETTINGS_FILE" 2>/dev/null; then
    echo -e "    余额插件:     ${GREEN}已安装${NC}"
  else
    echo -e "    余额插件:     ${RED}未安装${NC}"
  fi

  echo ""
}

show_menu() {
  print_banner
  show_status_summary

  echo -e "  ${WHITE}请选择操作:${NC}"
  echo -e "    ${CYAN}1)${NC} 安装 Claude Code"
  echo -e "    ${CYAN}2)${NC} 配置 API 密钥"
  echo -e "    ${CYAN}3)${NC} 安装余额显示插件"
  echo -e "    ${CYAN}4)${NC} 一键全部安装"
  echo -e "    ${CYAN}0)${NC} 退出"
  echo ""
}

run_all() {
  install_claude_code
  echo ""
  echo -e "  ${BOLD}--------------------------------------${NC}"
  configure_api
  echo ""
  echo -e "  ${BOLD}--------------------------------------${NC}"
  install_statusline
}

################################################################################
# 入口
################################################################################

main() {
  detect_os
  detect_shell_configs

  for config_file in "${SHELL_CONFIGS[@]}"; do
    source "$config_file" 2>/dev/null || true
  done

  if [[ "$OS_TYPE" == "windows" ]]; then
    if [[ -z "${ANTHROPIC_API_KEY:-}" ]] && command -v cmd.exe >/dev/null 2>&1; then
      local win_key
      win_key="$(cmd.exe /C "echo %ANTHROPIC_API_KEY%" 2>/dev/null | tr -d '\r')" || true
      if [[ -n "$win_key" && "$win_key" != "%ANTHROPIC_API_KEY%" ]]; then
        export ANTHROPIC_API_KEY="$win_key"
      fi
      local win_url
      win_url="$(cmd.exe /C "echo %ANTHROPIC_BASE_URL%" 2>/dev/null | tr -d '\r')" || true
      if [[ -n "$win_url" && "$win_url" != "%ANTHROPIC_BASE_URL%" ]]; then
        export ANTHROPIC_BASE_URL="$win_url"
      fi
    fi
  fi

  while true; do
    show_menu

    local choice
    echo -en "  ${WHITE}请输入数字: ${NC}" >&2
    read -r choice < /dev/tty

    case "$choice" in
      1) install_claude_code ;;
      2) configure_api ;;
      3) install_statusline ;;
      4) run_all ;;
      0)
        echo ""
        print_status "ok" "再见!"
        exit 0
        ;;
      *)
        print_status "warn" "无效选项，请重试"
        ;;
    esac

    echo ""
    echo -en "  按 Enter 继续 ..." >&2
    read -r < /dev/tty
  done
}

main
