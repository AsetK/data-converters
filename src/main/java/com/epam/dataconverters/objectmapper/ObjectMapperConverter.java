package com.epam.dataconverters.objectmapper;

import com.epam.dataconverters.AbstractPerson;
import com.epam.dataconverters.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ObjectMapperConverter {

    private ObjectMapper objectMapper = new ObjectMapper();

    public String convertToJSON(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public Object convertToObject (String json) throws IOException {
        return objectMapper.readValue(json, AbstractPerson.class);
    }
}
