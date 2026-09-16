package net.industrialgrid;

import org.patryk3211.powergrid.electricity.wire.WireItem;

/**
 * Just a WireItem - all the "can't burn out" behaviour comes from the
 * extreme thermalMass/maximumCurrent values in its wire_types datapack
 * entry (see data/industrialgrid/powergrid/wire_types/industrial_cable.json).
 */
public class IndustrialCableItem extends WireItem {
    public IndustrialCableItem(Properties settings) {
        super(settings);
    }
}
