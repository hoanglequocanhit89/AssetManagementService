package com.rookie.asset_management.service;

import com.rookie.asset_management.service.impl.ExampleServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class is a placeholder for the ExampleServiceTest.
 * This should not a public class, use the default instead.
 * The test class should be an annotated with <b>@ExtendWith(MockitoExtension.class)</b> to enable Mockito support.
 */
@ExtendWith(MockitoExtension.class)
class ExampleServiceTest {
    // This is a placeholder for the actual implementation of the test class.
    // use InjectMocks to inject the ExampleServiceImpl into the test class because it is a bean.
    @InjectMocks
    private ExampleServiceImpl exampleService;

    // Add your test methods here
    // Example test method
    @Test
    @DisplayName("Test example method")
    void testExampleMethod() {
        // Your test logic here
        String result = exampleService.exampleMethod();
        assertEquals("Hello from ExampleServiceImpl!", result);
     }
}

