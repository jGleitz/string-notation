package de.joshuagleitze.stringnotation.example;

import de.joshuagleitze.stringnotation.JavaTypeName;
import de.joshuagleitze.stringnotation.LowerCamelCase;
import de.joshuagleitze.stringnotation.NormalWords;
import de.joshuagleitze.stringnotation.UpperCamelCase;

import static de.joshuagleitze.stringnotation.StringToWord.fromNotation;

public class CallingFromJavaExample {
	public static void main(String... args) {
		// myVariable -> MY_VARIABLE
		fromNotation("myVariable", UpperCamelCase.INSTANCE).toNotation(LowerCamelCase.INSTANCE);

		// 1 Type Name 4 You! -> TypeName4You
		fromNotation("1 Type Name 4 You!", NormalWords.INSTANCE).toNotation(JavaTypeName.INSTANCE);
	}
}
