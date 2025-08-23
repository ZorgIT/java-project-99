package hexlet.code.utils;

import hexlet.code.model.User;
import net.datafaker.Faker;

import java.time.LocalDate;
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
            user.setCreatedAt(LocalDate.now());
            user.setUpdatedAt(LocalDate.now());
            users.add(user);

        }


        return users;
    }
}
