//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.api;

import jakarta.ws.rs.*;

import java.util.List;

@Path(value = "/postal-codes")
public class PostalCodeApi {

    private final PostalCodeData postalCodeData;

    public PostalCodeApi(PostalCodeData postalCodeData) {
        this.postalCodeData = postalCodeData;
    }

    @GET
    @Path("/suggest")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public PostalCode[] suggestPostalCodes(@QueryParam("country") String country,
                                           @QueryParam("substring") String substring) {

        if (country == null)
            country = "";

        // get postal code
        List<PostalCodeData.PostalCode> postalCodeList = postalCodeData.suggestPostalCodes(country, substring);

        // convert result into API data structure
        int len = postalCodeList.size();
        PostalCode[] postalCodes = new PostalCode[len];
        for (int i = 0; i < len; i++) {
            PostalCode pc = new PostalCode();
            pc.setPostalCode(postalCodeList.get(i).code);
            pc.setTown(postalCodeList.get(i).town);
            postalCodes[i] = pc;
        }
        return postalCodes;
    }
}
