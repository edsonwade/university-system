package code.with.vanilson.studentmanagement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


//@SpringBootTest
class StudentManagementApplicationTests {

    @Test
    void testExecutionTime() {
        int[] actual = {1, 2, 3, 4, 5};
        int[] expected = new int[5];

        long startTime = System.currentTimeMillis(); // Start measuring time

        // Perform the operation you want to measure
        IntStream.iterate(actual.length - 1, i -> i >= 0, i -> i - 1)
                .forEach(i -> expected[i] = actual[i]);

        long endTime = System.currentTimeMillis(); // End measuring time

        // Assert the result to ensure correctness
        assertArrayEquals(expected, actual);

        // Calculate and print the execution time
        long executionTime = endTime - startTime;
        System.out.println("Execution time: " + executionTime + " milliseconds");
    }

    @Test
    void testArrayCopyWithIteration() {
        int[] actual = {1, 2, 3, 4, 5};
        int[] expected = new int[5];

        long startTime = System.currentTimeMillis();

        // Method 1: Using IntStream.iterate to copy in reverse order
        for (int i = actual.length - 1; i >= 0; i--) {
            expected[i] = actual[i];
        }

        long endTime = System.currentTimeMillis();

        assertArrayEquals(expected, actual);

        long executionTime = endTime - startTime;
        System.out.println("Method 1 Execution time: " + executionTime + " milliseconds");
    }

}
