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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			description = "1-1-1와같은 값으로 입력.<br>" +
					"결과는 1-1-1-n으로 생성됨",
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
			organizationService.createOrganization(organizationDTO);

			responseDTO.setSuccess(true);
			responseDTO.setMsg("조직 생성 성공");
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (RuntimeException ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.status(400).body(responseDTO);
		} catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg("서버에 문제가 발생하였습니다.");
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@PutMapping("")
	@Tag(name = "조직도")
	@Operation(
			summary = "조직 정보 수정",
			description = "변경할 조직 명, 조직 위치정보 입력.<br>" +
					"입력한 depth의 하위 조직으로 이동.<br>" +
					"예) 1-1-1을 입력한경우 1-1-1-n으로 이동됨.<br>" +
					"조직명만 변경할 경우 idx와 조직명만 입력",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							schema = @Schema(hidden = true),
							examples = @ExampleObject(name = "조직 생성 예시", ref = "#/components/examples/organization.put")
					)
			)
	)
	public ResponseEntity<?> updateOrganization (@Parameter(description = "조직 정보") @RequestBody OrganizationDTO organizationDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			String memberId = authFacadeService.getMemberId();
			organizationDTO.setUpdId(memberId);

			organizationService.updateOrganization(organizationDTO);

			responseDTO.setSuccess(true);
			responseDTO.setMsg("조직정보 변경 성공");
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (RuntimeException ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.status(400).body(responseDTO);
		} catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg("서버에 문제가 발생하였습니다.");
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@DeleteMapping("")
	@Tag(name = "조직도")
	@Operation(
			summary = "조직 미사용처리",
			description = "미사용 처리할 조직의 idx 배열로 입력<br> +" +
					"하위조직을 포함하여 사용자가 있는지 확인.<br>+" +
					"사용자가 있는 경우 미사용처리 안함, 반환하는 ",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							schema = @Schema(hidden = true),
							examples = @ExampleObject(name = "조직 생성 예시", ref = "#/components/examples/organization.delete")
					)
			)
	)
	public ResponseEntity<?> deleteOrganization (@Parameter(description = "조직 정보") @RequestBody OrganizationDTO organizationDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			if (organizationDTO.getDeleteIdxList().isEmpty()) {
				throw new RuntimeException("미사용 처리할 조직정보가 없습니다.");
			}

			String memberId = authFacadeService.getMemberId();
			organizationDTO.setUpdId(memberId);

			Map<String, Object> returnData = organizationService.deleteOrganization(organizationDTO);
			List<Integer> undeletedIdxList = (List<Integer>) returnData.get("undeletedIdxList");
			List<String> undeletedNameList = (List<String>) returnData.get("undeletedNameList");

			String msg = undeletedIdxList.isEmpty() ? "조직 미사용처리 완료." : "하위 조직을 포함하여 구성원이 있는 조직을 제외하고 미사용 처리 완료.";
			responseDTO.addData("undeletedIdxList", undeletedIdxList);
			responseDTO.addData("undeletedNameList", undeletedNameList);

			responseDTO.setSuccess(true);
			responseDTO.setMsg(msg);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (RuntimeException ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.status(400).body(responseDTO);
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
