import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.memory.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {
    private InMemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
    }

    @Test
    void saveAndFindById_ShouldReturnSavedUser() {
        // Arrange
        User user = new User("jane", "password");

        // Act
        int id = repository.save(user);
        User savedUser = repository.findById(id);

        // Assert
        assertEquals(user.getUsername(), savedUser.getUsername());
        assertEquals(user.getPassword(), savedUser.getPassword());
    }
    @Test
    void deleteByUserId() {
        // Arrange
        User user = new User("jane", "password");

        // Act
        int id = repository.save(user);
        repository.deleteById(id);
        User savedUser = repository.findById(id);
        // Assert
        assertNull(savedUser);
    }
}