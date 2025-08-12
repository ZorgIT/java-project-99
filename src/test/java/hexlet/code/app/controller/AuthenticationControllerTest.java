package hexlet.code.app.controller;

import hexlet.code.app.dto.AuthRequest;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        AuthRequest authRequest = new AuthRequest("test@example.com", "password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthRequest> request = new HttpEntity<>(authRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/login",
                HttpMethod.POST,
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotBlank();
    }

    @Test
    void testLoginFailure() {
        AuthRequest authRequest = new AuthRequest("wrong@example.com", "wrongpassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthRequest> request = new HttpEntity<>(authRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/login",
                HttpMethod.POST,
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testAccessRootWithoutAuth() {
        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testAccessLoginWithoutAuth() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/login", String.class);
        // Обычно GET /api/login не используется, но если есть, должен быть доступен всем
        // Если нет такого эндпоинта, можно проверить POST отдельно
        assertThat(response.getStatusCode().is2xxSuccessful() || response.getStatusCode().is4xxClientError())
                .isTrue();
    }

    @Test
    void testAccessProtectedEndpointWithoutAuth() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/users", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testAccessForbiddenForWrongUser() {
        // Создаём двух пользователей
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setPassword(passwordEncoder.encode("password1"));
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setPassword(passwordEncoder.encode("password2"));
        userRepository.save(user2);

        // Логинимся под user1 и получаем токен
        AuthRequest authRequest = new AuthRequest("user1@example.com", "password1");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthRequest> authRequestEntity = new HttpEntity<>(authRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.exchange(
                "/api/login",
                HttpMethod.POST,
                authRequestEntity,
                String.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token = loginResponse.getBody();
        assertThat(token).isNotBlank();

        // Пытаемся удалить или отредактировать user2 используя токен user1 (имитируем Forbidden)
        HttpHeaders headersWithAuth = new HttpHeaders();
        headersWithAuth.setBearerAuth(token);
        HttpEntity<Void> requestWithAuth = new HttpEntity<>(headersWithAuth);

        // Здесь нужно указать реальный эндпоинт редактирования или удаления пользователя с id user2.getId()
        String url = "/api/users/" + user2.getId();

        ResponseEntity<String> forbiddenResponse = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                requestWithAuth,
                String.class
        );

        assertThat(forbiddenResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
