//
// Swiss QR Bill Generator
// Copyright (c) 2020 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.model;

import javax.json.bind.annotation.JsonbProperty;
import java.util.Objects;

public class AlternativeScheme {

    private String name;
    private String instruction;

    /**
     * Scheme name
     **/

    @JsonbProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Payment instruction
     **/

    @JsonbProperty("instruction")
    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AlternativeScheme alternativeScheme = (AlternativeScheme) o;
        return Objects.equals(name, alternativeScheme.name) &&
                Objects.equals(instruction, alternativeScheme.instruction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, instruction);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AlternativeScheme {\n");

        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    instruction: ").append(toIndentedString(instruction)).append("\n");
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

