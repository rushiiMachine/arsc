package dev.rushii.arsc.internal

/**
 * Internal API annotation that forces consumers to use @OptIn.
 * All internal APIs (i.e. classes, fields, methods that are not pure) should be public with this annotation attached instead.
 */
@RequiresOptIn(
	level = RequiresOptIn.Level.ERROR,
	message = "This is an internal API. Changes that break binary compatibility may occur without any prior warning or documentation.",
)
public annotation class ArscInternalApi
