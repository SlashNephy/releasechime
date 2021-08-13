# releasechime

🔔 Simple GitHub releases &amp; commits notifier which supports Discord Webhook

[![Kotlin](https://img.shields.io/badge/Kotlin-1.5-blue)](https://kotlinlang.org)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/SlashNephy/releasechime)](https://github.com/SlashNephy/releasechime/releases)
[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/SlashNephy/releasechime/Docker)](https://hub.docker.com/r/slashnephy/releasechime)
[![Docker Image Size (tag)](https://img.shields.io/docker/image-size/slashnephy/releasechime/latest)](https://hub.docker.com/r/slashnephy/releasechime)
[![Docker Pulls](https://img.shields.io/docker/pulls/slashnephy/releasechime)](https://hub.docker.com/r/slashnephy/releasechime)
[![license](https://img.shields.io/github/license/SlashNephy/releasechime)](https://github.com/SlashNephy/releasechime/blob/master/LICENSE)
[![issues](https://img.shields.io/github/issues/SlashNephy/releasechime)](https://github.com/SlashNephy/releasechime/issues)
[![pull requests](https://img.shields.io/github/issues-pr/SlashNephy/releasechime)](https://github.com/SlashNephy/releasechime/pulls)

[![screenshot.png](https://i.imgur.com/6pmT6U6.png)](https://github.com/SlashNephy/releasechime)

## Requirements

- Java 11 or later

## Docker

There are some images.

- `slashnephy/releasechime:latest`  
  Automatically published every push to `master` branch.
- `slashnephy/releasechime:dev`  
  Automatically published every push to `dev` branch.
- `slashnephy/releasechime:<version>`  
  Coresponding to release tags on GitHub.

`docker-compose.yml`

```yaml
version: '3.8'

services:
  releasechime:
    container_name: releasechime
    image: slashnephy/releasechime:latest
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
      TARGET_RELEASE_REPOSITORIES: 'SlashNephy/saya:dev'
      TARGET_RELEASE_REPOSITORIES2: 'mirakc/mirakc'
      # コミットを監視したい対象のリポジトリ (複数指定可能)
      TARGET_COMMIT_REPOSITORIES: 'DBCTRADO/TVTest'
      # コミットを監視したい対象のリポジトリ (複数指定可能)
      # 指定されたパスを含む変更のみ通知される
      TARGET_PATH_COMMIT_REPOSITORIES: 'l3tnun/EPGStation,doc'  # l3tnun/EPGStation の doc 以下の変更のみ通知

volumes:
  data:
    driver: local
```
