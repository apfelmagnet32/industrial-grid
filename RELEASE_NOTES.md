# Industrial Grid v1.0.2

## Added

- **Industrial Transformer** — a sibling of Power Grid's own Transformer
  block, built for players pushing extreme voltages. Power Grid's stock
  transformer is capped at 60 turns per coil and only 1,000 W / 4.0 kg of
  thermal headroom, so it overheats and explodes long before it can step up
  to voltages anywhere near 1,000,000 V. The Industrial Transformer uses the
  same coil-winding mechanic (right-click with a wire to set turns, same
  turns-ratio physics) but allows up to 1,000,000 turns per coil, a
  near-ideal coupling factor to minimize heat loss, and vastly more thermal
  headroom, so it survives producing extreme voltages.

---

# Industrial Grid v1.0.1

## Fixed

- **Invalid modId on NeoForge**: the mod failed to load with
  `InvalidModFileException: Invalid modId found : industrial-grid` because
  NeoForge does not allow hyphens in a mod id. Renamed the mod id/namespace
  from `industrial-grid` to `industrialgrid` everywhere (resource
  namespace, item/block ids, translation keys, `MOD_ID` constant). No
  gameplay changes.

---

# Industrial Grid v1.0.0

Addon for Create: Power Grid. Available for NeoForge 1.21.1 and Forge 1.20.1
(Power Grid 0.6.1).

## Added

- **Industrial Cable** — an armored wire that practically never overloads or
  burns out under any realistic current. Connects directly to every Power
  Grid device terminal, including ones normally restricted to "light wires"
  (Spark Gap, Creative Voltage Source).
- **High-Voltage Gauge** — measures up to 2,000,000 V, four switchable
  ranges (2 kV / 20 kV / 200 kV / 2000 kV).
- **High-Current Gauge** — measures up to 200,000,000 A, seven switchable
  ranges.
- **High-Power Gauge** — measures up to 200,000,000 W, five switchable
  ranges.
- **Industrial Breaker** — a kinetic (shaft-powered) circuit breaker with
  five fixed trip-current settings: 1,000 / 5,000 / 10,000 / 100,000 /
  1,000,000 A. Trips open instantly if the current through it exceeds the
  selected setting.
- **Industrial HV Switch** — a full two-part kinetic switch (charge via
  shaft, toggle via redstone pulse, arcs if opened under load), rated for
  1,000,000,000 A before it overheats and fails.

## Notes

- No source code from Power Grid is included; this mod only compiles
  against its public API and data-driven `wire_types` format.
- Power Grid and Create must be installed separately — neither is bundled.
- Licensed under the Use-Restriction-License (URL); see `LICENSE`.
