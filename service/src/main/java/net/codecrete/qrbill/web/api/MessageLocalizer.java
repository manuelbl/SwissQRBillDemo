//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.HttpHeaders;
import net.codecrete.qrbill.web.model.ValidationMessage;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@ApplicationScoped
public class MessageLocalizer {

    private final Locale defaultLocale;

    /**
     * Creates an instance.
     */
    public MessageLocalizer() {
        defaultLocale = new Locale("de", "CH");
    }

    public void translateMessages(List<ValidationMessage> messages, HttpHeaders headers) {

        List<Locale> locales = headers.getAcceptableLanguages();
        Locale locale;
        if (locales != null && !locales.isEmpty())
            locale = locales.get(0);
        else
            locale = defaultLocale;

        for (ValidationMessage message : messages) {
            message.setMessage(getLocalMessage(message.getMessageKey(), message.getMessageParameters(), locale));
        }
    }

    public String getLocalMessage(String messageKey, List<String> messageParameters, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);

        String message = bundle.getString(messageKey);
        if (messageParameters != null && messageParameters.size() > 0) {
            MessageFormat formatter = new MessageFormat(message, locale);
            message = formatter.format(messageParameters.toArray(new Object[0]));
        }

        return message;
    }
}
