package org.agilelovers.backend;

import java.io.File;
import java.io.IOException;

/**
 * Mock Database for testing purposes because querying during tests would be an expensive use of our tokens, so we will
 * be using pre-written queries and answers
 * <p>
 * The implementations of methods are the same, so testing should be the same as if we were using the actual database
 */
public class MockDatabase extends Database {
    /**
     * File where the queries and answers are stored
     */
    private static final File queryDatabase = new File("MockDatabase.JSON");

    /**
     * Initializes the mock database file with mock data (3 queries and answers).
     *
     * @throws IOException if file is not found
     */
    public MockDatabase() throws IOException {
        super(queryDatabase);

        //write mock data to file
        transcribeQueryIntoFile("title1", "question1", "answer1");
        transcribeQueryIntoFile("title2", "question2", "answer2");
        transcribeQueryIntoFile("title3", "question3", "answer3");
    }
}
