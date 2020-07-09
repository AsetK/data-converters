package com.epam.dataconverters;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ToString
public class Person extends AbstractPerson{
    private String firstName = "Aa";
    private String secondName = "Kk";
    private Integer age = 30;
    @Autowired
    private Address address;
}
