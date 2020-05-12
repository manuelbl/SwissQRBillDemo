//
// Swiss QR Bill Generator
// Copyright (c) 2020 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.model;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ValidationMessage {


    /**
     * Message type (error or warning)
     */
    public enum TypeEnum {
        ERROR,
        WARNING
    }

    @Provider
    public static class TypeEnumMapper implements JsonbAdapter<TypeEnum, String> {

        private final Map<String, TypeEnum> STRING_TO_ENUM = Map.of(
                "Error", TypeEnum.ERROR,
                "Warning", TypeEnum.WARNING
        );

        private final Map<TypeEnum, String> ENUM_TO_STRING = Map.of(
                TypeEnum.ERROR, "Error",
                TypeEnum.WARNING, "Warning"
        );

        @Override
        public String adaptToJson(final TypeEnum type) {
            return ENUM_TO_STRING.get(type);
        }

        @Override
        public TypeEnum adaptFromJson(final String str) {
            return STRING_TO_ENUM.get(str);
        }
    }

    @JsonbTypeAdapter(TypeEnumMapper.class)
    private TypeEnum type;

    private String messageKey;
    private List<String> messageParameters;
    private String message;
    private String field;

    /**
     * Message type (error or warning)
     **/

    @JsonbProperty("type")
    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    /**
     * Language independent message key
     **/

    @JsonbProperty("messageKey")
    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    /**
     * Variable parts of the message (if any)
     **/

    @JsonbProperty("messageParameters")
    public List<String> getMessageParameters() {
        return messageParameters;
    }

    public void setMessageParameters(List<String> messageParameters) {
        this.messageParameters = messageParameters;
    }

    /**
     * Localized message (incl. variable parts). The Accept-Language header field is used to select a suitable language.
     **/

    @JsonbProperty("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Affected field name. Examples are Examples are: \&quot;account\&quot;, \&quot;creditor.street\&quot;
     **/

    @JsonbProperty("field")
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ValidationMessage validationMessage = (ValidationMessage) o;
        return Objects.equals(type, validationMessage.type) &&
                Objects.equals(messageKey, validationMessage.messageKey) &&
                Objects.equals(messageParameters, validationMessage.messageParameters) &&
                Objects.equals(message, validationMessage.message) &&
                Objects.equals(field, validationMessage.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, messageKey, messageParameters, message, field);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ValidationMessage {\n");

        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    messageKey: ").append(toIndentedString(messageKey)).append("\n");
        sb.append("    messageParameters: ").append(toIndentedString(messageParameters)).append("\n");
        sb.append("    message: ").append(toIndentedString(message)).append("\n");
        sb.append("    field: ").append(toIndentedString(field)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

