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
	@NotBlank(message = "이름을 입력하세요.")
	private String 	memberNm;
	@NotBlank(message = "비밀번호를 입력하세요.")
	private String 	memberPw;
	private String 	newMemberPw;
	@NotBlank(message = "이메일을 입력하세요.")
	private String 	email;
	@NotBlank(message = "핸드폰 번호를 입력하세요.")
	private String 	phone;
	@NotBlank(message = "사용자 등급을 선택하세요.")
	private String 	memberType;
	@NotBlank(message = "사용여부를 선택하세요.")
	private String 	memberStatus;

	private String 	deviceType;
	private String 	deviceToken;
	private String 	delYn = "N";
	private String 	loginCnt;
	private String 	loginDt;
	private String 	lastPwdChangeDt;
	private String 	authKey;
	private String 	accessIp;
	private String 	accessBrowser;
	private String 	adminYn = "N";
	private String 	insDt;
	private String 	insMember;
	private String 	updDt;
	private String 	updMember;
	private boolean	accountNonExpired;
	private boolean	accountNonLocked;
	private boolean	credentialsNonExpired;
	private boolean	enabled;

}
