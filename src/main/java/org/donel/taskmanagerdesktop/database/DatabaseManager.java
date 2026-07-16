package org.donel.taskmanagerdesktop.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:taskmanager.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            initializeTables();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void initializeTables() throws SQLException {
        String[] createTableStatements = {
            "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY," +
                "email TEXT NOT NULL UNIQUE," +
                "displayName TEXT NOT NULL," +
                "profilePictureUrl TEXT," +
                "lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")",

            "CREATE TABLE IF NOT EXISTS projects (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "description TEXT," +
                "type TEXT NOT NULL," +
                "status TEXT NOT NULL," +
                "dueDate TEXT," +
                "pastDue INTEGER DEFAULT 0," +
                "ownerId INTEGER NOT NULL," +
                "lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY(ownerId) REFERENCES users(id)" +
            ")",

            "CREATE TABLE IF NOT EXISTS deliverables (" +
                "id INTEGER PRIMARY KEY," +
                "projectId INTEGER NOT NULL," +
                "title TEXT NOT NULL," +
                "description TEXT," +
                "status TEXT NOT NULL," +
                "dueDate TEXT," +
                "pastDue INTEGER DEFAULT 0," +
                "lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY(projectId) REFERENCES projects(id) ON DELETE CASCADE" +
            ")",

            "CREATE TABLE IF NOT EXISTS tasks (" +
                "id INTEGER PRIMARY KEY," +
                "deliverableId INTEGER NOT NULL," +
                "title TEXT NOT NULL," +
                "status TEXT NOT NULL," +
                "dueDate TEXT," +
                "pastDue INTEGER DEFAULT 0," +
                "lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY(deliverableId) REFERENCES deliverables(id) ON DELETE CASCADE" +
            ")",

            "CREATE TABLE IF NOT EXISTS projectMembers (" +
                "id INTEGER PRIMARY KEY," +
                "projectId INTEGER NOT NULL," +
                "userId INTEGER NOT NULL," +
                "role TEXT NOT NULL," +
                "joinedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY(projectId) REFERENCES projects(id) ON DELETE CASCADE," +
                "FOREIGN KEY(userId) REFERENCES users(id)," +
                "UNIQUE(projectId, userId)" +
            ")",

            "CREATE TABLE IF NOT EXISTS session (" +
                "key TEXT PRIMARY KEY," +
                "value TEXT," +
                "expiresAt TIMESTAMP" +
            ")",

            "CREATE TABLE IF NOT EXISTS preferences (" +
                "key TEXT PRIMARY KEY," +
                "value TEXT" +
            ")",

            "CREATE TABLE IF NOT EXISTS syncLog (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "entityType TEXT NOT NULL," +
                "entityId INTEGER NOT NULL," +
                "action TEXT NOT NULL," +
                "syncedAt TIMESTAMP," +
                "UNIQUE(entityType, entityId, action)" +
            ")"
        };

        try (Statement stmt = connection.createStatement()) {
            for (String sql : createTableStatements) {
                stmt.execute(sql);
            }

            String[] createIndexes = {
                "CREATE INDEX IF NOT EXISTS idx_projects_ownerId ON projects(ownerId)",
                "CREATE INDEX IF NOT EXISTS idx_deliverables_projectId ON deliverables(projectId)",
                "CREATE INDEX IF NOT EXISTS idx_tasks_deliverableId ON tasks(deliverableId)",
                "CREATE INDEX IF NOT EXISTS idx_projectMembers_projectId ON projectMembers(projectId)",
                "CREATE INDEX IF NOT EXISTS idx_projectMembers_userId ON projectMembers(userId)",
                "CREATE INDEX IF NOT EXISTS idx_syncLog_entityType ON syncLog(entityType)"
            };

            for (String sql : createIndexes) {
                stmt.execute(sql);
            }
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
