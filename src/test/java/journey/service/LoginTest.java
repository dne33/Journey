package journey.service;

import org.junit.jupiter.api.BeforeAll;
import journey.repository.DatabaseManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginTest {
    @BeforeAll
    public static void resetDb() throws Exception {
        DatabaseManager databaseManager = DatabaseManager.initialiseWithUrl("/test.db");
    }
    @Test
    void digitNameTest() {
        boolean valid = LoginService.checkUser("Test1");
        assertFalse(valid);
    }

    @Test
    void specialNameTest() {
        boolean valid = LoginService.checkUser("Test!");
        assertFalse(valid);
    }

    @Test
    void spaceNameTest() {
        boolean valid = LoginService.checkUser("Test User");
        assertTrue(valid);
    }
}
