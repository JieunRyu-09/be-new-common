package kr.co.triphos.member.dto.memberDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@SuperBuilder
public class MemberUpdateDTO extends MemberDTO {
	@NotBlank(message = "사용자 신규 비밀번호를 입력하세요.")
	@Schema(description = "사용자 신규 비밀번호")
	private String 	newMemberPw;
	@NotBlank(message = "사용자 사용여부를 선택하세요.")
	@Schema(description = "사용자 사용여부")
	private String 	delYn;
}
