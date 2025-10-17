package com.icc.clinic.validation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FieldValidatorsFuzzTest {

    @Test
    void testSafeInputFuzz() {
        String[] unsafeInputs = {
            "DROP TABLE users;",
            "<script>alert('x')</script>",
            "Robert'); DROP TABLE Students;--",
            "\" OR \"1\"=\"1",
            "' OR '1'='1",
            "DELETE FROM accounts WHERE 1=1"
        };

        for (String input : unsafeInputs) {
            boolean result = FieldValidators.isSafeInput(input);
            assertFalse(result, "Unsafe input passed validation: " + input);
        }
    }
}
