package com.ex.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ex.entity.UserEntity;
import com.ex.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserSecurityService implements UserDetailsService {
	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		
		Optional<UserEntity> op = this.userRepository.findByUsername(username); // Repository 에서  findByUsername 생성 Optional 로 UserEntity넣고
		if(!op.isPresent()) {
			throw new RuntimeException("사용자를 찾을수 없습니다.");
		}
		UserEntity ue = op.get();  // DB에 저장되어있는 ID와 PW를 꺼내주는역할
		List<GrantedAuthority> grantList = new ArrayList<>();
		if(ue.getGrade1() == 1) {
			grantList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}else {
			grantList.add(new SimpleGrantedAuthority("ROLE_USER"));
		}
		return new User(ue.getUsername() , ue.getPassword() , grantList);
		
	}

}





















