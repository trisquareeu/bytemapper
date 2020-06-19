package eu.trisquare.bytemapper.classmapper;

import eu.trisquare.bytemapper.impl.MappingException;

/**
 * Exception used when mapping of whole class was unsuccessful
 */
class ClassMappingException extends MappingException {

    ClassMappingException(String message) {
        super(message);
    }

    ClassMappingException(String message, Throwable cause) {
        super(message, cause);
    }

}
