package com.ex.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

	private String username;
	private String password;
	private String email;
	private String realName;
	private String adress;
	private String phone;
	private LocalDateTime reg;
	private long point;

}
