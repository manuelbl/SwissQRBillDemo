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

public class BillFormat {


    /**
     * Language of the generated QR bill
     */
    public enum LanguageEnum {
        DE,
        FR,
        IT,
        EN,
        RM
    }

    @Provider
    public static class LanguageEnumMapper implements JsonbAdapter<LanguageEnum, String> {

        @Override
        public String adaptToJson(final LanguageEnum language) throws Exception {
            return language.name().toLowerCase(Locale.US);
        }

        @Override
        public LanguageEnum adaptFromJson(final String str) throws Exception {
            return LanguageEnum.valueOf(str.toUpperCase(Locale.US));
        }
    }

    @JsonbTypeAdapter(LanguageEnumMapper.class)
    private LanguageEnum language;

    /**
     * Graphics format of generated QR bill
     */
    public enum GraphicsFormatEnum {
        SVG,
        PDF
    }

    @Provider
    public static class GraphicsFormatEnumMapper implements JsonbAdapter<GraphicsFormatEnum, String> {

        @Override
        public String adaptToJson(final GraphicsFormatEnum graphicsFormat) {
            return graphicsFormat.name().toLowerCase(Locale.US);
        }

        @Override
        public GraphicsFormatEnum adaptFromJson(final String str) {
            return GraphicsFormatEnum.valueOf(str.toUpperCase(Locale.US));
        }
    }

    @JsonbTypeAdapter(GraphicsFormatEnumMapper.class)
    private GraphicsFormatEnum graphicsFormat;

    /**
     * Output size of generated QR bill
     */
    public enum OutputSizeEnum {
        A4_PORTRAIT_SHEET,
        QR_BILL_ONLY,
        QR_BILL_WITH_HORIZONTAL_LINE,
        QR_CODE_ONLY
    }

    @Provider
    public static class OutputSizeEnumMapper implements JsonbAdapter<OutputSizeEnum, String> {

        @Override
        public String adaptToJson(final OutputSizeEnum outputSize) {
            return outputSize.name().replace('_', '-').toLowerCase(Locale.US);
        }

        @Override
        public OutputSizeEnum adaptFromJson(final String str) {
            return OutputSizeEnum.valueOf(str.toUpperCase(Locale.US).replace('-', '_'));
        }
    }

    @JsonbTypeAdapter(OutputSizeEnumMapper.class)
    private OutputSizeEnum outputSize;

    /**
     * Type of separator lines above and between payment part and receipt
     */
    public enum SeparatorTypeEnum {
        NONE,
        SOLID_LINE,
        SOLID_LINE_WITH_SCISSORS,
        DASHED_LINE,
        DASHED_LINE_WITH_SCISSORS,
        DOTTED_LINE,
        DOTTED_LINE_WITH_SCISSORS
    }

    @Provider
    public static class SeparatorTypeEnumMapper implements JsonbAdapter<SeparatorTypeEnum, String> {

        @Override
        public String adaptToJson(final SeparatorTypeEnum separatorType) {
            return separatorType.name().replace('_', '-').toLowerCase(Locale.US);
        }

        @Override
        public SeparatorTypeEnum adaptFromJson(final String str) {
            return SeparatorTypeEnum.valueOf(str.toUpperCase(Locale.US).replace('-', '_'));
        }
    }

    @JsonbTypeAdapter(SeparatorTypeEnumMapper.class)
    private SeparatorTypeEnum separatorType;

    private String fontFamily;

    /**
     * Language of the generated QR bill
     **/

    @JsonbProperty("language")
    public LanguageEnum getLanguage() {
        return language;
    }

    public void setLanguage(LanguageEnum language) {
        this.language = language;
    }

    /**
     * Graphics format of generated QR bill
     **/

    @JsonbProperty("graphicsFormat")
    public GraphicsFormatEnum getGraphicsFormat() {
        return graphicsFormat;
    }

    public void setGraphicsFormat(GraphicsFormatEnum graphicsFormat) {
        this.graphicsFormat = graphicsFormat;
    }

    /**
     * Output size of generated QR bill
     **/

    @JsonbProperty("outputSize")
    public OutputSizeEnum getOutputSize() {
        return outputSize;
    }

    public void setOutputSize(OutputSizeEnum outputSize) {
        this.outputSize = outputSize;
    }

    /**
     * Type of separator lines above and between payment part and receipt
     **/

    @JsonbProperty("separatorType")
    public SeparatorTypeEnum getSeparatorType() {
        return separatorType;
    }

    public void setSeparatorType(SeparatorTypeEnum separatorType) {
        this.separatorType = separatorType;
    }

    /**
     * Font family used for text
     **/

    @JsonbProperty("fontFamily")
    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BillFormat billFormat = (BillFormat) o;
        return Objects.equals(language, billFormat.language) &&
                Objects.equals(graphicsFormat, billFormat.graphicsFormat) &&
                Objects.equals(outputSize, billFormat.outputSize) &&
                Objects.equals(separatorType, billFormat.separatorType) &&
                Objects.equals(fontFamily, billFormat.fontFamily);
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, graphicsFormat, outputSize, separatorType, fontFamily);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class BillFormat {\n");

        sb.append("    language: ").append(toIndentedString(language)).append("\n");
        sb.append("    graphicsFormat: ").append(toIndentedString(graphicsFormat)).append("\n");
        sb.append("    outputSize: ").append(toIndentedString(outputSize)).append("\n");
        sb.append("    separatorType: ").append(toIndentedString(separatorType)).append("\n");
        sb.append("    fontFamily: ").append(toIndentedString(fontFamily)).append("\n");
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

