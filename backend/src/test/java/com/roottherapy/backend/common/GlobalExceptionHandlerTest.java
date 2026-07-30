package com.roottherapy.backend.common;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest {

    //JUNIT-ERR-001: IllegalArgumentException returnsconflict with error responced
    @Test
    void illegalArgumentException_returnsConflictResponse() {

        GlobalExceptionHandler handler =
                new GlobalExceptionHandler();

        IllegalArgumentException exception =
                new IllegalArgumentException(
                        "Email already exists"
                );

        GlobalExceptionHandler.ErrorResponse response =
                handler.handleIllegalArgumentException(exception);

        assertEquals(
                "Email already exists",
                response.message()
        );
    }

    //JUNIT-ERR-002: ErrorResponse contains the original exception message
@Test
void errorResponse_containsOriginalMessage() {

    GlobalExceptionHandler.ErrorResponse response =
            new GlobalExceptionHandler.ErrorResponse(
                    "User not found"
            );

    assertEquals(
            "User not found",
            response.message()
    );
}
}