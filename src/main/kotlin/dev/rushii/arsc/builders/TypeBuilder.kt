package dev.rushii.arsc.builders

import dev.rushii.arsc.*

/**
 * Gets or creates a new resource type based on the name to modify with [block].
 * Generates an ID automatically.
 */
public fun ArscPackage.type(name: ArscTypeName, block: ArscType.() -> Unit): ArscType {
	val type = types.computeIfAbsent(name) {
		ArscType(
			id = highestTypeId() + 1U,
			name = name,
			configs = mutableListOf(),
			specs = null,
		)
	}

	block(type)

	if (type.specs == null)
		throw IllegalArgumentException("No specs defined for new type")

	if (type.name.isEmpty())
		throw IllegalArgumentException("Type cannot have an empty name")

	if (type.id < 1U)
		throw IllegalArgumentException("Id cannot be lower than 1")

	return type
}

public fun ArscType.spec(id: UInt? = null, block: ArscSpecs.Spec.() -> Unit): ArscSpecs.Spec {
	val specs = specs ?: run {
		specs = ArscSpecs(0u, mutableMapOf())
		specs!!
	}

	val targetId = (id ?: specs.highestSpecId()) + 1U

	val spec = specs.specs.computeIfAbsent(targetId) {
		dev.rushii.arsc.ArscSpecs.Spec(
			id = targetId,
			flags = 0u,
		)
	}

	block(spec)

	if (spec.id < 1U)
		throw IllegalArgumentException("Id cannot be lower than 1")

	return spec
}

// public fun ArscType.config(id: ArscConfig.ConfigId, block: ArscConfig.() -> Unit): ArscConfig {
// 	val config = configs.find { it.configId == id }
// 		?: ArscConfig(0u, id, mutableListOf())
// 			.also { configs += it }
//
// 	block(config)
// 	// if (this.specs?.specs?.keys?.toSortedSet() != config.resources.map { it.specId }.toSortedSet()) {
// 	// 	throw IllegalStateException("Config resources do not match defined specs")
// 	// }
// }
