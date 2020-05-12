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
import java.util.Locale;
import java.util.Objects;

public class Address {

    @JsonbTypeAdapter(AddressTypeEnumMapper.class)
    private AddressTypeEnum addressType;
    private String name;
    private String addressLine1;
    private String addressLine2;
    private String street;
    private String houseNo;
    private String town;
    private String postalCode;
    private String countryCode;

    /**
     * Address type
     **/

    @JsonbProperty("addressType")
    public AddressTypeEnum getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressTypeEnum addressType) {
        this.addressType = addressType;
    }

    /**
     * First, middle and last name of a natural person or company or organization name of a legal person
     **/

    @JsonbProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * First address line containing street name, house number and P.O. box
     **/

    @JsonbProperty("addressLine1")
    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    /**
     * Second address line containing postal code and town
     **/

    @JsonbProperty("addressLine2")
    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    /**
     * Street name (without the house number)
     **/

    @JsonbProperty("street")
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * House or building number
     **/

    @JsonbProperty("houseNo")
    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    /**
     * Town or city name
     **/

    @JsonbProperty("town")
    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    /**
     * Postal code
     **/

    @JsonbProperty("postalCode")
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Two letter ISO country code
     **/

    @JsonbProperty("countryCode")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Address address = (Address) o;
        return Objects.equals(addressType, address.addressType) &&
                Objects.equals(name, address.name) &&
                Objects.equals(addressLine1, address.addressLine1) &&
                Objects.equals(addressLine2, address.addressLine2) &&
                Objects.equals(street, address.street) &&
                Objects.equals(houseNo, address.houseNo) &&
                Objects.equals(town, address.town) &&
                Objects.equals(postalCode, address.postalCode) &&
                Objects.equals(countryCode, address.countryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressType, name, addressLine1, addressLine2, street, houseNo, town, postalCode, countryCode);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Address {\n");

        sb.append("    addressType: ").append(toIndentedString(addressType)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    addressLine1: ").append(toIndentedString(addressLine1)).append("\n");
        sb.append("    addressLine2: ").append(toIndentedString(addressLine2)).append("\n");
        sb.append("    street: ").append(toIndentedString(street)).append("\n");
        sb.append("    houseNo: ").append(toIndentedString(houseNo)).append("\n");
        sb.append("    town: ").append(toIndentedString(town)).append("\n");
        sb.append("    postalCode: ").append(toIndentedString(postalCode)).append("\n");
        sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
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

    /**
     * Address type
     */
    public enum AddressTypeEnum {
        UNDETERMINED,
        STRUCTURED,
        COMBINED_ELEMENTS,
        CONFLICTING
    }

    @Provider
    public static class AddressTypeEnumMapper implements JsonbAdapter<AddressTypeEnum, String> {

        @Override
        public String adaptToJson(final AddressTypeEnum addressType) throws Exception {
            return addressType.name().toLowerCase(Locale.US);
        }

        @Override
        public AddressTypeEnum adaptFromJson(final String str) throws Exception {
            return AddressTypeEnum.valueOf(str.toUpperCase(Locale.US));
        }
    }
}

