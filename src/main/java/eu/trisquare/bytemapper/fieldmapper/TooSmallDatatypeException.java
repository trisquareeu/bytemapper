package eu.trisquare.bytemapper.fieldmapper;

/**
 * Used when selected data range value cannot fit into provided field type
 */
public class TooSmallDatatypeException extends IllegalArgumentException {

    TooSmallDatatypeException(Class<?> dataType, int maximumSupportedSize, int requestedSize) {
        super(String.format(
                "For type %s maximum allowed size is %d, but requested parsing of %d bytes. Would you like to use different data type?",
                dataType.getSimpleName(),
                maximumSupportedSize,
                requestedSize
        ));
    }

}
