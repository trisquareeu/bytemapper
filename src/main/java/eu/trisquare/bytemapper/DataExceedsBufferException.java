package eu.trisquare.bytemapper;

/**
 * Used when value's last byte index exceeds buffer limit.
 */
public class DataExceedsBufferException extends IllegalArgumentException {

    DataExceedsBufferException(int index, int size, int bufferLimit) {
        super(String.format(
                "Last byte index should not exceed buffer limit of %d bytes, but %d was calculated",
                bufferLimit, index + size
        ));
    }

}
