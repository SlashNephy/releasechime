# releasechime

🔔 Simple GitHub releases &amp; commits notifier which supports Discord Webhook

[![Kotlin](https://img.shields.io/badge/Kotlin-1.x-blue)](https://kotlinlang.org)
[![license](https://img.shields.io/github/license/SlashNephy/releasechime)](https://github.com/SlashNephy/releasechime/blob/master/LICENSE)
[![issues](https://img.shields.io/github/issues/SlashNephy/releasechime)](https://github.com/SlashNephy/releasechime/issues)
[![pull requests](https://img.shields.io/github/issues-pr/SlashNephy/releasechime)](https://github.com/SlashNephy/releasechime/pulls)

[![screenshot.png](https://i.imgur.com/6pmT6U6.png)](https://github.com/SlashNephy/releasechime)

## Requirements

- Java 17 or newer

## Docker

`docker-compose.yml`

```yaml
version: '3.8'

services:
  releasechime:
    image: ghcr.io/slashnephy/releasechime:master
    restart: always
    volumes:
      - data:/app/data
    environment:
      # 必須: GitHub Private Access Token
      GITHUB_TOKEN: xxx
      # Discord Webhook URL (複数指定可能)
      DISCORD_WEBHOOK_URLS: https://discord.com/api/webhooks/xxx
      DISCORD_WEBHOOK_URLS2: https://discord.com/api/webhooks/xxx

      # チェック間隔 (秒)
      INTERVAL_SEC: 1800
      # ログレベル (OFF, ERROR, WARN, INFO, DEBUG, TRACE, ALL)
      LOG_LEVEL: 'TRACE'

      # リリースを監視したい対象のリポジトリ (複数指定可能)
      TARGET_RELEASE_REPOSITORIES: 'SlashNephy/saya'
      TARGET_RELEASE_REPOSITORIES2: 'mirakc/mirakc'
      # コミットを監視したい対象のリポジトリ (複数指定可能, ブランチ指定可能)
      TARGET_COMMIT_REPOSITORIES: 'DBCTRADO/TVTest'
      # コミットを監視したい対象のリポジトリ (複数指定可能, ブランチ指定可能)
      # 指定されたパスを含む変更のみ通知される
      TARGET_PATH_COMMIT_REPOSITORIES: 'l3tnun/EPGStation:v2,doc'  # l3tnun/EPGStation の doc 以下の変更のみ通知

volumes:
  data:
    driver: local
```
