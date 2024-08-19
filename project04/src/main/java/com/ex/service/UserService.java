package com.ex.service;

import com.ex.data.UserDTO;
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

	// 사용자 실명, 이메일, 전화번호로 사용자 이름 찾기
	public String findUsername(String realName, String email, String phone) {
		Optional<UserEntity> op = userRepository.findByRealNameAndEmailAndPhone(realName, email, phone);
		String result = "존재하지 않는 아이디입니다.";
		if (op.isPresent()) {
			result = op.get().getUsername();
			
		}
		return result;
	}

	// 사용자 등록하기
	public void registerUser(UserDTO userDTO) {
		UserEntity user = UserEntity.builder()
				.username(userDTO.getUsername())
				.password(passwordEncoder.encode(userDTO.getPassword()))
				.realName(userDTO.getRealName())
				.email(userDTO.getEmail())
				.address(userDTO.getAddress())
				.phone(userDTO.getPhone())
				.reg(LocalDateTime.now())
				.point(1000)
				.grade(1)
				.build();
		userRepository.save(user);
	}

	// 사용자 인증 정보 확인하기
	public void verifyUser(String username, String realName, String email) {
		Optional<UserEntity> op = userRepository.findByUsernameAndRealNameAndEmail(username, realName, email);
		if (!op.isPresent()) {
			throw new RuntimeException("일치하는 사용자 정보가 없습니다.");
		}
	}

	// 비밀번호 업데이트하기
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

	// 사용자 이름으로 사용자 정보 가져오기
	public UserEntity getUsername(String username) {
		Optional<UserEntity> op = userRepository.findByUsername(username);
		if(op.isPresent()) {
			return op.get();
		}else {
			throw new RuntimeException("SiteUser not found");
		}
	}

	// 사용자 ID로 사용자 요소 모두 가져오기
	public UserEntity findById(Integer id) {
		Optional<UserEntity> op = this.userRepository.findById(id);
		if(op.isPresent()) {
			return op.get();
		}else {
			throw new RuntimeException("user not found");
		}
	}

	// 사용자 이름으로 사용자 요소 모두 가져오기
	public UserEntity findByUserName(String username) {
		Optional<UserEntity> op = this.userRepository.findByUsername(username);
		if(op.isPresent()) {
			return op.get();
		}else {
			throw new RuntimeException("user not found");
		}
	}

	// 이메일로 사용자 정보 찾기
	public UserDTO findUserByEmail(String email) {
		Optional<UserEntity> userEntity = userRepository.findByEmail(email);
		if (userEntity.isPresent()) {
			return UserDTO.entityToDTO(userEntity.get());
		} else {
			return null;
		}
	}

	// 회원정보 업데이트하기
	public void updateUser(String username, UserDTO userDTO) {
		Optional<UserEntity> op = userRepository.findByUsername(username);
		if (op.isPresent()) {
			UserEntity user = op.get();
			user.setEmail(userDTO.getEmail());
			user.setRealName(userDTO.getRealName());
			user.setAddress(userDTO.getAddress());
			user.setPhone(userDTO.getPhone());
			userRepository.save(user);
		} else {
			throw new RuntimeException("존재하지 않는 사용자입니다.");
		}
	}

	// 사용자 이름으로 등급 반환하기
	public int findUserGradeByUsername(String username) {
		UserEntity user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
		return user.getGrade(); // 사용자의 등급을 반환
	}
}
