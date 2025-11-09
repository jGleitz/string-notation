# string-notation  [![CI](https://github.com/jGleitz/string-notation/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/jGleitz/string-notation/actions/workflows/ci.yml?query=branch%3Amain) [![Maven Central Version](https://img.shields.io/maven-central/v/de.joshuagleitze/string-notation?logo=sonatype&label=maven%20central)](https://central.sonatype.com/artifact/de.joshuagleitze/string-notation)
_Convert between different string notations commonly found in programming._
Useful, for example, to build identifiers when generating code.
Also includes notations to build valid Java type or member names. 

#### [API Documentation](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/)

## Notations
We support the following notations:

 * General Programming
    * [LowerCamelCase](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/-lower-camel-case/), e.g. `thisIsMyString`
    * [UpperCamelCase](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/-upper-camel-case/), e.g. `ThisIsMyString`
    * [SnakeCase](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/-snake-case/), e.g. `this_is_MY_String`
    * [ScreamingSnakeCase](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/-screaming-snake-case/), e.g. `THIS_IS_MY_STRING`
 * Java
   * [JavaMemberName](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/-java-member-name/), e.g. `thisIsMyString`
   * [JavaTypeName](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/-java-type-name/), e.g. `ThisIsMyString`
   * [JavaConstantName](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/-java-constant-name/), e.g. `THIS_IS_MY_STRING`
   * [JavaPackageName](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/-java-package-name/), e.g. `this.is.MY.String`
   * [JavaPackagePart](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/-java-package-part/), e.g. `thisismystring`
 * File System
   * [UnixPath](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/-unix-path/), e.g. `/home/user/some/file`
   * [WindowsPath](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/-windows-path/), e.g. `C:/Users/user/some/file`
 * Miscellaneous    
    * [NormalWords](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/-normal-words/), e.g. `this is MY String`

## Usage
`string-notation` converts Strings from their notation into a [Word](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/-word/), which is a notation-agnostic representation.
[Words](https://jgleitz.github.io/string-notation/string-notation/de.joshuagleitze.stringnotation/-word/) can then be transformed into Strings in a notation again.

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

