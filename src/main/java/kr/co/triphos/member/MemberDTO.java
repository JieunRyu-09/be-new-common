package kr.co.triphos.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
public class MemberDTO {
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
