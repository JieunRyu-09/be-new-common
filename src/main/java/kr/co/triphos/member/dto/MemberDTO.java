package kr.co.triphos.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import kr.co.triphos.member.entity.Member;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
	@NotBlank(message = "아이디를 입력하세요.")
	@Schema(description = "사용자 ID")
	private String 	memberId;

	@NotBlank(message = "이름을 입력하세요.")
	@Schema(description = "사용자 이름")
	private String 	memberNm;

	@Schema(description = "사용자 조직")
	private Integer organizationIdx;

	@Schema(description = "사용자 직급")
	private Integer positionIdx;

	@NotBlank(message = "비밀번호를 입력하세요.")
	@Schema(description = "사용자 PW")
	private String 	memberPw;

	@NotBlank(message = "사용자 신규 비밀번호를 입력하세요.")
	@Schema(description = "사용자 신규 비밀번호")
	private String 	newMemberPw;

	@NotBlank(message = "이메일을 입력하세요.")
	@Schema(description = "사용자 이메일")
	private String 	email;

	@NotBlank(message = "핸드폰 번호를 입력하세요.")
	@Schema(description = "사용자 핸드폰")
	private String 	phone;

	@NotBlank(message = "사용자 등급을 선택하세요.")
	@Schema(description = "사용자 등급 (MMG001: 관리자, MMG002: 사용자)")
	private String 	memberType;

	@NotBlank(message = "사용자 사용여부를 선택하세요.")
	@Schema(description = "사용자 사용여부")
	private String	delYn;

	@NotBlank(message = "관리자 설정을 선택하세요.")
	@Schema(description = "사용자 관리자 권한여부")
	private String	adminYn;

	public MemberDTO(Member member) {
		this.memberId 	= member.getMemberId();
		this.memberNm 	= member.getMemberNm();
		this.memberPw 	= member.getMemberPw();
		this.email 		= member.getEmail();
		this.phone 		= member.getPhone();
		this.memberType = member.getMemberType();
		this.delYn 		= member.getDelYn();
		this.adminYn 	= member.getAdminYn();
	}

	@Builder(builderClassName = "createMember", builderMethodName = "createMember")
	public MemberDTO(@NonNull String memberId,
					 @NonNull String memberNm,
					 @NonNull String memberPw,
					 @NonNull String email,
					 @NonNull String phone,
					 @NonNull String memberType) {
		this.memberId 	= memberId;
		this.memberNm 	= memberNm;
		this.memberPw 	= memberPw;
		this.email 		= email;
		this.phone 		= phone;
		this.memberType = memberType;
	}
}
