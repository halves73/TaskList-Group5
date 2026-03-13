package cncs.academy.ess.repository.sql;

import cncs.academy.ess.model.TodoList;
import cncs.academy.ess.repository.TodoListsRepository;
import java.sql.*;
import java.util.List;
import org.apache.commons.dbcp2.BasicDataSource;

import java.util.ArrayList;

public class SQLTodoListsRepository implements TodoListsRepository {
    private final BasicDataSource dataSource;

    public SQLTodoListsRepository(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public TodoList findById(int todolistId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM lists WHERE id = ?")) {
            stmt.setInt(1, todolistId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTodoList(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find TodoList by ID", e);
        }
        return null;
    }

    @Override
    public List<TodoList> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM lists");
             ResultSet rs = stmt.executeQuery()) {
            List<TodoList> TodoLists = new ArrayList<>();
            while (rs.next()) {
                TodoLists.add(mapResultSetToTodoList(rs));
            }
            return TodoLists;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all TodoLists", e);
        }
    }

    @Override
    public List<TodoList> findAllByUserId(int userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM lists where owner_id = ?")) {
             stmt.setInt(1, userId);
             try (ResultSet rs = stmt.executeQuery()) {
                 List<TodoList> TodoLists = new ArrayList<>();
                 while (rs.next()) {
                     TodoLists.add(mapResultSetToTodoList(rs));
                 }
                 return TodoLists;
             }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all TodoLists", e);
        }
    }

    @Override
    public int save(TodoList todoList) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO lists (name, owner_id) VALUES (?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, todoList.getName());
            stmt.setInt(2, todoList.getOwnerId());
            stmt.executeUpdate();
            int generatedId = 0;
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
            return generatedId;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save TodoList", e);
        }
    }

    @Override
    public void update(TodoList todoList) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE lists SET name = ?, owner_id = ? where id = ?",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, todoList.getName());
            stmt.setInt(2, todoList.getOwnerId());
            stmt.setInt(3,todoList.getListId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update the TodoList", e);
        }
    }

    @Override
    public boolean deleteById(int listId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM lists WHERE id = ?")) {
            stmt.setInt(1, listId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete TodoList", e);
        }
        return false;
    }

    private TodoList mapResultSetToTodoList(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        int ownerId = rs.getInt("owner_id");
        return new TodoList(id, name, ownerId);
    }
}