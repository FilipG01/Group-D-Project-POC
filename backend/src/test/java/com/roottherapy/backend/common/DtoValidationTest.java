package com.roottherapy.backend.common;
import com.roottherapy.backend.auth.dto.ChangePasswordRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

public class DtoValidationTest {
    private Validator validator;

@BeforeEach
void setUp() {

    ValidatorFactory factory =
            Validation.buildDefaultValidatorFactory();

    validator = factory.getValidator();
}

//JUNIT-ERR-003: Required password fields reject null and empty and blank values
@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = {" ", "    "})
void requiredStringFields_nullEmptyOrBlank_areInvalid(String value) {

    ChangePasswordRequest request =
            new ChangePasswordRequest(
                    value,
                    value,
                    value
            );

    Set<ConstraintViolation<ChangePasswordRequest>> violations =
            validator.validate(request);

    assertFalse(violations.isEmpty());
}
//JUNIT-ERR-004: Password fields rejects values over max which is 72, test setting pass to 73
@Test
void lengthRestrictedFields_overMaximum_areInvalid() {

    String overLengthPassword = "a".repeat(73);

    ChangePasswordRequest request =
            new ChangePasswordRequest(
                    overLengthPassword,
                    overLengthPassword,
                    overLengthPassword
            );

    Set<ConstraintViolation<ChangePasswordRequest>> violations =
            validator.validate(request);

    assertFalse(violations.isEmpty());
}
}
