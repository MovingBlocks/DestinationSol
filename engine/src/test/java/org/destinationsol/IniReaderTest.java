/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol;

import com.google.common.base.Enums;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IniReaderTest {
    private IniReader iniReader;

    @BeforeEach
    public void initIniReader() {
        String iniFileContents =
                "# Full line comment\n" + // Each of these values is used exactly once in these tests, except for doubleRequestedKey
                        "partLineCommentKey = correctValue1 # Part line comment\n" +
                        "terrible key name        =          terrible key value\n" +
                        "# missingKey = wrongValue\n" +
                        "# Follows blank line:\n" +
                        "\n" +
                        "this shouldn't throw exception\n" +
                        "doubleRequestedKey = validValue2\n" +
                        "validStringKey = validString\n" +
                        "trueBooleanKey = true\n" +
                        "anotherTrueBooleanKey = tRuE\n" +
                        "falseBooleanKey = false\n" +
                        "anotherFalseBooleanKey = FaLsE\n" +
                        "invalidBooleanKey=thisIsNotBoolean\n" +
                        "intKey = 5\n" +
                        "invalidIntKey = 3.4\n" +
                        "anotherInvalidIntKey = two\n" +
                        "blankIntKey = \n" +
                        "floatKey = 6\n" +
                        "anotherFloatKey = 7.3f\n" +
                        "invalidFloatKey = 8,6\n" +
                        "anotherInvalidFloatKey = hi\n" +
                        "enumInvalid = 1\n" +
                        "enumEmpty =\n" +
                        "enumValid = KEYBOARD\n" +
                        "UnicodeKey çáč🧝 = unicodevalue áśǵj́ḱĺóí⋄«»⋄⋄ǫő";
        iniReader = new IniReader(new BufferedReader(new StringReader(iniFileContents)));
    }

    @Test
    public void testInputHandling() {
        assertEquals("correctValue", iniReader.getString("missingKey", "correctValue"));
        assertEquals("terrible key value", iniReader.getString("terrible key name", "wrongValue"));
        assertEquals("correctValue1", iniReader.getString("partLineCommentKey", "wrongValue"));
        assertEquals("validValue2", iniReader.getString("doubleRequestedKey", "wrongValue"));
        assertEquals("validValue2", iniReader.getString("doubleRequestedKey", "wrongValue"));
        assertEquals("correctValue", iniReader.getString("this shouldn't throw exception", "correctValue"));
        assertEquals("unicodevalue áśǵj́ḱĺóí⋄«»⋄⋄ǫő", iniReader.getString("UnicodeKey çáč🧝", "wrongValue"));
    }

    @org.junit.jupiter.api.Test
    public void testGetString() {
        assertEquals("correctValue", iniReader.getString("asdfghjk", "correctValue"));
        assertEquals("validString", iniReader.getString("validStringKey", "wrongValue"));
    }

    @org.junit.jupiter.api.Test
    public void testGetInt() {
        assertEquals(55, iniReader.getInt("asdfghjk", 55));
        assertEquals(5, iniReader.getInt("intKey", 0));
        assertEquals(56, iniReader.getInt("invalidIntKey", 56));
        assertEquals(57, iniReader.getInt("anotherInvalidIntKey", 57));
        assertEquals(58, iniReader.getInt("blankIntKey", 58));
    }

    @org.junit.jupiter.api.Test
    public void testGetBoolean() {
        assertFalse(iniReader.getBoolean("asdfghjk", false));
        assertTrue(iniReader.getBoolean("asdfghjk", true));
        assertTrue(iniReader.getBoolean("trueBooleanKey", false));
        assertTrue(iniReader.getBoolean("anotherTrueBooleanKey", false));
        assertFalse(iniReader.getBoolean("falseBooleanKey", true));
        assertFalse(iniReader.getBoolean("anotherFalseBooleanKey", true));
        assertFalse(iniReader.getBoolean("invalidBooleanKey", false));
        assertTrue(iniReader.getBoolean("invalidBooleanKey", true));
    }

    @org.junit.jupiter.api.Test
    public void testGetFloat() {
        assertTrue(Float.compare(iniReader.getFloat("asdfghjk", 7.8f), 7.8f) == 0);
        assertTrue(Float.compare(iniReader.getFloat("floatKey", 7.8f), 6f) == 0);
        assertTrue(Float.compare(iniReader.getFloat("anotherFloatKey", 7.8f), 7.3f) == 0);
        assertTrue(Float.compare(iniReader.getFloat("invalidFloatKey", 7.8f), 7.8f) == 0);
        assertTrue(Float.compare(iniReader.getFloat("anotherInvalidFloatKey", 7.8f), 7.8f) == 0);
    }

    @org.junit.jupiter.api.Test
    public void testEnums() {
        // When no value exists in the file, use the defaultValue in getString.
        assertEquals(GameOptions.ControlType.MIXED, Enums.getIfPresent(GameOptions.ControlType.class,  iniReader.getString("enumDefault", "MIXED")).or(GameOptions.ControlType.KEYBOARD));
        assertEquals(GameOptions.ControlType.MIXED, Enums.getIfPresent(GameOptions.ControlType.class,  iniReader.getString("enumDefault", "MIXED")).or(GameOptions.ControlType.MIXED));
        assertEquals(GameOptions.ControlType.KEYBOARD, Enums.getIfPresent(GameOptions.ControlType.class,  iniReader.getString("enumEmpty", "KEYBOARD")).or(GameOptions.ControlType.MIXED));

        // When the value in the file isn't a valid enum, use the default value in getIfPresent
        assertEquals(GameOptions.ControlType.MIXED, Enums.getIfPresent(GameOptions.ControlType.class,  iniReader.getString("enumInvalid", "KEYBOARD")).or(GameOptions.ControlType.MIXED));
        assertEquals(GameOptions.ControlType.MIXED, Enums.getIfPresent(GameOptions.ControlType.class,  iniReader.getString("enumInvalid", "MIXED")).or(GameOptions.ControlType.MIXED));

        // When the value is in the file, use that value regardless of the default values.
        assertEquals(GameOptions.ControlType.KEYBOARD, Enums.getIfPresent(GameOptions.ControlType.class,  iniReader.getString("enumValid", "MIXED")).or(GameOptions.ControlType.MIXED));
    }

}
