package com.epam.dataconverters.transformer;

import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class SomeClass {

    private Multimap<String, String> multimap;
    private Map<String, Set<String>> map;

}
