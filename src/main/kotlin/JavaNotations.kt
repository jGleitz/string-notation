package de.joshuagleitze.stringnotation

import java.util.*
import java.util.stream.IntStream
import javax.lang.model.SourceVersion

/**
 * A notation for Java type names. This notation is like [UpperCamelCase], but when [printing][StringNotation.print], it will drop any
 * character that is not allowed in a Java identifier. If the result is a Java keyword, `_` will be appended to it.
 *
 * Allowed characters are determined using [Character.isJavaIdentifierStart] and [Character.isJavaIdentifierPart]. Keywords are detected
 * using [SourceVersion.isKeyword].
 */
object JavaTypeName: StringNotation by UpperCamelCase {
	override fun print(word: Word) = UpperCamelCase.print(word).makeValidJavaIdentifier()

	override fun toString() = this::class.java.simpleName!!
}

/**
 * A notation for java member names. This notation is like [LowerCamelCase], but when [printing][StringNotation.print], it will drop any
 * character that is not allowed in a Java identifier. If the result is a Java keyword, `_` will be appended to it.
 *
 * Allowed characters are determined using [Character.isJavaIdentifierStart] and [Character.isJavaIdentifierPart]. Keywords are detected
 * using [SourceVersion.isKeyword].
 */
object JavaMemberName: BaseStringNotation(camelCaseSplitRegex) {
	override fun transformPartAfterParse(index: Int, part: String) = part.toLowerCase(Locale.ROOT)

	override fun print(word: Word) = word.parts
		.foldIndexed(StringBuffer()) { index, left, right ->
			val rightPart =
				if (left.contains(Regex("[a-zA-Z]"))) right.toFirstUpperOtherLowerCase()
				else right.toLowerCase()
			left.append(printBeforePart(index, rightPart)).append(rightPart)
		}.toString().makeValidJavaIdentifier()
}

/**
 * A notation for java package parts. When [printing][StringNotation.print], it simply concatenates all word parts and drops any character
 * that is not allowed in a Java identifier. If the result is a Java keyword, `_` will be appended to it. When
 * [parsing][StringNotation.parse], the notation will recognise word parts both in the [LowerCamelCase] and the [SnakeCase] notation.
 * However, neither notation is conventional and parsing will usually yield only one word part on real-world inputs.
 *
 * Allowed characters are determined using [Character.isJavaIdentifierStart] and [Character.isJavaIdentifierPart]. Keywords are detected
 * using [SourceVersion.isKeyword].
 */
object JavaPackagePart: BaseStringNotation(Regex("_|${camelCaseSplitRegex.pattern}")) {
	override fun transformPartAfterParse(index: Int, part: String) = part.toLowerCase(Locale.ROOT)

	override fun transformPartToPrint(index: Int, part: String) = part.toLowerCase(Locale.ROOT)

	override fun print(word: Word) = super.print(word).makeValidJavaIdentifier()
}

/**
 * A notation for whole java packages. When [printing][StringNotation.print] parts, it will drop any character that is not allowed in a Java
 * identifier. If the result is a Java keyword, `_` will be appended to it.
 *
 * Allowed characters are determined using [Character.isJavaIdentifierStart] and [Character.isJavaIdentifierPart]. Keywords are detected
 * using [SourceVersion.isKeyword].
 */
object JavaPackageName: BaseStringNotation(Regex("\\.")) {
	override fun transformPartToPrint(index: Int, part: String) = part.toLowerCase(Locale.ROOT).makeValidJavaIdentifier()

	override fun printBeforeInnerPart(index: Int, part: String) = "."
}

/**
 * A notation for `static final` fields in Java. This notation is like [ScreamingSnakeCase], but when [printing][StringNotation.print], it
 * will drop any character that is not allowed in a Java identifier.  If the result is a Java keyword, `_` will be appended to it.
 *
 * Allowed characters are determined using [Character.isJavaIdentifierStart] and [Character.isJavaIdentifierPart]. Keywords are detected
 * using [SourceVersion.isKeyword].
 */
object JavaConstantName: StringNotation by ScreamingSnakeCase {
	override fun print(word: Word) = ScreamingSnakeCase.print(word).makeValidJavaIdentifier()

	override fun toString() = this::class.java.simpleName!!
}

private fun String.makeValidJavaIdentifier() = this.keepOnlyJavaIdentifierChars().neutralizeJavaReservedKeywords().ifEmpty { "__" }

private fun String.keepOnlyJavaIdentifierChars() = this.chars()
	.skipWhile { !Character.isJavaIdentifierStart(it) }
	.filter { Character.isJavaIdentifierPart(it) }
	.collect({ StringBuilder() }, { left, right -> left.appendCodePoint(right) }, { left, right -> left.append(right) })
	.toString()

private fun String.neutralizeJavaReservedKeywords() = if (SourceVersion.isKeyword(this)) this + "_" else this

private inline fun IntStream.skipWhile(crossinline condition: (Int) -> Boolean): IntStream {
	var found = false
	return this.filter {
		if (!found) {
			found = !condition(it)
		}
		found
	}
}

