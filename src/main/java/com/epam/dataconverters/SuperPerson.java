package com.epam.dataconverters;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Getter
@Setter
@ToString
public class SuperPerson  extends AbstractPerson{
    String superName = "superPerson";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuperPerson that = (SuperPerson) o;
        return Objects.equals(superName, that.superName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(superName);
    }
}
