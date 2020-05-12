package net.codecrete.qrbill.web.model;

import javax.json.bind.annotation.JsonbProperty;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class QrBill {

    private String version = "V2_0";
    private BigDecimal amount;
    private String currency = "CHF";
    private String account;
    private Address creditor;
    private String reference;
    private String unstructuredMessage;
    private String billInformation;
    private Address debtor;
    private List<AlternativeScheme> alternativeSchemes;
    private BillFormat format;

    /**
     * QR bill specification version
     **/

    @JsonbProperty("version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Bill amount
     **/

    @JsonbProperty("amount")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Bill currency
     **/

    @JsonbProperty("currency")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Creditor&#39;s account
     **/

    @JsonbProperty("account")
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    /**
     *
     **/

    @JsonbProperty("creditor")
    public Address getCreditor() {
        return creditor;
    }

    public void setCreditor(Address creditor) {
        this.creditor = creditor;
    }

    /**
     * Payment reference number (QR/ISR reference number or ISO 11649 creditor reference)
     **/

    @JsonbProperty("reference")
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Message for the bill recipient
     **/

    @JsonbProperty("unstructuredMessage")
    public String getUnstructuredMessage() {
        return unstructuredMessage;
    }

    public void setUnstructuredMessage(String unstructuredMessage) {
        this.unstructuredMessage = unstructuredMessage;
    }

    /**
     * Structured bill information for recipient
     **/

    @JsonbProperty("billInformation")
    public String getBillInformation() {
        return billInformation;
    }

    public void setBillInformation(String billInformation) {
        this.billInformation = billInformation;
    }

    /**
     *
     **/

    @JsonbProperty("debtor")
    public Address getDebtor() {
        return debtor;
    }

    public void setDebtor(Address debtor) {
        this.debtor = debtor;
    }

    /**
     *
     **/

    @JsonbProperty("alternativeSchemes")
    public List<AlternativeScheme> getAlternativeSchemes() {
        return alternativeSchemes;
    }

    public void setAlternativeSchemes(List<AlternativeScheme> alternativeSchemes) {
        this.alternativeSchemes = alternativeSchemes;
    }

    /**
     *
     **/

    @JsonbProperty("format")
    public BillFormat getFormat() {
        return format;
    }

    public void setFormat(BillFormat format) {
        this.format = format;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QrBill qrBill = (QrBill) o;
        return Objects.equals(version, qrBill.version) &&
                Objects.equals(amount, qrBill.amount) &&
                Objects.equals(currency, qrBill.currency) &&
                Objects.equals(account, qrBill.account) &&
                Objects.equals(creditor, qrBill.creditor) &&
                Objects.equals(reference, qrBill.reference) &&
                Objects.equals(unstructuredMessage, qrBill.unstructuredMessage) &&
                Objects.equals(billInformation, qrBill.billInformation) &&
                Objects.equals(debtor, qrBill.debtor) &&
                Objects.equals(alternativeSchemes, qrBill.alternativeSchemes) &&
                Objects.equals(format, qrBill.format);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, amount, currency, account, creditor, reference, unstructuredMessage, billInformation, debtor, alternativeSchemes, format);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class QrBill {\n");

        sb.append("    version: ").append(toIndentedString(version)).append("\n");
        sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
        sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
        sb.append("    account: ").append(toIndentedString(account)).append("\n");
        sb.append("    creditor: ").append(toIndentedString(creditor)).append("\n");
        sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
        sb.append("    unstructuredMessage: ").append(toIndentedString(unstructuredMessage)).append("\n");
        sb.append("    billInformation: ").append(toIndentedString(billInformation)).append("\n");
        sb.append("    debtor: ").append(toIndentedString(debtor)).append("\n");
        sb.append("    alternativeSchemes: ").append(toIndentedString(alternativeSchemes)).append("\n");
        sb.append("    format: ").append(toIndentedString(format)).append("\n");
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

