package pl.jklimek.hex2dec.test;
import org.junit.*;
import static org.junit.Assert.*;
import pl.jklimek.hex2decBackend.model.BadValueException;
import pl.jklimek.hex2decBackend.model.ConvertedPair;
import pl.jklimek.hex2decBackend.model.JavaConverter;
import pl.jklimek.hex2decBackend.model.OutOfBoundsException;



/**
 * Tests Model implementation
 * @author Jakub Klimek
 * @version 3
 */
public class TestModel {
    private JavaConverter converter;
    @Before
    public void initConverter(){
        try {
            converter = new JavaConverter(2,10);
            converter.setDecValue(0,16);
            converter.setHexValue(1, "10");
        } catch (OutOfBoundsException |BadValueException ex ) {
            fail("Couldn't create valid component");
        }
    }
    @Test
    public void shortConstructorNoExcept(){
        try {
            JavaConverter conv = new JavaConverter(15);
            assertEquals("Created model with wrong maximum size", 15, conv.getMaxQueueSize());
            assertEquals("Created model should have one element in queue", 1, conv.getQueueSize());
        } catch (OutOfBoundsException ex) {
            Assert.fail("Can't create simple converter object with one item");
        }
    }
    @Test(expected = OutOfBoundsException.class)
    public void shortConstructorNegativeSize() throws OutOfBoundsException{
        JavaConverter conv = new JavaConverter(-1);
    }
    @Test(expected = OutOfBoundsException.class)
    public void shortConstructorZeroSize() throws OutOfBoundsException{
        JavaConverter conv = new JavaConverter(0);
    }
    @Test
    public void twoArgConstructorValid(){
        try {
            JavaConverter conv = new JavaConverter(2,15);
            assertEquals("Created model with wrong maximum size", 15, conv.getMaxQueueSize());
            assertEquals("Created model should have one element in queue", 2, conv.getQueueSize());
        } catch (OutOfBoundsException ex) {
            Assert.fail("Can't create simple converter object with one item");
        }
    }
    @Test(expected = OutOfBoundsException.class)
    public void twoArgConstructorSizeBiggerThanMax() throws OutOfBoundsException{
        JavaConverter conv = new JavaConverter(17,15);
    }
    @Test(expected = OutOfBoundsException.class)
    public void twoArgConstructorSizeLessThanZero() throws OutOfBoundsException{
        JavaConverter conv = new JavaConverter(-2,15);
    }
    @Test
    public void getMaxQueueSize(){
        JavaConverter conv;
        try {
            conv = new JavaConverter(1,10);
            assertEquals("Max queue size is not correct.", 10, conv.getMaxQueueSize());
        } catch (OutOfBoundsException ex) {
            fail("Constructor's parameters were good");
        }
    }
    @Test
    public void getQueueSize(){
        JavaConverter conv;
        try {
            conv = new JavaConverter(2,10);
            assertEquals("Queue size is not correct.", 2, conv.getQueueSize());
        } catch (OutOfBoundsException ex) {
            fail("Constructor's parameters were good");
        }
    }
    @Test 
    public void setQueueSizeString() throws OutOfBoundsException, NumberFormatException{
        converter.setQueueSize("5");
    }
    @Test(expected = NumberFormatException.class)
    public void setQueueSizeStringNotNumber() throws OutOfBoundsException, NumberFormatException{
        converter.setQueueSize("test");
    }
    @Test
    public void setQueueSizeBiggerValid() throws OutOfBoundsException{
        converter.setQueueSize(8);
    }
    @Test
    public void setQueueSizeSmallerValid() throws OutOfBoundsException{
        converter.setQueueSize(1);
    }
    @Test(expected = OutOfBoundsException.class)
    public void setQueueSizeBiggerThanMax() throws OutOfBoundsException{
        converter.setQueueSize(11);
    }
    @Test(expected = OutOfBoundsException.class)
    public void setQueueSizeLesserThanZero() throws OutOfBoundsException{
        JavaConverter conv;
        converter.setQueueSize(-2);
    }
    @Test(expected = OutOfBoundsException.class)
    public void setQueueSizeToZero() throws OutOfBoundsException{
        converter.setQueueSize(0);
    }
    @Test
    public void getValidPairStringIndex() throws OutOfBoundsException, NumberFormatException{
        assertEquals("Didn't get good value",16, converter.getPair("0").getDec());
    }
    @Test(expected = NumberFormatException.class)
    public void getConversionExceptionValidPairStringIndex() throws OutOfBoundsException, NumberFormatException{
        assertEquals("Didn't get good value",16, converter.getPair("ddd0").getDec());
    }
    @Test
    public void getValidPair() throws OutOfBoundsException{
        assertEquals("Didn't get good value",16, converter.getPair(0).getDec());
    }
    @Test(expected = OutOfBoundsException.class)
    public void getOutOfBoundsLowerPair() throws OutOfBoundsException{
        //Will throw exception
        converter.getPair(-1);
    }
    @Test(expected = OutOfBoundsException.class)
    public void getOutOfBoundsUpperPair() throws OutOfBoundsException{
        //Will throw exception
        converter.getPair(2);
    }
    @Test
    public void getAllPairs(){
        ConvertedPair[] pair = converter.getAllPairs();
        assertEquals("Array is not valid", 16, pair[0].getDec());       
        assertEquals("Array is not valid", "10", pair[1].getHex());

    }
    @Test
    public void getAllPairsEmptyConverter() throws OutOfBoundsException{
        JavaConverter conv = new JavaConverter(0, 5);
        ConvertedPair[] pair = conv.getAllPairs();
        assertTrue("Returned array should be empty", pair.length == 0);
    }
    @Test
    public void convertToDec(){
        converter.convertToDec();
        ConvertedPair[] pair = converter.getAllPairs();
        assertEquals("Conversion doesn't yield valid result", 16, pair[1].getDec());
    }
    @Test
    public void convertToDecMaximumValue(){
        try {
            converter.setHexValue(0, "7fffffffffffffff");
        } catch (OutOfBoundsException | BadValueException ex) {
            fail("Shouldn't catch exception here");
        }
        converter.convertToDec();
        ConvertedPair[] pair = converter.getAllPairs();
        assertEquals("Conversion doesn't yield valid result", 9223372036854775807L, pair[0].getDec());
    }
    @Test
    public void convertToHex(){
        converter.convertToHex();
        ConvertedPair[] pair = converter.getAllPairs();
        assertEquals("Conversion doesn't yield valid result", "10", pair[0].getHex());
    }
    @Test
    public void convertToHexWithMaximumValues(){
        try {
            converter.setDecValue(0, 9223372036854775807L);
        } catch (OutOfBoundsException | BadValueException ex) {
            fail("Shouldn't catch exception here");
        }
        converter.convertToHex();
        ConvertedPair[] pair = converter.getAllPairs();
        assertEquals("Conversion doesn't yield valid result", "7fffffffffffffff", pair[0].getHex());
    }
    @Test 
    public void setValidDecValueWithString() throws OutOfBoundsException, BadValueException, NumberFormatException{
        converter.setDecValue("0", "10");
    }
    @Test(expected = NumberFormatException.class)
    public void setInvalidDecValueWithStringInvalidIndex() throws OutOfBoundsException, BadValueException, NumberFormatException{
        converter.setDecValue("test", "10");
    }
    @Test(expected = NumberFormatException.class)
    public void setInvalidDecValueWithStringInvalidValue() throws OutOfBoundsException, BadValueException, NumberFormatException{
        converter.setDecValue("0", "10deedd");
    }
    @Test
    public void setValidDecValue() throws OutOfBoundsException, BadValueException{
        converter.setDecValue(0, 14);
        assertEquals("Inserted value is wrong", 14, converter.getPair(0).getDec());
    }
    @Test(expected = BadValueException.class)
    public void setInvalidDecValue() throws BadValueException, OutOfBoundsException{
        converter.setDecValue(0, -14);
    }
    @Test
    public void setMaximumValidDecValue() throws BadValueException, OutOfBoundsException{
        converter.setDecValue(0, 9223372036854775807L);
    }
    @Test(expected = OutOfBoundsException.class)
    public void setDecValueAtTooHighIndex() throws BadValueException, OutOfBoundsException{
        converter.setDecValue(2, 14);
    }
    @Test(expected = OutOfBoundsException.class)
    public void setDecValueAtNegativeIndex() throws BadValueException, OutOfBoundsException{
        converter.setDecValue(-1, 14);
    }
    @Test 
    public void setValidHexValueWithString() throws OutOfBoundsException, BadValueException, NumberFormatException{
        converter.setHexValue("0", "FF2");
    }
    @Test(expected = NumberFormatException.class)
    public void setInvalidHexValueWithStringInvalidIndex() throws OutOfBoundsException, BadValueException, NumberFormatException{
        converter.setHexValue("test", "FF2");
    }
    @Test
    public void setValidHexValue() throws OutOfBoundsException, BadValueException{
        converter.setHexValue(0, "FF2");
        assertEquals("Inserted value is wrong", "FF2", converter.getPair(0).getHex());
    }
    @Test
    public void setValidHexValueSmallLetters() throws OutOfBoundsException, BadValueException{
        converter.setHexValue(0, "ff2");
        assertEquals("Inserted value is wrong", "ff2", converter.getPair(0).getHex());
    }
    @Test(expected = BadValueException.class)
    public void setTooBigHexValue() throws BadValueException, OutOfBoundsException{
        converter.setHexValue(0, "8FFFFFFFFFFFFFFF");
    }
    @Test
    public void setMaxHexValue() throws BadValueException, OutOfBoundsException{
        converter.setHexValue(0, "7FFFFFFFFFFFFFFF");
    }
    @Test(expected = BadValueException.class)
    public void setNegativeHexValue() throws BadValueException, OutOfBoundsException{
        converter.setHexValue(0, "-FFF");
    }
    @Test(expected = BadValueException.class)
    public void setNotHexValue() throws BadValueException, OutOfBoundsException{
        converter.setHexValue(0, "ala ma kota");
    }
    @Test(expected = OutOfBoundsException.class)
    public void setHexValueAtTooHighIndex() throws BadValueException, OutOfBoundsException{
        converter.setHexValue(2, "1A");
    }
    @Test(expected = OutOfBoundsException.class)
    public void setHexValueAtNegativeIndex() throws BadValueException, OutOfBoundsException{
        converter.setHexValue(-1, "22");
    }
    @Test(expected = BadValueException.class)
    public void setNullHexValue() throws BadValueException, OutOfBoundsException{
        converter.setHexValue(0, null);
        
    }
}
