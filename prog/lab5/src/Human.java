public class Human {
    private Long age; //Значение поля должно быть больше 0
    private java.time.LocalDateTime birthday;
    Human(final Long age, final java.time.LocalDateTime birthday) throws IncorrectValueException {
        setAge(age);
        setBirthday(birthday);
    }

    public void setAge(final Long age) throws IncorrectValueException {
        if(age <= 0)
            throw new IncorrectValueException("Возраст должен быть больше 0");
        this.age = age;
    }
    public void setBirthday(final java.time.LocalDateTime birthday) {
        this.birthday = birthday;
    }

    public Long getAge() {
        return age;
    }

    public java.time.LocalDateTime getBirthday() {
        return birthday;
    }

    @Override
    public String toString() {
        return "age: "+age+" birthday: "+birthday;
    }
}
