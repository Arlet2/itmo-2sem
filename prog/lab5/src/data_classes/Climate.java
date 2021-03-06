package data_classes;

public enum Climate {
    RAIN_FOREST (0),
    HUMIDSUBTROPICAL (1),
    SUBARCTIC (2);
    /**
     * that uses for enum sorting
     */
    private final int value;
    Climate (final int value) {
        this.value = value;
    }

    /**
     * Get value for sort
     * @return integer for sort
     */
    public int getValue() {
        return value;
    }
}
