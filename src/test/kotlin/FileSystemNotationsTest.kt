package de.joshuagleitze.stringnotation

class UnixPathTest: BaseNotationTest(
	notation = UnixPath,
	unchangedWords = listOf(
		"/home/user/some/file" to Word("", "home", "user", "some", "file"),
		"a/relative/path" to Word("a", "relative", "path")
	),
	printOnlyWords = listOf(
		Word("", "home", "null\u0000") to "/home/null",
		Word("", "home", "user/some/file") to "/home/usersomefile",
	)
)

class WindowsPathTest: BaseNotationTest(
	notation = WindowsPath,
	unchangedWords = listOf(
		"C:\\Users\\user\\some\\file" to Word("C:", "Users", "user", "some", "file"),
		"a\\relative\\path" to Word("a", "relative", "path")
	),
	printOnlyWords = listOf(
		*('\u0000'..'\u001F').map { controlChar ->
			Word("C:", "bad${controlChar}File") to "C:\\badFile"
		}.toTypedArray(),
		Word("C:", "bad\u007FFile") to "C:\\badFile",
		Word("C:", "bad<File>") to "C:\\badFile",
	)
)
