package kr.co.triphos.organization.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.common.dto.ExcelDTO;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.common.service.AuthenticationFacadeService;
import kr.co.triphos.member.dto.MemberDTO;
import kr.co.triphos.member.dto.MenuMemberAuthDTO;
import kr.co.triphos.member.service.AuthService;
import kr.co.triphos.member.service.MemberService;
import kr.co.triphos.organization.dto.OrganizationDTO;
import kr.co.triphos.organization.dto.OrganizationInfoDTO;
import kr.co.triphos.organization.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/v1/organizations")
@RequiredArgsConstructor
@Log4j2
public class OrganizationController {
	private final AuthenticationFacadeService authFacadeService;
	private final OrganizationService organizationService;

	@GetMapping("")
	@Tag(name = "조직도")
	@Operation(summary = "조직도 정보 조회", description = "")
	public ResponseEntity<?> getOrganization () {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			List<OrganizationInfoDTO> organization = organizationService.getOrganization();

			responseDTO.setSuccess(true);
			responseDTO.setMsg("조직도 조회");
			responseDTO.addData("organization", organization);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (RuntimeException ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		} catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg("서버에 문제가 발생하였습니다.");
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@PostMapping("")
	@Tag(name = "조직도")
	@Operation(
			summary = "조직 정보 생성",
			description = "depth가 필요없는경우 null로 표시. <br> " +
					"입력한 depth의 하위 조직으로 생성.<br>" +
					"예) 1,1,1 입력한경우 1,1,1,n으로 생성됨.<br>" +
					"트리포스-기술개발본부-개발실 하위에 생성하고싶을 경우 1,2,1로 입력 <br>" +
					"1-2-1-n으로 조직 자동 생성",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							schema = @Schema(hidden = true),
							examples = @ExampleObject(name = "조직 생성 예시", ref = "#/components/examples/organization.post")
					)
			)
	)
	public ResponseEntity<?> createOrganization (@Parameter(description = "조직 정보") @RequestBody OrganizationDTO organizationDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			String memberId = authFacadeService.getMemberId();
			organizationDTO.setInsId(memberId);
			int level = 0;
			StringBuilder organizationKey = new StringBuilder();

			if (organizationDTO.getDepth1() != null) {
				level ++;
				organizationKey.append(organizationDTO.getDepth2());
			}

			if (organizationDTO.getDepth2() != null) {
				level ++;
				organizationKey.append("-").append(organizationDTO.getDepth2());
			}

			if (organizationDTO.getDepth3() != null) {
				level ++;
				organizationKey.append("-").append(organizationDTO.getDepth3());
			}

			if (organizationDTO.getDepth4() != null) {
				level ++;
				organizationKey.append("-").append(organizationDTO.getDepth4());
			}

			if (organizationDTO.getDepth5() != null) {
				level ++;
				organizationKey.append("-").append(organizationDTO.getDepth5());
			}

			organizationDTO.setLevel(level);
			organizationDTO.setOrganizationKey(organizationKey.toString());

			organizationService.createOrganization(organizationDTO);

			responseDTO.setSuccess(true);
			responseDTO.setMsg("조직 생성 성공");
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (RuntimeException ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		} catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg("서버에 문제가 발생하였습니다.");
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@GetMapping("/members")
	@Tag(name = "사용자")
	@Operation(summary = "조직도의 사용자 정보 조회", description = "")
	public ResponseEntity<?> getOrganizationMember (@Parameter(description = "조직도 IDX") @RequestParam int organizationIdx,
											  	    @Parameter(description = "하위 조직 멤버 포함여부") @RequestParam(required = false) String includeAllYn) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			if (!"Y".equals(includeAllYn)) includeAllYn = "N";

			List<HashMap<String, Object>> memberList = organizationService.getOrganizationMember(organizationIdx, includeAllYn);

			responseDTO.setSuccess(true);
			responseDTO.setMsg("조직의 사용자 조회");
			responseDTO.addData("memberList", memberList);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (RuntimeException ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		} catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg("서버에 문제가 발생하였습니다.");
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}
}
