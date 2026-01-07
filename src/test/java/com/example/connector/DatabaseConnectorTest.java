package com.example.connector;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for DatabaseConnector.
 */
public class DatabaseConnectorTest {

    @Test
    public void testConnectWithValidString() {
        DatabaseConnector connector = new DatabaseConnector("jdbc:postgresql://localhost:5432/testdb");
        assertTrue("Should connect with valid connection string", connector.connect());
    }

    @Test
    public void testConnectWithNullString() {
        DatabaseConnector connector = new DatabaseConnector(null);
        assertFalse("Should not connect with null connection string", connector.connect());
    }

    @Test
    public void testConnectWithEmptyString() {
        DatabaseConnector connector = new DatabaseConnector("");
        assertFalse("Should not connect with empty connection string", connector.connect());
    }

    @Test
    public void testGetConnectionString() {
        String connectionString = "jdbc:postgresql://localhost:5432/testdb";
        DatabaseConnector connector = new DatabaseConnector(connectionString);
        assertEquals("Should return correct connection string", connectionString, connector.getConnectionString());
    }
}
