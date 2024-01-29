package com.boha.skunk.data.rapyd;

public class RequiredField {
    private String name;
    private Type type;
    private String regex;
    private String description;
    private boolean isRequired;
    private boolean isUpdatable;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type value) {
        this.type = value;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String value) {
        this.regex = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(boolean value) {
        this.isRequired = value;
    }

    public boolean getIsUpdatable() {
        return isUpdatable;
    }

    public void setIsUpdatable(boolean value) {
        this.isUpdatable = value;
    }
}
