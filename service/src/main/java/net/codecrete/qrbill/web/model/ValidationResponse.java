package net.codecrete.qrbill.web.model;

import javax.json.bind.annotation.JsonbProperty;
import java.util.List;
import java.util.Objects;

public class ValidationResponse {

    private Boolean valid;
    private List<ValidationMessage> validationMessages;
    private QrBill validatedBill;
    private String billID;
    private String qrCodeText;

    /**
     * Indicates if the bill data was valid or not
     **/

    @JsonbProperty("valid")
    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    /**
     *
     **/

    @JsonbProperty("validationMessages")
    public List<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }

    public void setValidationMessages(List<ValidationMessage> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     *
     **/

    @JsonbProperty("validatedBill")
    public QrBill getValidatedBill() {
        return validatedBill;
    }

    public void setValidatedBill(QrBill validatedBill) {
        this.validatedBill = validatedBill;
    }

    /**
     * Bill ID if the bill data was valid. Used to retrieve the QR bill as SVG or PDF.
     **/

    @JsonbProperty("billID")
    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    /**
     * Text embedded in QR code if the bill data was valid.
     **/

    @JsonbProperty("qrCodeText")
    public String getQrCodeText() {
        return qrCodeText;
    }

    public void setQrCodeText(String qrCodeText) {
        this.qrCodeText = qrCodeText;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ValidationResponse validationResponse = (ValidationResponse) o;
        return Objects.equals(valid, validationResponse.valid) &&
                Objects.equals(validationMessages, validationResponse.validationMessages) &&
                Objects.equals(validatedBill, validationResponse.validatedBill) &&
                Objects.equals(billID, validationResponse.billID) &&
                Objects.equals(qrCodeText, validationResponse.qrCodeText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid, validationMessages, validatedBill, billID, qrCodeText);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ValidationResponse {\n");

        sb.append("    valid: ").append(toIndentedString(valid)).append("\n");
        sb.append("    validationMessages: ").append(toIndentedString(validationMessages)).append("\n");
        sb.append("    validatedBill: ").append(toIndentedString(validatedBill)).append("\n");
        sb.append("    billID: ").append(toIndentedString(billID)).append("\n");
        sb.append("    qrCodeText: ").append(toIndentedString(qrCodeText)).append("\n");
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

