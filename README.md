# JsonLight

### Description

This is a minimalist and lightweight Java Json5 library<br>
Classes and methods names are freely inspired from the Google Gson library

This library aims to fully support Json5, which up to this day isn't
done by Gson. Hence, should this lib allow you to make use of comments
and such in your projects.

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

### Import

_awaiting Sonatype validation for MavenCentral hosting_