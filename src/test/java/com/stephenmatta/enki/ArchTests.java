package com.stephenmatta.enki;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ArchTests {

    @Test
    public void archTest() {
        ApplicationModules.of(EnkiApplication.class).verify();
    }
}
