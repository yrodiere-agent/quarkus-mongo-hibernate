package io.quarkiverse.mongohibernate.deployment;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class ClassNamesTest {

    @Test
    void allClassNamesAreValid() {
        List<String> missing = new ArrayList<>();
        for (String className : ClassNames.CREATED_CONSTANTS) {
            try {
                // Inner classes use '$' in the binary name
                Class.forName(className, false, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
                missing.add(className);
            }
        }
        if (!missing.isEmpty()) {
            fail("The following class names in ClassNames are not found on the classpath: " + missing);
        }
    }
}
