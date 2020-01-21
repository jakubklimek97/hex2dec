package pl.jklimek.hex2decBackend.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to highlight MVC implementing classes.   
 * @author Jakub Klimek
 * @version 2
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@Documented
public @interface MVCImplementation {
    /**
     * Enumaration to express type of MVC Implementation
     * @author  Jakub Klimek
     * @version 2
     */
    public enum Component {
        /**
         * Names Model component
         */
        MODEL("Model"), 
        /**
         * Names View component
         */
        VIEW("View"),
        /**
         * Names Controller component
         */
        CONTROLLER("Controller");
        /**
         * Default Constructor
         * @param stringValue type of implementation
         */
        Component(String stringValue){
            this.stringValue = stringValue;
        }
        /**
         * Type of implementation
         */
        String stringValue;
        /**
         * Returns type of MVC Implementation
         * @return Type of implementation as String
         */
        String ToString(){
            return stringValue;
        }
        
    }
    /**
     * Informs what component of MVC class implements.
     * @return component name
     */
    Component value();
}
