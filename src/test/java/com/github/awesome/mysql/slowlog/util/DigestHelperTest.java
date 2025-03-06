package com.github.awesome.mysql.slowlog.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DigestHelperTest {

    @Test
    void testNewInstance() {
        UnsupportedOperationException e = assertThrows(UnsupportedOperationException.class, DigestHelper::new);
        assertEquals("Utility class should not be instantiated", e.getMessage());
    }

}
