# Industrial Grid

Ein kleiner Addon-Mod für [Create: Power Grid](https://github.com/patryk3211/PowerGrid),
der ein neues Kabel hinzufügt: das **Industrial Cable** — ein Panzerkabel mit
Hochspannungs-Warnstreifen-Design, das praktisch nicht überlasten/durchbrennen
kann.

Zwei Varianten, je nach Minecraft-Version:

| Ordner | Minecraft | Loader | Power Grid Version |
|---|---|---|---|
| `neoforge-1.21.1/` | 1.21.1 | NeoForge | 0.6.1 |
| `forge-1.20.1/` | 1.20.1 | Forge | 0.6.1 |

Beides sind eigenständige Gradle-Projekte.

## Wie das "unzerstörbar" funktioniert

Power Grid registriert Kabel rein datenbasiert über die `wire_types`-Registry
(`data/<mod>/powergrid/wire_types/<item>.json`) — jedes Item mit so einem
Eintrag wird automatisch als Kabel erkannt, ganz ohne Java-Hacks in Power Grid
selbst. Unser Eintrag setzt `thermalMass` und `maximumCurrent` auf extrem
hohe Werte, wodurch das Kabel bei jeder realistischen Stromlast praktisch nie
die Überhitzungs-Temperatur erreicht — genau der Mechanismus, über den normale
Power-Grid-Kabel im Spiel tatsächlich "kaputt gehen" (durchbrennen).

**Einschränkung:** Das Kabel wird trotzdem physisch entfernt, wenn ein
Spieler es absichtlich mit der Drahtschere durchschneidet (das ist eine
Spieleraktion, kein "Kaputtgehen"), und es ist nicht explosionsimmun wie
Bedrock — das würde einen Mixin-Eingriff direkt in Power Grids `WireEntity`
erfordern, was pro Minecraft-Version sehr wartungsintensiv und riskant wäre.
Was der Mod löst: das Kabel überlastet/verbrennt nie durch zu hohen Strom,
egal wie viel Energie durchfließt.

## Rezept

```
[Netherite-Schrott] [Netherite-Schrott] [Netherite-Schrott]
[Netherite-Schrott] [Power Grid Eisendraht] [Netherite-Schrott]
[Netherite-Schrott] [Netherite-Schrott] [Netherite-Schrott]
```
→ 1x Industrial Cable

## Bauen

Jedes Unterprojekt braucht das jeweilige Power-Grid-Jar (liegt schon unter
`libs/` im Projekt) nur zum Kompilieren — es wird nicht mitgebündelt. Create
und Power Grid müssen beim Spielen selbst installiert sein.

```
cd neoforge-1.21.1 && ./gradlew build
# bzw.
cd forge-1.20.1 && ./gradlew build
```

Das fertige Jar landet in `build/libs/`.

## Lizenz-Hinweis

Dieser Mod steht unter der eigenen **Use-Restriction-License (URL)** (siehe
[`LICENSE`](LICENSE)) — Nutzung/Weitergabe/Content mit sichtbarem Link zur
offiziellen Seite erlaubt, keine Ownership-Claims, keine Redistribution ohne
Link.

Er verwendet keinen Quellcode aus Power Grid, referenziert aber dessen
öffentliche `WireItem`-API und das Datapack-Format `wire_types` (Power Grid
steht unter Apache License 2.0). Power Grid selbst wird nicht mitgeliefert
und muss separat installiert werden.
