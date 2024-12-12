package com.web.api;

import com.web.dto.LoginDto;
import com.web.dto.UserSearch;
import com.web.entity.User;
import com.web.config.JwtTokenProvider;
import com.web.repository.UserRepository;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class UserApi {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;



    @PostMapping("/login")
    public String login(@RequestBody LoginDto loginDto) throws Exception {
        String token = userService.login(loginDto.getUsername(), loginDto.getPassword());
        return token;
    }

    @PostMapping("/regis")
    public ResponseEntity<?> regisUser(@RequestBody User user) throws URISyntaxException {
        User result = userService.regisUser(user);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @PostMapping("/userlogged")
    public User getUserLogged() {
        return userService.getUserWithAuthority();
    }


    @GetMapping("/admin/getUserByRole")
    public List<User> getUserNotAdmin(@RequestParam(value = "role", required = false) String role) {
        if (role == null) {
            return userRepository.findAll();
        }
        return userRepository.getUserByRole(role);
    }

    @PostMapping("/user/update-info")
    public ResponseEntity<?> userUpdateInfo(@RequestBody User user) {
        try {
            User updatedUser = userService.updateUserInfo(user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update user info", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin/update-info")
    public ResponseEntity<?> adminUpdateInfo(@RequestBody User user) {
        try {
            User updatedUser = userService.updateUserInfo(user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update user info", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("admin/search-user")
    public List<User> search(@RequestBody UserSearch userSearch) {
        String param = userSearch.getParam();
        if (param == null) {
            param = "";
        }
        param = "%" + param + "%";
        List<User> list = null;
        if (param != null) {
            list = userRepository.findByParam(param);
        }
        return list;
    }


}
