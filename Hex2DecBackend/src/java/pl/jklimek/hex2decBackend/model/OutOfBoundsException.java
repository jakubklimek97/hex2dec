package pl.jklimek.hex2decBackend.model;
/**
 * Exception that should be thrown if index/queue parameter is invalid.
 * @author Jakub Klimek
 * @version 1
 * @see IConverterModel
 */
public class OutOfBoundsException extends Exception{
    /**
     * Default constructor
     */
    public OutOfBoundsException() {
        super();
    }
    
}
