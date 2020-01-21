package pl.jklimek.hex2decBackend.model;

import java.sql.SQLException;

/**
 * Interface represents Model in MVC pattern.
 * @author Jakub Klimek
 * @version 6
 */
public interface IConverterModel {
    /**
     * Implemented method should return maximum amount of numbers,
     * that can be queued for conversion.
     * @return maximum queue size
     */
    public int getMaxQueueSize();
    /**
     * Implemented method should perform dec to hex conversion 
     * on numbers in queue.
     */
    public void convertToHex();
    /**
     * Implemented method should perform hex to dec conversion
     * on numbers in queue.
     */
    public void convertToDec();
    /**
     * Implemented method should return amount of numbers in conversion queue.
     * @return amount of numbers in conversion queue
     */
    public int getQueueSize();
    /**
     * Implemented method should change model queue size.
     * @param size desired queue size
     * @throws OutOfBoundsException if desired size can't be set by model
     */
    public void setQueueSize(int size) throws OutOfBoundsException, SQLException; 
    /**
     * Implemented method should change model queue size.
     * @param size desired queue size as text
     * @throws OutOfBoundsException if desired size can't be set by model
     * @throws NumberFormatException if provided number value is not valid number
     * @throws SQLException when sql error occurs
     */
    public void setQueueSize(String size) throws OutOfBoundsException, NumberFormatException, SQLException;
    /**
     * Implemented method should change hex value in selected model's conversion pair.
     * @param index index of conversion pair
     * @param value new hex value
     * @throws OutOfBoundsException if desired pair doesn't exist
     * @throws BadValueException if provided value couldn't be set
     * @throws SQLException when sql error occurs
     */
    public void setHexValue(int index, String value) throws OutOfBoundsException, BadValueException, SQLException;
    /**
     * Implemented method should change hex value in selected model's conversion pair.
     * @param index index of conversion pair
     * @param value new hex value
     * @throws OutOfBoundsException if desired pair doesn't exist
     * @throws BadValueException if provided value couldn't be set
     * @throws NumberFormatException if provided number value is not valid number
     * @throws SQLException when sql error occurs
     */
    public void setHexValue(String index, String value) throws OutOfBoundsException, BadValueException, NumberFormatException, SQLException;
    /**
     * Implemented method should change decimal value in selected model's conversion pair.
     * @param index index of conversion pair
     * @param value new decimal value
     * @throws OutOfBoundsException if desired pair doesn't exist
     * @throws BadValueException if provided value couldn't be set
     * @throws SQLException when sql error occurs
     */
    public void setDecValue(int index, long value) throws OutOfBoundsException, BadValueException, SQLException;
    /**
     * Implemented method should change decimal value in selected model's conversion pair.
     * @param index index of conversion pair
     * @param value new decimal value
     * @throws OutOfBoundsException if desired pair doesn't exist
     * @throws BadValueException if provided value couldn't be set
     * @throws NumberFormatException if provided number values are not valid numbers
     * @throws SQLException when sql error occurs
     */
    public void setDecValue(String index, String value) throws OutOfBoundsException, BadValueException, NumberFormatException, SQLException;
    /**
     * Implemented method should counstruct and return conversion pair from 
     * internal model structures.
     * @param index index of conversion pair
     * @return (hex,dec) ConvertedPair object
     * @throws OutOfBoundsException if desired pair doesn't exist
     * @throws SQLException when sql error occurs
     */
    public ConvertedPair getPair(int index) throws OutOfBoundsException, SQLException;
    /**
     * Implemented method should counstruct and return conversion pair from 
     * internal model structures.
     * @param index index of conversion pair
     * @return (hex,dec) ConvertedPair object
     * @throws OutOfBoundsException if desired pair doesn't exist
     * @throws NumberFormatException if provided number value is not valid number
     * @throws SQLException when sql error occurs
     */
    public ConvertedPair getPair(String index) throws OutOfBoundsException, NumberFormatException, SQLException;
    /**
     * Implemented method should counstruct and return all conversion pairs from 
     * internal model structures.
     * @return array of ConvertedPair objects
     * @throws SQLException when sql error occurs
     */
    public ConvertedPair[] getAllPairs() throws SQLException;
}
