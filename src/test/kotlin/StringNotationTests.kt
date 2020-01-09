package de.joshuagleitze.stringnotation

import ch.tutteli.atrium.api.cc.en_GB.property
import ch.tutteli.atrium.api.cc.en_GB.returnValueOf
import ch.tutteli.atrium.api.cc.en_GB.toBe
import ch.tutteli.atrium.verbs.expect
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(PER_CLASS)
abstract class StringNotationTest(
	private val notation: StringNotation,
	private val unchangedWords: Map<String, List<String>>,
	private val parseOnlyWords: Map<String, List<String>> = mapOf(),
	private val printOnlyWords: Map<String, List<String>> = mapOf()
) {
	@ParameterizedTest(name = "\"{0}\" -> {1}")
	@MethodSource("parseWords")
	fun `parses words correctly`(input: String, expectedPartList: List<String>) {
		expect(input.inNotation(notation)) {
			property(Word::partsList).toBe(expectedPartList)
		}
	}

	@ParameterizedTest(name = "{1} -> \"{0}\"")
	@MethodSource("printWords")
	fun `prints words correctly`(word: String, partList: List<String>) {
		expect(Word(partList)) {
			returnValueOf(subject::toNotation, notation).toBe(word)
		}
	}

	@ParameterizedTest(name = "\"{0}\"")
	@MethodSource("unchangedWords")
	fun `parsing and printing a word written in this notation does not change the word`(word: String) {
		expect(word) {
			returnValueOf(subject::inNotation, notation) {
				returnValueOf(subject::toNotation, notation).toBe(word)
			}
		}
	}

	private fun parseWords() = asWordArguments(unchangedWords + parseOnlyWords)
	private fun printWords() = asWordArguments(unchangedWords + printOnlyWords)
	private fun unchangedWords() = asWordArguments(unchangedWords)

	private fun asWordArguments(mappings: Map<String, List<String>>) = mappings.entries.map { arguments(it.key, it.value) }
}

class UpperCamelCaseTest: StringNotationTest(
	notation = UpperCamelCase,
	unchangedWords = mapOf("ImInUpperCamelCase" to listOf("im", "in", "upper", "camel", "case"))
)

class LowerCamelCaseTest: StringNotationTest(
	notation = LowerCamelCase,
	unchangedWords = mapOf("imInLowerCamelCase" to listOf("im", "in", "lower", "camel", "case"))
)

class ScreamingSnakeCaseTest: StringNotationTest(
	notation = ScreamingSnakeCase,
	unchangedWords = mapOf("IM_IN_SCREAMING_SNAKE_CASE" to listOf("im", "in", "screaming", "snake", "case")),
	parseOnlyWords = mapOf("im_iN_sNAKe_cASE_with_CAPItals" to listOf("im", "in", "snake", "case", "with", "capitals"))
)

class SnakeCaseTest: StringNotationTest(
	notation = SnakeCase,
	unchangedWords = mapOf(
		"im_in_snake_case" to listOf("im", "in", "snake", "case"),
		"im_iN_sNAKe_cASE_with_CAPItals" to listOf("im", "iN", "sNAKe", "cASE", "with", "CAPItals")
	)
)

class JavaTypeNameTest: StringNotationTest(
	notation = JavaTypeName,
	unchangedWords = mapOf("ImInJavaTypeNotation" to listOf("im", "in", "java", "type", "notation")),
	printOnlyWords = mapOf(
		"ImusingBadCharacters" to listOf("I’m using", "Bad", "chaRacters!")
	)
)

class JavaMemberNameTest: StringNotationTest(
	notation = JavaMemberName,
	unchangedWords = mapOf("imInJavaMemberNotation" to listOf("im", "in", "java", "member", "notation")),
	printOnlyWords = mapOf(
		"imusingBadCharacters" to listOf("I’m using", "Bad", "chaRacters!")
	)
)

class NormalWordsTest: StringNotationTest(
	notation = NormalWords,
	unchangedWords = mapOf("I’m using normal words noTation!" to listOf("I’m", "using", "normal", "words", "noTation!")),
	parseOnlyWords = mapOf(
		"I’m     using tabs\nand\r other fancy    whitespace!" to listOf(
			"I’m", "using", "tabs", "and", "other", "fancy", "whitespace!"
		)
	)

)
