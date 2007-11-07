/*
 * Copyright 2006-2007 the original author or authors.
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

package org.springframework.batch.io.file;

import java.math.BigDecimal;
import java.text.ParseException;

import junit.framework.TestCase;

import org.springframework.batch.io.file.FieldSet;

public class FieldSetTests extends TestCase {
	FieldSet fieldSet;

	String[] tokens;

	String[] names;

	protected void setUp() throws Exception {
		super.setUp();

		tokens = new String[] { "TestString", "true", "C", "10", "-472", "354224", "543", "124.3", "424.3", "324",
				null, "2007-10-12", "12-10-2007", "" };
		names = new String[] { "String", "Boolean", "Char", "Byte", "Short", "Integer", "Long", "Float", "Double",
				"BigDecimal", "Null", "Date", "DatePattern", "BlankInput" };

		fieldSet = new FieldSet(tokens, names);
		assertEquals(14, fieldSet.getFieldCount());

	}
	
	public void testNames() throws Exception {
		assertEquals(fieldSet.getFieldCount(), fieldSet.getNames().length);
	}

	public void testNamesNotKnown() throws Exception {
		fieldSet = new FieldSet(new String[]{"foo"});
		try {
			fieldSet.getNames();
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			// expected
		}
	}

	public void testReadString() throws ParseException {

		assertEquals(fieldSet.readString(0), "TestString");
		assertEquals(fieldSet.readString("String"), "TestString");

	}

	public void testReadChar() throws Exception {

		assertTrue(fieldSet.readChar(2) == 'C');
		assertTrue(fieldSet.readChar("Char") == 'C');

	}

	public void testReadBooleanTrue() throws Exception {

		assertTrue(fieldSet.readBoolean(1));
		assertTrue(fieldSet.readBoolean("Boolean"));

	}

	public void testReadByte() throws Exception {

		assertTrue(fieldSet.readByte(3) == 10);
		assertTrue(fieldSet.readByte("Byte") == 10);

	}

	public void testReadShort() throws Exception {

		assertTrue(fieldSet.readShort(4) == -472);
		assertTrue(fieldSet.readShort("Short") == -472);

	}

	public void testReadFloat() throws Exception {

		assertTrue(fieldSet.readFloat(7) == 124.3F);
		assertTrue(fieldSet.readFloat("Float") == 124.3F);

	}

	public void testReadDouble() throws Exception {

		assertTrue(fieldSet.readDouble(8) == 424.3);
		assertTrue(fieldSet.readDouble("Double") == 424.3);

	}

	public void testReadBigDecimal() throws Exception {

		BigDecimal bd = new BigDecimal(324);
		assertEquals(fieldSet.readBigDecimal(9), bd);
		assertEquals(fieldSet.readBigDecimal("BigDecimal"), bd);

	}

	public void testReadBigDecimalWithDefaultvalue() throws Exception {

		BigDecimal bd = new BigDecimal(324);
		assertEquals(bd, fieldSet.readBigDecimal(10, bd));
		assertEquals(bd, fieldSet.readBigDecimal("Null", bd));

	}

	public void testReadNonExistentField() throws Exception {

		try {
			fieldSet.readString("something");
			fail("field set returns value even value was never put in!");
		}
		catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().indexOf("something") > 0);
		}

	}

	public void testReadIndexOutOfRange() throws Exception {

		try {
			fieldSet.readShort(-1);
			fail("field set returns value even index is out of range!");
		}
		catch (IndexOutOfBoundsException e) {
			assertTrue(true);
		}

		try {
			fieldSet.readShort(99);
			fail("field set returns value even index is out of range!");
		}
		catch (Exception e) {
			assertTrue(true);
		}
	}

	public void testReadBooleanWithTrueValue() {
		assertTrue(fieldSet.readBoolean(1, "true"));
		assertFalse(fieldSet.readBoolean(1, "incorrect trueValue"));

		assertTrue(fieldSet.readBoolean("Boolean", "true"));
		assertFalse(fieldSet.readBoolean("Boolean", "incorrect trueValue"));
	}

	public void testReadBooleanFalse() {
		fieldSet = new FieldSet(new String[] { "false" });
		assertFalse(fieldSet.readBoolean(0));
	}

	public void testReadCharException() {
		try {
			fieldSet.readChar(1);
			fail("the value read was not a character, exception expected");
		}
		catch (IllegalArgumentException expected) {
			assertTrue(true);
		}

		try {
			fieldSet.readChar("Boolean");
			fail("the value read was not a character, exception expected");
		}
		catch (IllegalArgumentException expected) {
			assertTrue(true);
		}
	}

	public void testReadInt() throws Exception {
		assertEquals(354224, fieldSet.readInt(5));
		assertEquals(354224, fieldSet.readInt("Integer"));
	}

	public void testReadBlankInt(){

		//Trying to parse a blank field as an integer, but without a default
		//value should throw a NumberFormatException
		try{
			fieldSet.readInt(13);
			fail();
		}
		catch(NumberFormatException ex){
			//expected
		}

		try{
			fieldSet.readInt("BlankInput");
			fail();
		}
		catch(NumberFormatException ex){
			//expected
		}

	}

	public void testReadLong() throws Exception {
		assertEquals(543, fieldSet.readLong(6));
		assertEquals(543, fieldSet.readLong("Long"));
	}

	public void testReadIntWithNullValue() {
		assertEquals(5, fieldSet.readInt(10, 5));
		assertEquals(5, fieldSet.readInt("Null", 5));
	}

	public void testReadIntWithDefaultAndNotNull() throws Exception {
		assertEquals(354224, fieldSet.readInt(5, 5));
		assertEquals(354224, fieldSet.readInt("Integer", 5));
	}

	public void testReadLongWithNullValue() {
		int defaultValue = 5;
		int indexOfNull = 10;
		int indexNotNull = 6;
		String nameNull = "Null";
		String nameNotNull = "Long";
		long longValueAtIndex = 543;

		assertEquals(fieldSet.readLong(indexOfNull, defaultValue), defaultValue);
		assertEquals(fieldSet.readLong(indexNotNull, defaultValue), longValueAtIndex);

		assertEquals(fieldSet.readLong(nameNull, defaultValue), defaultValue);
		assertEquals(fieldSet.readLong(nameNotNull, defaultValue), longValueAtIndex);
	}

	public void testReadBigDecimalInvalid() {
		int index = 0;

		try {
			fieldSet.readBigDecimal(index);
			fail("field value is not a number, exception expected");
		}
		catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().indexOf("TestString") > 0);
		}

	}

	public void testReadBigDecimalByNameInvalid() throws Exception {
		try {
			fieldSet.readBigDecimal("String");
			fail("field value is not a number, exception expected");
		}
		catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().indexOf("TestString") > 0);
			assertTrue(e.getMessage().indexOf("name: [String]") > 0);
		}
	}

	public void testReadDate() throws Exception {
		assertNotNull(fieldSet.readDate(11));
		assertNotNull(fieldSet.readDate("Date"));
	}

	public void testReadDateInvalid() throws Exception {

		try {
			fieldSet.readDate(0);
			fail("field value is not a date, exception expected");
		}
		catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().indexOf("TestString") > 0);
		}

	}

	public void testReadDateInvalidByName() throws Exception {

		try {
			fieldSet.readDate("String");
			fail("field value is not a date, exception expected");
		}
		catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().indexOf("name: [String]") > 0);
		}

	}

	public void testReadDateInvalidWithPattern() throws Exception {

		try {
			fieldSet.readDate(0, "dd-MM-yyyy");
			fail("field value is not a date, exception expected");
		}
		catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().indexOf("dd-MM-yyyy") > 0);
		}
	}

	public void testReadDateByNameInvalidWithPattern() throws Exception {

		try {
			fieldSet.readDate("String", "dd-MM-yyyy");
			fail("field value is not a date, exception expected");
		}
		catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().indexOf("dd-MM-yyyy") > 0);
			assertTrue(e.getMessage().indexOf("String") > 0);
		}
	}

	public void testEquals() {

		assertEquals(fieldSet, fieldSet);
		assertEquals(fieldSet, new FieldSet(tokens));

		String[] tokens1 = new String[] { "token1" };
		String[] tokens2 = new String[] { "token1" };
		FieldSet fs1 = new FieldSet(tokens1);
		FieldSet fs2 = new FieldSet(tokens2);
		assertEquals(fs1, fs2);
	}

	public void testNullField() {
		assertEquals(null, fieldSet.readString(10));
	}

	public void testEqualsNull() {
		assertFalse(fieldSet.equals(null));
	}

	public void testEqualsNullTokens() {
		assertFalse(new FieldSet(null).equals(fieldSet));
	}

	public void testEqualsNotEqual() throws Exception {

		String[] tokens1 = new String[] { "token1" };
		String[] tokens2 = new String[] { "token1", "token2" };
		FieldSet fs1 = new FieldSet(tokens1);
		FieldSet fs2 = new FieldSet(tokens2);
		assertFalse(fs1.equals(fs2));

	}

	public void testHashCode() throws Exception {
		assertEquals(fieldSet.hashCode(), new FieldSet(tokens).hashCode());
	}

	public void testHashCodeWithNullTokens() throws Exception {
		assertEquals(0, new FieldSet(null).hashCode());
	}

	public void testConstructor() throws Exception {
		try {
			new FieldSet(new String[] { "1", "2" }, new String[] { "a" });
			fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testToStringWithNames() throws Exception {
		fieldSet = new FieldSet(new String[] { "foo", "bar" }, new String[] { "Foo", "Bar" });
		assertTrue(fieldSet.toString().indexOf("Foo=foo") >= 0);
	}

	public void testToStringWithoutNames() throws Exception {
		fieldSet = new FieldSet(new String[] { "foo", "bar" });
		assertTrue(fieldSet.toString().indexOf("foo") >= 0);
	}

	public void testToStringNullTokens() throws Exception {
		fieldSet = new FieldSet(null);
		assertEquals("", fieldSet.toString());
	}

	public void testProperties() throws Exception {
		assertEquals("foo", new FieldSet(new String[] { "foo", "bar" }, new String[] { "Foo", "Bar" }).getProperties()
				.getProperty("Foo"));
	}

	public void testPropertiesWithNoNames() throws Exception {
		try {
			new FieldSet(new String[] { "foo", "bar" }).getProperties();
			fail("Expected IllegalStateException");
		}
		catch (IllegalStateException e) {
			// expected
		}
	}

	public void testPropertiesWithWhiteSpace() throws Exception{

		assertEquals("bar", new FieldSet(new String[] { "foo", "bar   " }, new String[] { "Foo", "Bar"}).getProperties().getProperty("Bar"));
	}

	public void testPropertiesWithNullValues() throws Exception{

		fieldSet = new FieldSet(new String[] { null, "bar" }, new String[] { "Foo", "Bar"});
		assertEquals("bar", fieldSet.getProperties().getProperty("Bar"));
		assertEquals(null, fieldSet.getProperties().getProperty("Foo"));
	}

	public void testAccessByNameWhenNamesMissing() throws Exception {
		try {
			new FieldSet(new String[] { "1", "2" }).readInt("a");
			fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}
}
