package pl.jklimek.hex2decBackend.model;;

/**
 * Exception that should be thrown whether unusable data is feeded to model
 * implementing object.
 * @author Jakub Klimek
 * @version 1
 * @see IConverterModel
 */
public class BadValueException extends Exception{
    /**
     * Default constructor.
     */
    public BadValueException() {
        super();
    }
    
}
