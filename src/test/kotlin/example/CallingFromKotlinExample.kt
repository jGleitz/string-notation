package de.joshuagleitze.stringnotation.example

import de.joshuagleitze.stringnotation.JavaTypeName
import de.joshuagleitze.stringnotation.LowerCamelCase
import de.joshuagleitze.stringnotation.NormalWords
import de.joshuagleitze.stringnotation.ScreamingSnakeCase
import de.joshuagleitze.stringnotation.fromNotation

fun callingFromKotlin() {
	// myVariable -> MY_VARIABLE
	"myVariable".fromNotation(LowerCamelCase).toNotation(ScreamingSnakeCase)

	// 1 Type Name 4 You! -> TypeName4You
	"1 Type Name 4 You!".fromNotation(NormalWords).toNotation(JavaTypeName)
}
