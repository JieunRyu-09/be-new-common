package kr.co.triphos.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Builder
public class MemberDTO {

	@NotBlank(message = "아이디를 입력하세요.")
	private String 	memberId;

	private String 	memberPw;
	private String 	newMemberPw;
	private String 	authority;
	private String 	memberNm;
	private String 	memberEmail;
	private String 	memberPhone;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;
}
