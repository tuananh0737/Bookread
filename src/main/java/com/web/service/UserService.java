package com.web.service;

import com.web.config.JwtTokenProvider;
import com.web.config.MessageException;
import com.web.config.SecurityUtils;
import com.web.entity.User;
import com.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.get() == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetails(user.get());
    }

    public User getUserWithAuthority() {
        Long id = Long.valueOf(SecurityUtils.getCurrentUserLogin().get());
        return userRepository.findById(id).get();
    }


    public String login(String username, String password) {
        Optional<User> users = userRepository.findByUsername(username);
        // check infor user
        checkUser(users);
        if(users.get().getPassword().equals(password)){
            CustomUserDetails customUserDetails = new CustomUserDetails(users.get());
            String token = jwtTokenProvider.generateToken(customUserDetails);
            return token;
        }
        else{
            throw new MessageException("Mật khẩu không chính xác", 400);
        }
    }


    public User regisUser(User user) {
        userRepository.findByUsername(user.getUsername())
                .ifPresent(exist->{
                    throw new MessageException("Tên đăng nhập đã tồn tại", 400);
                });
        user.setActived(true);
        user.setRole("ROLE_USER");
        User result = userRepository.save(user);
        return result;
    }

    public Boolean checkUser(Optional<User> users){
        if(users.isPresent() == false){
            throw new MessageException("Không tìm thấy tài khoản", 404);
        }
        else if(users.get().getActived()== false){
            throw new MessageException("Tài khoản đã bị khóa");
        }
        return true;
    }
}

