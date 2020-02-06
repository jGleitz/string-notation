package de.joshuagleitze.stringnotation

class JavaTypeNameTest: BaseNotationTest(
	notation = JavaTypeName,
	unchangedWords = listOf("ImInJavaTypeNotation" to Word("im", "in", "java", "type", "notation")),
	printOnlyWords = listOf(
		Word("I’m using", "Bad", "chaRacters!") to "ImusingBadCharacters",
		Word("1", "type", "name", "4", "you") to "TypeName4You",
		Word("removes", "upperCase") to "RemovesUppercase",
		Word("") to "__",
		Word("1") to "__",
		Word("8if") to "If",
		Word("enum") to "Enum",
		Word("_") to "__" ifJvmVersionIsAtLeast 9
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
		Word("removes", "upperCase") to "removesUppercase",
		Word("") to "__",
		Word("1") to "__",
		Word("8if") to "if_",
		Word("enum") to "enum_",
		Word("_") to "__" ifJvmVersionIsAtLeast 9
	)
)

class JavaPackagePartTest: BaseNotationTest(
	notation = JavaPackagePart,
	unchangedWords = listOf(
		"imapackagename" to Word("imapackagename")
	),
	parseOnlyWords = listOf(
		"withCamelCase" to Word("with", "camel", "case"),
		"with_snake_case" to Word("with", "snake", "case"),
		"withCamelAnd_snake_case" to Word("with", "camel", "and", "snake", "case"),
		"if" to Word("if")
	),
	printOnlyWords = listOf(
		Word("") to "__",
		Word("1") to "__",
		Word("8if") to "if_",
		Word("enum") to "enum_",
		Word("_") to "__" ifJvmVersionIsAtLeast 9
	)
)

class JavaPackageNameTest: BaseNotationTest(
	notation = JavaPackageName,
	unchangedWords = listOf("i.am.a.packagename" to Word("i", "am", "a", "packagename")),
	parseOnlyWords = listOf(
		"wIth.CAPITALS" to Word("wIth", "CAPITALS"),
		"if.true" to Word("if", "true")
	),
	printOnlyWords = listOf(
		Word("enum") to "enum_",
		Word("if", "", "cApitAls") to "if_.__.capitals",
		Word("_") to "__" ifJvmVersionIsAtLeast 9
	)

)

class JavaConstantNameTest: BaseNotationTest(
	notation = JavaConstantName,
	unchangedWords = listOf(
		"I_AM_A_CONSTANT" to Word("i", "am", "a", "constant")
	),
	parseOnlyWords = listOf(
		"if" to Word("if")
	),
	printOnlyWords = listOf(
		Word("") to "__",
		Word("1") to "__",
		Word("8if") to "IF",
		Word("enum") to "enum_",
		Word("_") to "__" ifJvmVersionIsAtLeast 9
	)
)
