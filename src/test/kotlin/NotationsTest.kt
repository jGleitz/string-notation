package de.joshuagleitze.stringnotation

import ch.tutteli.atrium.api.cc.en_GB.property
import ch.tutteli.atrium.api.cc.en_GB.returnValueOf
import ch.tutteli.atrium.api.cc.en_GB.toBe
import ch.tutteli.atrium.verbs.expect
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(PER_CLASS)
abstract class BaseNotationTest(
	private val notation: StringNotation,
	private val unchangedWords: List<Pair<String, Word>>,
	private val parseOnlyWords: List<Pair<String, Word>> = emptyList(),
	private val printOnlyWords: List<Pair<Word, String>> = emptyList()
) {
	@ParameterizedTest(name = "\"{0}\" -> {1}")
	@MethodSource("parseWords")
	fun `parses words correctly`(input: String, expectedWord: Word) {
		expect(input.fromNotation(notation)) {
			property(Word::partsList).toBe(expectedWord.partsList)
		}
	}

	@ParameterizedTest(name = "{1} -> \"{0}\"")
	@MethodSource("printWords")
	fun `prints words correctly`(sourceWord: Word, expectedResult: String) {
		expect(sourceWord) {
			returnValueOf(subject::toNotation, notation).toBe(expectedResult)
		}
	}

	@ParameterizedTest(name = "\"{0}\"")
	@MethodSource("unchangedWords")
	fun `parsing and printing a word written in this notation does not change the word`(word: String) {
		expect(word) {
			returnValueOf(subject::fromNotation, notation) {
				returnValueOf(subject::toNotation, notation).toBe(word)
			}
		}
	}

	private fun parseWords() = asArguments(unchangedWords + parseOnlyWords)
	private fun printWords() = asArguments(unchangedWords.map { it.swap() } + printOnlyWords)
	private fun unchangedWords() = asArguments(unchangedWords)

	private fun asArguments(pairs: List<Pair<*, *>>) = pairs.map { arguments(it.first, it.second) }

	private fun <First, Second> Pair<First, Second>.swap() = Pair(this.second, this.first)
}

class UpperCamelCaseTest: BaseNotationTest(
	notation = UpperCamelCase,
	unchangedWords = listOf("ImInUpperCamelCase" to Word("im", "in", "upper", "camel", "case")),
	printOnlyWords = listOf(
		Word("removes", "upperCase") to "RemovesUppercase"
	)
)

class LowerCamelCaseTest: BaseNotationTest(
	notation = LowerCamelCase,
	unchangedWords = listOf("imInLowerCamelCase" to Word("im", "in", "lower", "camel", "case")),
	printOnlyWords = listOf(
		Word("removes", "upperCase") to "removesUppercase"
	)
)

class ScreamingSnakeCaseTest: BaseNotationTest(
	notation = ScreamingSnakeCase,
	unchangedWords = listOf("IM_IN_SCREAMING_SNAKE_CASE" to Word("im", "in", "screaming", "snake", "case")),
	parseOnlyWords = listOf("im_iN_sNAKe_cASE_with_CAPItals" to Word("im", "in", "snake", "case", "with", "capitals"))
)

class SnakeCaseTest: BaseNotationTest(
	notation = SnakeCase,
	unchangedWords = listOf(
		"im_in_snake_case" to Word("im", "in", "snake", "case"),
		"im_iN_sNAKe_cASE_with_CAPItals" to Word("im", "iN", "sNAKe", "cASE", "with", "CAPItals")
	)
)

class JavaTypeNameTest: BaseNotationTest(
	notation = JavaTypeName,
	unchangedWords = listOf("ImInJavaTypeNotation" to Word("im", "in", "java", "type", "notation")),
	printOnlyWords = listOf(
		Word("I’m using", "Bad", "chaRacters!") to "ImusingBadCharacters",
		Word("1", "type", "name", "4", "you") to "TypeName4You",
		Word("removes", "upperCase") to "RemovesUppercase"
	)
)

class JavaMemberNameTest: BaseNotationTest(
	notation = JavaMemberName,
	unchangedWords = listOf("imInJavaMemberNotation" to Word("im", "in", "java", "member", "notation")),
	printOnlyWords = listOf(
		Word("I’m using", "Bad", "chaRacters!") to "imusingBadCharacters",
		Word("1", "Member", "name", "4", "you") to "memberName4You",
		Word("_", "underscore", "start") to "_underscoreStart",
		Word("$", "dollar", "start") to "\$dollarStart",
		Word("a", "letter", "start") to "aLetterStart",
		Word("removes", "upperCase") to "removesUppercase"
	)
)

class NormalWordsTest: BaseNotationTest(
	notation = NormalWords,
	unchangedWords = listOf("I’m using normal words noTation!" to Word("I’m", "using", "normal", "words", "noTation!")),
	parseOnlyWords = listOf(
		"I’m     using tabs\nand\r other fancy    whitespace!" to Word(
			"I’m", "using", "tabs", "and", "other", "fancy", "whitespace!"
		)
	)
)
