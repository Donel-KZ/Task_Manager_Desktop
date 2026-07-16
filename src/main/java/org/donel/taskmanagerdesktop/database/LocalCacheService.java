package org.donel.taskmanagerdesktop.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import org.donel.taskmanagerdesktop.Controllers.ProjectResponse;
import org.donel.taskmanagerdesktop.services.UserResponse;

public class LocalCacheService {
    private final DatabaseManager dbManager = DatabaseManager.getInstance();

    public void cacheUser(UserResponse user) {
        String sql = "INSERT OR REPLACE INTO users (id, email, displayName, profilePictureUrl) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, user.id());
            pstmt.setString(2, user.email());
            pstmt.setString(3, user.displayName());
            pstmt.setString(4, user.profilePictureUrl());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UserResponse getCachedUser(long userId) {
        String sql = "SELECT id, email, displayName, profilePictureUrl FROM users WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new UserResponse(
                        rs.getLong("id"),
                        rs.getString("email"),
                        rs.getString("displayName"),
                        rs.getString("profilePictureUrl")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void cacheProject(ProjectResponse project) {
        String sql = "INSERT OR REPLACE INTO projects (id, name, description, type, status, dueDate, pastDue, ownerId) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, project.id());
            pstmt.setString(2, project.name());
            pstmt.setString(3, project.description());
            pstmt.setString(4, project.type() != null ? project.type().toString() : "GROUP");
            pstmt.setString(5, project.status() != null ? project.status().toString() : "PENDING");
            pstmt.setString(6, project.dueDate());
            pstmt.setInt(7, project.pastDue() ? 1 : 0);
            pstmt.setLong(8, project.owner() != null ? project.owner().id() : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ProjectResponse> getCachedProjects() {
        List<ProjectResponse> projects = new ArrayList<>();
        String sql = "SELECT id, name, description, type, status, dueDate, pastDue, ownerId FROM projects";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                UserResponse owner = getCachedUser(rs.getLong("ownerId"));
                projects.add(new ProjectResponse(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("type"),
                    rs.getString("status"),
                    rs.getString("dueDate"),
                    rs.getInt("pastDue") == 1,
                    owner
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }

    public void clearCache() {
        String[] clearStatements = {
            "DELETE FROM syncLog",
            "DELETE FROM projectMembers",
            "DELETE FROM tasks",
            "DELETE FROM deliverables",
            "DELETE FROM projects",
            "DELETE FROM users"
        };
        try (Connection conn = dbManager.getConnection()) {
            for (String sql : clearStatements) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void storeAuthToken(String token) {
        String sql = "INSERT OR REPLACE INTO session (key, value) VALUES (?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "auth_token");
            pstmt.setString(2, token);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getAuthToken() {
        String sql = "SELECT value FROM session WHERE key = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "auth_token");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("value");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearAuthToken() {
        String sql = "DELETE FROM session WHERE key = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "auth_token");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void storePreference(String key, String value) {
        String sql = "INSERT OR REPLACE INTO preferences (key, value) VALUES (?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getPreference(String key) {
        String sql = "SELECT value FROM preferences WHERE key = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("value");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
