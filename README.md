# JsonLight

### Description

This is a minimalist and lightweight Java Json5 library<br>
Classes and methods names are freely inspired from the Google Gson library

This library aims to fully support Json5, which up to this day isn't
done by Gson. Hence, should this lib allow you to make use of comments
and such in your projects.

### Example

```java
import com.nerjal.json.elements.*;
import com.nerjal.json.JsonParser;

import static com.nerjal.json.JsonError.*;

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

### Import

Use [Jitpack](https://jitpack.io) in order to import the library in
your own project

#### With Maven

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
            <version>1.1.3</version>
        </dependency>
    </dependencies>
</project>
```

#### With Gradle

```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation "com.github.NerjalNosk:JsonLight:1.1.3"
}
```
