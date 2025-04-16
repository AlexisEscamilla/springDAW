package com.example.rolas.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collections;

public class UserDetailsDeserializer extends JsonDeserializer<UserDetails> {

    @Override
    public UserDetails deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // Verifica si el campo "sender" está presente
        JsonNode senderNode = node.get("sender");
        if (senderNode == null || senderNode.get("username") == null) {
            throw new IllegalArgumentException("El campo 'sender.username' es obligatorio en el JSON");
        }

        String username = senderNode.get("username").asText();

        // Crea un objeto User con los datos necesarios
        return new User(username, "", Collections.emptyList());
    }
}