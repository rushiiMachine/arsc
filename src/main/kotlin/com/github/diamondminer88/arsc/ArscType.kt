package com.github.diamondminer88.arsc

public data class ArscType(
	var id: UInt,
	var name: ArscTypeName,
	var configs: List<ArscConfig>,
	var specs: ArscSpecs?, // TODO: figure out what to do about the null here
)

public typealias ArscTypeName = String
