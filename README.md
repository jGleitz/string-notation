# string-notation [![CI Status](https://github.com/jGleitz/string-notation/workflows/CI/badge.svg)](https://github.com/jGleitz/string-notation/actions) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.joshuagleitze/string-notation/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.joshuagleitze/string-notation)
_Convert between different string notations commonly found in programming._
Useful, for example, to build identifiers when generating code.
Also includes notations to build valid Java type or member names. 

[API Documentation](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/)

## Usage
`string-notation` converts Strings from their notation into a [Word](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/-word), which is a notation-agnostic representation.
[Words](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/-word) can then be transformed into Strings in a notation again.

### Kotlin

```kotlin
import de.joshuagleitze.stringnotation.LowerCamelCase
import de.joshuagleitze.stringnotation.ScreamingSnakeCase
import de.joshuagleitze.stringnotation.fromNotation

// myVariable -> MY_VARIABLE
"myVariable".fromNotation(LowerCamelCase).toNotation(ScreamingSnakeCase)
```

```kotlin
import de.joshuagleitze.stringnotation.JavaTypeName
import de.joshuagleitze.stringnotation.NormalWords
import de.joshuagleitze.stringnotation.fromNotation

// 1 Type Name 4 You! -> TypeName4You
"1 Type Name 4 You!".fromNotation(NormalWords).toNotation(JavaTypeName)
```


### Java

```java
import de.joshuagleitze.stringnotation.LowerCamelCase;
import de.joshuagleitze.stringnotation.UpperCamelCase;
import static de.joshuagleitze.stringnotation.StringToWord.fromNotation;

// myVariable -> MY_VARIABLE
fromNotation("myVariable", UpperCamelCase.INSTANCE).toNotation(LowerCamelCase.INSTANCE);
```

```java
import de.joshuagleitze.stringnotation.JavaTypeName;
import de.joshuagleitze.stringnotation.NormalWords;
import static de.joshuagleitze.stringnotation.StringToWord.fromNotation;

// 1 Type Name 4 You! -> TypeName4You
fromNotation("1 Type Name 4 You!", NormalWords.INSTANCE).toNotation(JavaTypeName.INSTANCE);
```

## [Contributions welcome](http://contributionswelcome.org/)

All contributions (no matter if small) are always welcome.

