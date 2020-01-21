package pl.jklimek.hex2decBackend.model;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of IConverterModel. 
 * It is meant to be used as part of MVC scheme converter.
 * @author Jakub Klimek
 * @version 7
 * @see IConverterModel
 */

@MVCImplementation(MVCImplementation.Component.MODEL)
public class JavaConverter implements IConverterModel {
    /**
     * Constructs converter with provided parameters.
     * @param queueSize number of conversion pairs in queue
     * @param maxQueueSize maximum number of conversion pairs in queue
     * @throws OutOfBoundsException if selected size and maximum size are wrong
     */
    @SuppressWarnings("Convert2Diamond")
    public JavaConverter(int queueSize, int maxQueueSize) throws OutOfBoundsException{
        this.maxQueueSize = maxQueueSize;
        this.queueSize = 0; //starting size is 0
        this.decList = new ArrayList<Long>();
        this.hexList = new ArrayList<String>();
        this.setQueueSize(queueSize);
    }
    /**
     * Constructs converter with one conversion pair in queue.
     * @param maxQueueSize maximum number of conversion pairs in queue
     * @throws OutOfBoundsException if maxQueueSize is lower than conversion 
     * pairs in queue
     */
    public JavaConverter(int maxQueueSize) throws OutOfBoundsException{
        this(1,maxQueueSize);
    }
    /**
     * Implemented interface method to return max queue size.
     * @return max queue size
     */
    @Override
    public int getMaxQueueSize() {
        return maxQueueSize;
    }
    /**
     * Implemented interface's Dec to Hex cnversion method.
     */
    @Override
    public void convertToHex() {
        hexList.clear();
        decList.forEach(decValue -> {hexList.add(Long.toHexString(decValue));});
    }
    /**
     * Implemented interface's Hex to Dec cnversion method.
     */
    @Override
    public void convertToDec() {
        decList.clear();
        hexList.forEach(hexValue -> {decList.add(Long.parseLong(hexValue, 16));});
    }
    /**
     * Implemented interface's method to return numbers of conversion pairs in queue.
     * @return number of conversion pairs in queue
     */
    @Override
    public int getQueueSize() {
        return queueSize; 
    }
    /**
     * Implemented interface's method to change queue size.
     * @param size new queue size
     * @throws OutOfBoundsException if desired size is lower or higher than allowed
     */
    @Override
    final public void setQueueSize(int size) throws OutOfBoundsException {
        if(size == queueSize && size <= maxQueueSize){
            return;
        } else if(size > maxQueueSize || size <= 0) {
            throw new OutOfBoundsException();
        } 
        resizeLists(size);       
    }
    /**
     * Implemented interface's method to change queue size.
     * @param size new queue size
     * @throws OutOfBoundsException if desired size is lower or higher than allowed
     * @throws NumberFormatException if provided number value is not valid number
     */
    @Override
    final public void setQueueSize(String size) throws OutOfBoundsException, NumberFormatException {
        int sizeN = Integer.parseInt(size);
        setQueueSize(sizeN);       
    }
    /**
     * Implemented interface's method to set hex value of desired conversion pair.
     * @param index index of conversion pair
     * @param value new hex value
     * @throws OutOfBoundsException if desired pair doesn't exist
     * @throws BadValueException if provided value couldn't be set
     */
    @Override
    public void setHexValue(int index, String value) throws OutOfBoundsException, BadValueException{
        if(index >= queueSize || index < 0){
            throw new OutOfBoundsException();
        }
        if(isHex(value)){
            hexList.set(index, value);
        }
        else{
            throw new BadValueException();
        }   
    }
    /**
     * Implemented interface's method to set hex value of desired conversion pair.
     * @param index index of conversion pair
     * @param value new hex value
     * @throws OutOfBoundsException if desired pair doesn't exist
     * @throws NumberFormatException if provided number value is not valid number
     * @throws BadValueException if provided value couldn't be set
     */
    @Override
    public void setHexValue(String index, String value) throws OutOfBoundsException, BadValueException, NumberFormatException{
        int indexN = Integer.parseInt(index);
        setHexValue(indexN, value);
    }
    /**
     * Implemented interface's method to set decimal value of desired conversion pair.
     * @param index index of conversion pair
     * @param value new decimal value
     * @throws OutOfBoundsException if desired pair doesn't exist
     * @throws BadValueException if provided value couldn't be set
     */
    @Override
    public void setDecValue(int index, long value) throws OutOfBoundsException, BadValueException{
        if(index >= queueSize || index < 0){
            throw new OutOfBoundsException();
        }
        if(value < 0){ //this model allows conversion on numbers >= 0
            throw new BadValueException();
        }
        decList.set(index, value);
    }
    /**
     * Implemented interface's method to set decimal value of desired conversion pair.
     * @param index index of conversion pair
     * @param value new decimal value
     * @throws OutOfBoundsException if desired pair doesn't exist
     * @throws BadValueException if provided value couldn't be set
     */
    @Override
    public void setDecValue(String index, String value) throws OutOfBoundsException, BadValueException, NumberFormatException {
          int indexN = Integer.parseInt(index); 
          long valueL = Long.parseLong(value);
          setDecValue(indexN, valueL);
    }
    /**
     * Implemented interface's method that returns desired conversion pair.
     * @param index index of desired conversion pair
     * @return conversion pair as ConvertedPair object
     * @throws OutOfBoundsException if desired pair doesn't exist
     */
    @Override
    public ConvertedPair getPair(int index) throws OutOfBoundsException{
        if(index >= queueSize || index < 0){
            throw new OutOfBoundsException();
        }
        return new ConvertedPair(hexList.get(index), decList.get(index));
    }
    /**
     * Implemented interface's method that returns desired conversion pair.
     * @param index index of desired conversion pair
     * @return conversion pair as ConvertedPair object
     * @throws OutOfBoundsException if desired pair doesn't exist
     */
    @Override
    public ConvertedPair getPair(String index) throws OutOfBoundsException,NumberFormatException{
        int indexN = Integer.parseInt(index);
        return getPair(indexN);
    }
    /**
     * Implemented interface's method that returns all conversion pairs in queue.
     * @return Array of ConvertedPair objects representing queue elements
     */
    @Override
    public ConvertedPair[] getAllPairs() {
        int index = 0;
        ConvertedPair[] pairs = new ConvertedPair[hexList.size()];
        for(String hex : hexList){
            pairs[index] = new ConvertedPair(hex, decList.get(index));
            index++;
        }
        return pairs;
    }
    /**
     * Internal method responsible for resizing internal queue structures.
     * @param size new queue size
     */
    private void resizeLists(int size){
        if(size > queueSize){
            hexList.ensureCapacity(size);
            for(int index = queueSize; index < size; ++index){
                hexList.add("0");
                decList.add(new Long(0));
            }
        }
        else 
            if(queueSize > size){
                hexList.subList(size, hexList.size()).clear();
                decList.subList(size, decList.size()).clear();
                hexList.trimToSize();
                decList.trimToSize();
        }
        queueSize = size;
    }
    /**
     * Internal method to check if provided String is hex number.
     * It also checks if provided hex number fits in Long variable.
     * @param hex hex humber to check
     * @return true if provided number is acceptable, false otherwise
     */
    private boolean isHex(String hex){
        if(hex == null)
            return false;
        Pattern hexPattern = Pattern.compile("[a-fA-F0-9]{1,16}");
        Matcher hexMatcher = hexPattern.matcher(hex);
        if(hexMatcher.matches()){
            if(hex.length() == 16){
                //ensure that hex number isn't bigger than long max value
                if(!(hex.charAt(0) < '8'))
                    return false;
            }
            return true;
        }
        else{
            return false;
        }
    }
    /**
     * Field contains information how many conversion pairs are currently in queue.
     */
    private int queueSize;
    /**
     * Max allowed queue size in this particular implementation's object.
     */
    private int maxQueueSize;
    /**
     * Internal structure to store hex values of conversion pairs.
     */
    private ArrayList<String> hexList;
    /**
     * Internal structure to store decimal values of conversion pairs.
     */
    private ArrayList<Long> decList;

    
}
