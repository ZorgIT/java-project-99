package hexlet.code.app.controller;

import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.utill.RandomUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/welcome")
public class WelcomeController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String welcome() {
        var randomUsers = RandomUsers.generateFakeUsers(5);
        userRepository.saveAll(randomUsers);
        var users = userRepository.findAll();
        return "Welcome to Spring" + users.toString();
    }

}
