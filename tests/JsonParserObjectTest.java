import com.nerjal.json.JsonError;
import com.nerjal.json.JsonParser;
import com.nerjal.json.elements.JsonElement;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class JsonParserObjectTest {

    @Test
    public void emptyObject() throws JsonError.JsonParseException, JsonError.JsonElementTypeException {
        JsonElement jsonElement = JsonParser.parseString("{}");
        assertTrue(jsonElement.isJsonObject());
        assertTrue(jsonElement.getAsJsonObject().isEmpty());
    }
}
