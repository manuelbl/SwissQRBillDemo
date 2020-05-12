//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.api;

import net.codecrete.qrbill.web.model.BillFormat;

/**
 * Data class to generate bill ID
 */
public class BillPayload {

    private int version;
    private BillFormat format;
    private String qrText;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public BillFormat getFormat() {
        return format;
    }

    public void setFormat(BillFormat format) {
        this.format = format;
    }

    public String getQrText() {
        return qrText;
    }

    public void setQrText(String qrText) {
        this.qrText = qrText;
    }
}
