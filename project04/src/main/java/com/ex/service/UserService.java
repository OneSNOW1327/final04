package com.ex.service;

import com.ex.data.UserDTO;
import com.ex.entity.DeliveryEntity;
import com.ex.entity.OrderlistEntity;
import com.ex.entity.UserEntity;
import com.ex.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

//(가은) userID로 user 요소 모두 꺼내기
	public UserEntity findById(Integer id) {
		Optional<UserEntity> op = this.userRepository.findById(id);
		if(op.isPresent()) {
			return op.get();
		}else {
			throw new RuntimeException("user not found");
		}
	}

//(가은) username으로 user 요소 모두 꺼내기
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
	    
//(재성)회원정보 업데이트하기
	    public void updateUser(String username, UserDTO userDTO) {
	        Optional<UserEntity> op = userRepository.findByUsername(username);
	        if (op.isPresent()) {
	            UserEntity user = op.get();
	            user.setEmail(userDTO.getEmail());
	            user.setRealName(userDTO.getRealName());
	            user.setPostcode(userDTO.getPostcode());
	            user.setAddress(userDTO.getAddress());
	            user.setDetailAddress(userDTO.getDetailAddress());
	            user.setExtraAddress(userDTO.getExtraAddress());
	            user.setPhone(userDTO.getPhone());
	            userRepository.save(user);
	        } else {
	            throw new RuntimeException("존재하지 않는 사용자입니다.");
	        }
	    }

	    public void usePoint(long point, String username) {
	    	UserEntity ue = userRepository.findByUsername(username).get();
	    	ue.setPoint(ue.getPoint()-point);
	    	userRepository.save(ue);
	    	
	    }
	    
// 사용자 이름으로 등급 반환하기
		public int findUserGradeByUsername(String username) {
			UserEntity user = userRepository.findByUsername(username)
					.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
			return user.getGrade(); // 사용자의 등급을 반환
		}
//(가은)최근 		
		public List<DeliveryEntity> getSortedDeliveries(UserEntity user) {
		    List<DeliveryEntity> deliveries = user.getDelivery();
		
		    // DeliveryEntity를 가장 최근 OrderlistEntity의 orderTime 기준으로 정렬
		    List<DeliveryEntity> sortedDeliveries = deliveries.stream()
		        .sorted((delivery1, delivery2) -> {
		            // delivery1의 최신 orderTime 가져오기
		            Optional<LocalDateTime> latestOrderTime1 = delivery1.getOrder().stream()
		                .map(OrderlistEntity::getOrderTime)
		                .max(Comparator.naturalOrder());
		
		            // delivery2의 최신 orderTime 가져오기
		            Optional<LocalDateTime> latestOrderTime2 = delivery2.getOrder().stream()
		                .map(OrderlistEntity::getOrderTime)
		                .max(Comparator.naturalOrder());
		
		            // 최신 orderTime 기준으로 역순 정렬
		            return latestOrderTime2.orElse(LocalDateTime.MIN).compareTo(latestOrderTime1.orElse(LocalDateTime.MIN));
		        })
		        .collect(Collectors.toList());
		
		    return sortedDeliveries; // 정렬된 DeliveryEntity 반환
		}
		// 사용자 삭제하기
	    public void deleteUser(String username) {
	        Optional<UserEntity> op = userRepository.findByUsername(username);
	        if (op.isPresent()) {
	            userRepository.delete(op.get());
	        } else {
	            throw new RuntimeException("존재하지 않는 사용자입니다.");
	        }
	    }
	    
}
