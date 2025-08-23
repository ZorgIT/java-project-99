package hexlet.code.controller;

import hexlet.code.repository.UserRepository;
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
        var users = userRepository.findAll();
        return "Welcome to Spring" + users.toString();
    }

}
