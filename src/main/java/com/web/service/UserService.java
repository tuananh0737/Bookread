package com.web.service;

import com.web.config.JwtTokenProvider;
import com.web.config.MessageException;
import com.web.config.SecurityUtils;
import com.web.dto.ChangePasswordRequest;
import com.web.entity.User;
import com.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder; // [Sửa 1] Import PasswordEncoder
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

    @Autowired
    private PasswordEncoder passwordEncoder; // [Sửa 2] Inject PasswordEncoder

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

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
        // [Sửa 3] Sử dụng passwordEncoder.matches để so sánh mật khẩu chưa mã hóa và mật khẩu trong DB
        if(passwordEncoder.matches(password, users.get().getPassword())){
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
        // [Sửa 4] Mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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

    public User updateUserInfo(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new MessageException("User not found", 404));
        existingUser.setFullname(user.getFullname());
        existingUser.setPhone(user.getPhone());
        existingUser.setIdCard(user.getIdCard());
        return userRepository.save(existingUser);
    }


    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        Long userId = Long.valueOf(SecurityUtils.getCurrentUserLogin().get());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MessageException("User not found", 404));

        // [Sửa 5] Kiểm tra mật khẩu cũ bằng matches()
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new MessageException("Old password is incorrect", 400);
        }

        if (changePasswordRequest.getNewPassword() == null || changePasswordRequest.getNewPassword().isEmpty()) {
            throw new MessageException("New password cannot be empty", 400);
        }

        // [Sửa 6] Mã hóa mật khẩu mới trước khi lưu
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }

}