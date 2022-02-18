package data_classes;

public enum Government {
    KRITARCHY (0),
    MATRIARCHY (1),
    OLIGARCHY (2),
    PLUTOCRACY (3),
    REPUBLIC (4);
    private final int value;
    Government (int value) {
        this.value = value;
    }

    /**
     * Get value for sort enum
     * @return integer for sorting
     */
    public int getValue() {
        return value;
    }
}
