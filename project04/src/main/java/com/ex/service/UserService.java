package com.ex.service;

import com.ex.dto.UserDTO;
import com.ex.entity.UserEntity;
import com.ex.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    
    public String findUsername(String realName, String email, String phone) {
        Optional<UserEntity> op = userRepository.findByRealNameAndEmailAndPhone(realName, email, phone);
        String result = "존재하지 않는 아이디 입니다";
        if(!op.isPresent()){
        	result = op.get().getUsername();
        }
        return result;
    }

    public void registerUser(UserDTO userDTO) {
        UserEntity user = UserEntity.builder()
        		.username(userDTO.getUsername())
				.password(passwordEncoder.encode(userDTO.getPassword()))
				.realName(userDTO.getRealName())
				.email(userDTO.getEmail())
				.adress(userDTO.getAdress())
				.phone(userDTO.getPhone())
				.reg(LocalDateTime.now())
				.point(1000)
				.build();
        userRepository.save(user);
    }
    
}
