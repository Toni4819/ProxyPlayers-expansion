# ProxyPlayers Expansion

A [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) expansion that shows the player count of one or more backend servers, or of the entire proxy. Compatible with BungeeCord, Waterfall, and Velocity.

## Placeholders

| Placeholder | Result |
|---|---|
| `%proxyplayers_total%` | All players on the proxy |
| `%proxyplayers_server:<server>%` | Players on one backend |
| `%proxyplayers_server:<server1>,<server2>%` | Sum of the listed backends |

The `server:` prefix is required, so a backend named `total` is still reachable via `%proxyplayers_server:total%`.

## Installation

1. Drop the jar into `plugins/PlaceholderAPI/expansions/`.
2. Run `/papi reload`.
3. Check with `/papi list`.

## Requirements

- Spigot / Paper 1.13+ with PlaceholderAPI 2.11+
- At least 1 player online on the backend (plugin messaging limitation)
- Velocity: `bungee-plugin-message-channel = true` in `velocity.toml` (default)

## Notes

- Values refresh every 2 seconds; a server shows `0` for about 2 seconds after its first use.
- A misspelled placeholder is left as-is instead of showing a misleading `0`.

## AI Disclosure

This project was written with the assistance of an AI (Claude, by Anthropic). The code was reviewed and **tested: it works**.

## License
MIT
