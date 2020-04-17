package eu.trisquare.bytemapper;

/**
 * Used when value's first byte index is lower than 0.
 */
public class NegativeIndexException extends IllegalArgumentException {

    NegativeIndexException(int index) {
        super(String.format(
                "Byte index must be positive! (%d was provided)",
                index
        ));
    }

}
