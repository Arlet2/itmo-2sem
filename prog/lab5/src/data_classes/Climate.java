package data_classes;

public enum Climate {
    RAIN_FOREST (0),
    HUMIDSUBTROPICAL (1),
    SUBARCTIC (2);
    private int value;
    Climate (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
