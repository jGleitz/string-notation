package de.joshuagleitze.stringnotation

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
	parseOnlyWords = listOf("im_iN_sNAKe_cASE_with_CAPItals" to Word("im", "in", "snake", "case", "with", "capitals")),
	printOnlyWords = listOf(Word("im", "iN", "sNAKe", "cASE", "with", "CAPItals") to "IM_IN_SNAKE_CASE_WITH_CAPITALS")
)

class SnakeCaseTest: BaseNotationTest(
	notation = SnakeCase,
	unchangedWords = listOf(
		"im_in_snake_case" to Word("im", "in", "snake", "case"),
		"im_iN_sNAKe_cASE_with_CAPItals" to Word("im", "iN", "sNAKe", "cASE", "with", "CAPItals")
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
