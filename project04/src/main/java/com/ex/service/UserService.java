package com.ex.service;

import com.ex.data.UserDTO;
import com.ex.entity.UserEntity;
import com.ex.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService{

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;


	public String findUsername(String realName, String email, String phone) {
		Optional<UserEntity> op = userRepository.findByRealNameAndEmailAndPhone(realName, email, phone);
		String result = "존재하지 않는 아이디입니다.";
		if (op.isPresent()) {
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
				.grade(1)
				.build();
		userRepository.save(user);
	}

	public void verifyUser(String username, String realName, String email) {
		Optional<UserEntity> op = userRepository.findByUsernameAndRealNameAndEmail(username, realName, email);
		if (!op.isPresent()) {
			throw new RuntimeException("일치하는 사용자 정보가 없습니다.");
		}
	}

	public void updatePassword(String username, String newPassword) {
		Optional<UserEntity> op = userRepository.findByUsername(username);
		if (op.isPresent()) {
			UserEntity user = op.get();
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);
		} else {
			throw new RuntimeException("존재하지 않는 사용자입니다.");
		}
	}

	public UserEntity getUsername(String username) {
		Optional<UserEntity> op = userRepository.findByUsername(username);
		if(op.isPresent()) {
			return op.get();
		}else {
			throw new RuntimeException("SiteUser not found");
		}
	}

	//♣가은♣ userID로 user 요소 모두 꺼내기
	public UserEntity findById(Integer id) {
		Optional<UserEntity> op = this.userRepository.findById(id);
		if(op.isPresent()) {
			return op.get();
		}else {
			throw new RuntimeException("user not found");
		}
	}

	//♣가은♣ username으로 user 요소 모두 꺼내기
	public UserEntity findByUserName(String username) {
		Optional<UserEntity> op = this.userRepository.findByUsername(username);
		if(op.isPresent()) {
			return op.get();
		}else {
			throw new RuntimeException("user not found");
		}
	}

	    public UserDTO findUserByEmail(String email) {
	        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
	        if (userEntity.isPresent()) {
	            return UserDTO.entityToDTO(userEntity.get());
	        } else {
	            return null;
	        }
	    }

}
