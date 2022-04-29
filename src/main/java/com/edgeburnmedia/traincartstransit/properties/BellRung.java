package com.edgeburnmedia.traincartstransit.properties;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.properties.api.ITrainProperty;

import java.util.Optional;

public class BellRung implements ITrainProperty<Boolean> {
	/**
	 * Gets the default value of this property. If the current value of the
	 * property equals this value, then the property is not written to the
	 * configuration. As well, this default is used during initialization
	 * of new properties.
	 *
	 * @return default property value
	 */
	@Override
	public Boolean getDefault() {
		return false;
	}

	/**
	 * Tries to read the value of this property from a given YAML configuration.
	 * The configuration could be of a train, cart or train default. If the value
	 * is not stored in the configuration, {@link Optional#empty()} should be returned.<br>
	 * <br>
	 * If the property is unset because it is the default value, then empty needs to be
	 * returned anyway. Applying the default in this situation is done automatically.
	 *
	 * @param config YAML configuration from which to read
	 * @return read value, or {@link Optional#empty()} if none was read
	 */
	@Override
	public Optional<Boolean> readFromConfig(ConfigurationNode config) {
		return Util.getConfigOptional(config, "bellRung", Boolean.TYPE);
	}

	/**
	 * Updates the value of this property in the given YAML configuration.
	 * The configuration could be of a train, cart or train default. If the value
	 * is {@link Optional#isPresent()}, the value should be written to the configuration.
	 * If it is not present, the relevant configuration entries need to be
	 * removed from the configuration.<br>
	 * <br>
	 * This method is generally called with {@link Optional#empty()} when the current
	 * value is the default one.
	 *
	 * @param config YAML configuration to which to write
	 * @param value  The value to write when {@link Optional#isPresent()}, otherwise
	 *               the property should be removed from the configuration node
	 */
	@Override
	public void writeToConfig(ConfigurationNode config, Optional<Boolean> value) {
		Util.setConfigOptional(config, "bellRung", value);
	}
}
