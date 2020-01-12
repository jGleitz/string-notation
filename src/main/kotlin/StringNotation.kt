package de.joshuagleitze.stringnotation

import java.lang.Character.isJavaIdentifierPart
import java.lang.Character.isJavaIdentifierStart
import java.util.*
import java.util.stream.IntStream

/**
 * A convention for representing [Words][Word].
 */
interface StringNotation {
	/**
	 * Transforms an input string that is in `this` notation into a notation-agnostic [Word].
	 */
	fun parse(sourceString: String): Word

	/**
	 * Transforms a [Word] into a string that is formatted according to `this` notation.
	 */
	fun print(word: Word): String
}


