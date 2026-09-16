# Industrial Grid

A small addon mod for [Create: Power Grid](https://github.com/patryk3211/PowerGrid)
that adds a new cable: the **Industrial Cable** — an armored cable with a
high-voltage hazard-stripe design that practically never overloads/burns out.

Two variants, depending on Minecraft version:

| Folder | Minecraft | Loader | Power Grid Version |
|---|---|---|---|
| `neoforge-1.21.1/` | 1.21.1 | NeoForge | 0.6.1 |
| `forge-1.20.1/` | 1.20.1 | Forge | 0.6.1 |

Both are standalone Gradle projects.

## How the "indestructible" behavior works

Power Grid registers cables purely through data, via the `wire_types`
registry (`data/<mod>/powergrid/wire_types/<item>.json`) — any item with
such an entry is automatically recognized as a cable, with no Java hacks in
Power Grid itself. Our entry sets `thermalMass` and `maximumCurrent` to
extremely high values, so the cable practically never reaches the overheat
temperature under any realistic current load — exactly the mechanism
through which normal Power Grid cables actually "burn out" in-game.

**Limitation:** the cable can still be physically removed if a player
deliberately cuts it with wire cutters (that's a player action, not
"burning out"), and it is not explosion-immune like Bedrock — that would
require a Mixin directly into Power Grid's `WireEntity`, which would be
high-maintenance and risky across Minecraft versions. What the mod does
solve: the cable never overloads/burns out from excessive current, no
matter how much energy flows through it.

## Recipe

```
[Netherite Scrap] [Netherite Scrap] [Netherite Scrap]
[Netherite Scrap] [Power Grid Iron Wire] [Netherite Scrap]
[Netherite Scrap] [Netherite Scrap] [Netherite Scrap]
```
→ 1x Industrial Cable

## Building

Each subproject needs the respective Power Grid jar (already included
under `libs/` in the project) only to compile against — it is not bundled.
Create and Power Grid must be installed separately to actually play.

```
cd neoforge-1.21.1 && ./gradlew build
# or
cd forge-1.20.1 && ./gradlew build
```

The finished jar lands in `build/libs/`.

## License notice

This mod is under its own **Use-Restriction-License (URL)** (see
[`LICENSE`](LICENSE)) — use/redistribution/content with a visible link to
the official page is permitted, no ownership claims, no redistribution
without a link.

It does not use any source code from Power Grid, but does reference its
public `WireItem` API and the `wire_types` data-pack format (Power Grid is
licensed under Apache License 2.0). Power Grid itself is not bundled and
must be installed separately.
