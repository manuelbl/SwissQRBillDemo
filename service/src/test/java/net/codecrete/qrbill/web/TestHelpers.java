//
// Swiss QR Bill Generator
// Copyright (c) 2023 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.assertj.core.api.Condition;

import java.nio.charset.StandardCharsets;

/**
 * Helpers for testing for SVG and PDF responses
 */
public class TestHelpers {

    /**
     * Response specification check for status code 200 and SVG content type.
     */
    public static final ResponseSpecification SVG = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectContentType("image/svg+xml")
            .build();

    /**
     * Response specification check for status code 200 and SVG content type.
     */
    public static final ResponseSpecification PDF = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectContentType("application/pdf")
            .build();

    /**
     * Tests if the string looks like valid SVG content.
     * @return AssertJ condition
     */
    public static Condition<String> svg() {
        return new Condition<>(
                s -> s.startsWith("<?xml") && s.contains("<svg"),
                "looks like valid SVG content");
    }

    /**
     * Tests if the string looks like valid PDF content.
     * @return AssertJ condition
     */
    public static Condition<byte[]> pdf() {
        return new Condition<>(a -> {
            if (a.length < 1000)
                return false;
            String text = new String(a, 0, 7, StandardCharsets.UTF_8);
            return text.equals("%PDF-1.");
        },"looks like valid PDF content");
    }
}
