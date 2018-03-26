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

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IniReaderTest {
    private IniReader iniReader;

    @Before
    public void initIniReader() {
        String iniFileContents =
                "# Full line comment\n" +
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
                        "floatKey = 6\n" +
                        "anotherFloatKey = 7.3f\n" +
                        "invalidFloatKey = 8,6\n" +
                        "anotherInvalidFloatKey = hi\n" +
                        "UnicodeKey çáč🧝 = unicodevalue áśǵj́ḱĺóí⋄«»⋄⋄ǫő"; // Each of these values is used exactly once in these tests, except for doubleRequestedKey
        iniReader = new IniReader(new BufferedReader(new StringReader(iniFileContents)));
    }

    @Test
    public void testInputHandling() {
        assertEquals(iniReader.getString("missingKey", "correctValue"), "correctValue");
        assertEquals(iniReader.getString("terrible key name", "wrongValue"), "terrible key value");
        assertEquals(iniReader.getString("partLineCommentKey", "wrongValue"), "correctValue1");
        assertEquals(iniReader.getString("doubleRequestedKey", "wrongValue"), "validValue2");
        assertEquals(iniReader.getString("doubleRequestedKey", "wrongValue"), "validValue2");
        assertEquals(iniReader.getString("this shouldn't throw exception", "correctValue"), "correctValue");
        assertEquals(iniReader.getString("UnicodeKey çáč🧝", "wrongValue"), "unicodevalue áśǵj́ḱĺóí⋄«»⋄⋄ǫő");
    }

    @Test
    public void testGetString() {
        assertEquals(iniReader.getString("asdfghjk", "correctValue"), "correctValue");
        assertEquals(iniReader.getString("validStringKey", "wrongValue"), "validString");
    }

    @Test
    public void testGetInt() {
        assertEquals(iniReader.getInt("asdfghjk", 55), 55);
        assertEquals(iniReader.getInt("intKey", 0), 5);
        assertEquals(iniReader.getInt("invalidIntKey", 56), 56);
        assertEquals(iniReader.getInt("anotherInvalidIntKey", 57), 57);
    }

    @Test
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

    @Test
    public void testGetFloat() {
        assertTrue(Float.compare(iniReader.getFloat("asdfghjk", 7.8f), 7.8f) == 0);
        assertTrue(Float.compare(iniReader.getFloat("floatKey", 7.8f), 6f) == 0);
        assertTrue(Float.compare(iniReader.getFloat("anotherFloatKey", 7.8f), 7.3f) == 0);
        assertTrue(Float.compare(iniReader.getFloat("invalidFloatKey", 7.8f), 7.8f) == 0);
        assertTrue(Float.compare(iniReader.getFloat("anotherInvalidFloatKey", 7.8f), 7.8f) == 0);
    }

    //TODO ADD MOAR TESTS
}
