package com.example.connector;

/**
 * Simple database connector for POC demonstration.
 */
public class DatabaseConnector {

    private String connectionString;

    public DatabaseConnector(String connectionString) {
        this.connectionString = connectionString;
    }

    /**
     * Connect to the database.
     * @return true if connection successful
     */
    public boolean connect() {
        // Simulated connection logic
        if (connectionString == null || connectionString.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Get the connection string.
     * @return connection string
     */
    public String getConnectionString() {
        return connectionString;
    }
}
