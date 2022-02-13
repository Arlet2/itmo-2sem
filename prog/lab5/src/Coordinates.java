public class Coordinates {
    private float x; //Значение поля должно быть больше -407
    private Integer y; //Поле не может быть null

    public void setX(final float x) throws IncorrectValueException {
        if(x <= -407)
            throw new IncorrectValueException("Значение координаты X должно быть больше -407");
        this.x = x;
    }

    public void setY(final Integer y) throws NullValueException {
        if(y == null)
            throw new NullValueException();
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    @Override
    public String toString() {
        return "x: "+x+" y: "+y;
    }
}
