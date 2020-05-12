//
// Swiss QR Bill Generator
// Copyright (c) 2020 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import io.restassured.response.Response;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;

public class JsonHelper {
    static <T> T extract(Response response, Class<T> responseClass) {
        try (InputStream is = response.asInputStream()) {
            Jsonb jsonb = JsonbBuilder.create();
            return jsonb.fromJson(is, responseClass);
        } catch (IOException e) {
            assertThat("read response", false);
            return null;
        }
    }
}
