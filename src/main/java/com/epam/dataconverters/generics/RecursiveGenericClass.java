package com.epam.dataconverters.generics;

public class RecursiveGenericClass<T extends RecursiveGenericClass<T>>  implements Comparable<T> {

    @Override
    public int compareTo(T o) {
        return 0;
    }
}
