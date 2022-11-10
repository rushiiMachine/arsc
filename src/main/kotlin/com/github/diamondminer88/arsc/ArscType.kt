package com.github.diamondminer88.arsc

public data class ArscType(
    var id: UInt,
    var name: ArscTypeName,
    var configs: List<ArscConfig>,
    var specs: ArscSpecs,
)

public typealias ArscTypeName = String
