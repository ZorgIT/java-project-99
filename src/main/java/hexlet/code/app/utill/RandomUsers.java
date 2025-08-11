package hexlet.code.app.utill;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RandomUsers {
    public static List<User> generateFakeUsers(int count) {
        Faker faker = new Faker();
        List<User> users = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(faker.internet().password(8, 16));
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user.setModifiedBy(faker.name().username() + "test");
            user.setCreatedBy(faker.name().username() + "test");
            users.add(user);

        }


        return users;
    }
}
