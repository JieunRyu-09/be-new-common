package kr.co.triphos.member.dto.memberDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@SuperBuilder
public class MemberDTO {
	@NotBlank(message = "아이디를 입력하세요.")
	@Schema(description = "사용자 ID")
	private String 	memberId;
	@NotBlank(message = "이름을 입력하세요.")
	@Schema(description = "사용자 이름")
	private String 	memberNm;
	@NotBlank(message = "비밀번호를 입력하세요.")
	@Schema(description = "사용자 PW")
	private String 	memberPw;
	@NotBlank(message = "이메일을 입력하세요.")
	@Schema(description = "사용자 이메일")
	private String 	email;
	@NotBlank(message = "핸드폰 번호를 입력하세요.")
	@Schema(description = "사용자 핸드폰")
	private String 	phone;
	@NotBlank(message = "사용자 등급을 선택하세요.")
	@Schema(description = "사용자 등급")
	private String 	memberType;
}
