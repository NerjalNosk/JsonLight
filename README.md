# JsonLight

## Description

This is a lightweight Java Json5+ library, providing full [Json5](https://json5.org)
support, along-side many features inspired from other formats such as
[HOCON](https://github.com/lightbend/config/blob/main/HOCON.md).
Read more about these additions in the [Json Extension](#json-extension) part.

### Example

```java
import io.github.nerjalnosk.jsonlight.elements.JsonComment;
import io.github.nerjalnosk.jsonlight.elements.JsonNumber;
import io.github.nerjalnosk.jsonlight.elements.JsonObject;
import io.github.nerjalnosk.jsonlight.JsonParser;

import static io.github.nerjalnosk.jsonlight.JsonError.*;

public abstract class Main {
    /**
     * Prints the following:
     * ```
     * {
     *     "a": 1
     *     //this is a comment
     * }
     * ```
     */
    public static void main(String[] args) {
        JsonObject object = new JsonObject();
        object.put("a", new JsonNumber(1));
        object.add(null, new JsonComment("this is a comment"));
        try {
            System.out.println(JsonParser.stringify(object));
        } catch (RecursiveJsonElementException e) {
            e.printStackTrace();
        }
    }
}
```

## Json extension

As of 3.0, JsonLight includes a Json5 extension, as Json6 of sorts, which adds multiple features to regular Json5.

These features mostly aim to make life easier for all users.

### Circular structures

Introduced in version 3.0, it adds support for circular structures parsing, both to and from textual sources.
Indeed, it allows to reference an element inside itself, to avoid infinite text transformation
loops.

This extension works with an ID system (automatically generated), textualized as `<@id>`
and `<#id>`, respectively the *declaration* and *reference*, where `id` is an unsigned integer.
This ID  will of course be provided at the "declaration" of the element, as well as the later
referencing of that element.

#### Example

```
{
  "key_1": "value_1",
  "key_2": <@1234> { // here, the element with ID "1234" is declared. We will remember this ID.
    "key_2.1": "value_2.1",
    "key_2.2": [
      <#1234>, // here, the element with ID "1234" is referenced. We link it to the earlier declaration.
      {
        "key_3": "value_3"
      }
    ]
  }
}
```

Of course, referencing an undeclared element will result in an error. But upon automated stringification,
references will only be generated for elements located inside themselves, to whichever level.

Declaring an unused ID, on the contrary, will not cause an issue, but it will not be kept upon
further stringification.

__Note__ : IDs will most likely change upon each automated parsing/stringification, as they are
not kept in the processed elements. This also include not being able to recognise an element by
its ID at runtime.

### Open trailing

Introduced in version 3.1, it adds support for non-closed data structures at the end of a parsed source.

It is only supported at the very end of a parsed element to avoid getting messed up
data which would end up lost because of something as genuine as a typo,
but still provides a safeguard for potential data loss if a text is cut short or
a file cannot be read until its very end.

This is of course applied recursively over all non-closed container element upon parsing.

There are however exceptions to what can or cannot be closed in such ways:
* Arrays can be validated despite not having a closing character `]`.
* Strings can be validated despite not having a closing character (quotations).
* Object keys cannot be validated if they do not have a closing character but are opened with one (quotation).
* Objects cannot be validated if they have a trailing key with no linked value.
* Objects can be validated if they have no closing character `}`, and the precedent rules are not broken.
* Object references or ID declaration cannot be validated if they do not have a closing character `>`.
* Block comments can be validated if they do not have a closing sequence `*/.`

#### Examples

```json5
[
  [
    [
      [
        3,
        4,
        5,
        /**
         * This is a non-closed block comment haha
```
The above structure would be parsed as a valid array
containing a single array containing a single array containing a single array,
which at last contains the numbers 3, 4 and 5, as well as a block comment.

However, the following would be considered as invalid, as the inner array is never closed, while the `key` object still gets its own closure.
```json5
{
  "key": {
    "array": [
      3,
      4,
      5,
  }
```

## Import

Now in Maven Central! Latest available version: 
[3.1](https://mvnrepository.com/artifact/io.github.nerjalnosk/JsonLight/3.1)

### With Maven

```xml

<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>...</groupId>
    <artifactId>...</artifactId>
    <version>...</version>
    
    <properties>
        <jsonLight.version>2.1</jsonLight.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>io.github.nerjalnosk</groupId>
            <artifactId>JsonLight</artifactId>
            <version>${jsonlight.version}</version>
        </dependency>
    </dependencies>
</project>
```

### With Gradle

```groovy
dependencies {
    implementation "io.github.nerjalnosk:jsonlight:${jsonligh_version}"
}
```

#### Older versions

For versions before 2.0, please use [Jitpack](https://jitpack.io) in
order to import the library in your own project.

_With Maven_

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>...</groupId>
    <artifactId>...</artifactId>
    <version>...</version>
    
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>
    
    <dependencies>
        <dependency>
            <groupId>com.github.NerjalNosk</groupId>
            <artifactId>JsonLight</artifactId>
            <version>1.4.0</version>
        </dependency>
    </dependencies>
</project>
```

_With Gradle_

```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation "com.github.NerjalNosk:JsonLight:1.4.0"
}
```

## History

| Version | Date          | Name                       | Changes                                                                                  |
|---------|---------------|----------------------------|------------------------------------------------------------------------------------------|
| 1.4.0   | 10 Aug. 2022  | Witcheries and Stringeries | Json stringification rework, added parsing options and defaults, and more parsing tools  |
| 1.4.1   | 18 Sept. 2023 | TechniLexicalities         | Codebase repo fixes, jdoc and build improvements, parsing logging utilities              |
| 2.0     | 18 Sept. 2023 | Maven Conquest             | Classes refactoring, published to Maven Central                                          |
| 2.1     | 25 Sept. 2023 | Number Parse               | Fixed number parsing (from/to hexadecimal, from scientific notation)                     |
| 3.0     | 11 Mar. 2024  | Json Circles               | Added circular structure parsing, both to and from, using ID markers                     |
| 3.1     | 13 Mar. 2024  | Numbers Galore             | Added Number support for Big integers and decimals, as well as more exact value tracking |
