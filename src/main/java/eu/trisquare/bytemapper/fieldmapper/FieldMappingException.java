package eu.trisquare.bytemapper.fieldmapper;

import eu.trisquare.bytemapper.impl.MappingException;

/**
 * Exception used when mapping of specific field was unsuccessful
 */
class FieldMappingException extends MappingException {

    FieldMappingException(String message) {
        super(message);
    }

}
