package com.boha.skunk.data.rapyd;

public class PaymentOption {
    private String name;
    private Type type;
    private Regex regex;
    private String description;
    private IsRequired isRequired;
    private Boolean isUpdatable;
    private RequiredField[] requiredFields;

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

    public Regex getRegex() {
        return regex;
    }

    public void setRegex(Regex value) {
        this.regex = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public IsRequired getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(IsRequired value) {
        this.isRequired = value;
    }

    public Boolean getIsUpdatable() {
        return isUpdatable;
    }

    public void setIsUpdatable(Boolean value) {
        this.isUpdatable = value;
    }

    public RequiredField[] getRequiredFields() {
        return requiredFields;
    }

    public void setRequiredFields(RequiredField[] value) {
        this.requiredFields = value;
    }
}
