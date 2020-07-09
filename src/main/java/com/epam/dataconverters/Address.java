package com.epam.dataconverters;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ToString
public class Address {
    private String city = "Karaganda";
    private String street;
}
