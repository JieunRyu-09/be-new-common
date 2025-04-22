package kr.co.triphos.position.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.common.service.AuthenticationFacadeService;
import kr.co.triphos.organization.dto.OrganizationDTO;
import kr.co.triphos.organization.dto.OrganizationInfoDTO;
import kr.co.triphos.organization.service.OrganizationService;
import kr.co.triphos.position.dto.PositionDTO;
import kr.co.triphos.position.dto.PositionInfoDTO;
import kr.co.triphos.position.service.PositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/positions")
@RequiredArgsConstructor
@Log4j2
public class PositionController {
	private final AuthenticationFacadeService authFacadeService;
	private final PositionService positionService;

	@GetMapping("")
	@Tag(name = "직급")
	@Operation(summary = "직급 정보 조회", description = "findAllYn이 Y면 미사용 직급도 포함하여 반환.<br> N이면 사용중인 직급만")
	public ResponseEntity<?> getPosition (@Parameter(description = "미사용 직급 포함여부") @RequestParam String findAllYn) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			List<PositionInfoDTO> positionList = positionService.getPosition(findAllYn);

			responseDTO.setSuccess(true);
			responseDTO.setMsg("직급 조회");
			responseDTO.addData("positionList", positionList);
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
	@Tag(name = "직급")
	@Operation(
			summary = "직급 정보 생성",
			description = "",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							schema = @Schema(hidden = true),
							examples = @ExampleObject(name = "직급 생성 예시", ref = "#/components/examples/position.post")
					)
			)
	)
	public ResponseEntity<?> createPosition (@Parameter(description = "직급 정보") @RequestBody PositionDTO positionDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			String memberId = authFacadeService.getMemberId();
			positionDTO.setInsId(memberId);
			positionService.createPosition(positionDTO);

			responseDTO.setSuccess(true);
			responseDTO.setMsg("직급 생성 성공");
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
	@Tag(name = "직급")
	@Operation(
			summary = "직급 정보 수정",
			description = "미사용 처리 시 해당 직급의 사용자 여부 판단",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							schema = @Schema(hidden = true),
							examples = @ExampleObject(name = "직급 수정 예시", ref = "#/components/examples/position.put")
					)
			)
	)
	public ResponseEntity<?> updatePosition (@Parameter(description = "직급 정보") @RequestBody PositionDTO positionDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			String memberId = authFacadeService.getMemberId();
			positionDTO.setUpdId(memberId);

			positionService.updatePosition(positionDTO);

			responseDTO.setSuccess(true);
			responseDTO.setMsg("직급정보 변경 성공");
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

	@Hidden
	@DeleteMapping("")
	@Tag(name = "직급")
	@Operation(
			summary = "직급 미사용처리",
			description = "미사용 처리할 직급의 idx 배열로 입력" +
					"사용자가 있는 경우 미사용처리 안함, responseDTO에 <br>" +
					"미사용 처리 못한 직급의 idx 및 직급명 반환 ",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							schema = @Schema(hidden = true),
							examples = @ExampleObject(name = "직급 삭제 예시", ref = "#/components/examples/position.delete")
					)
			)
	)
	public ResponseEntity<?> deletePosition (@Parameter(description = "직급 정보") @RequestBody PositionDTO positionDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			if (positionDTO.getDeleteIdxList().isEmpty()) {
				throw new RuntimeException("미사용 처리할 직급정보가 없습니다.");
			}

			String memberId = authFacadeService.getMemberId();
			positionDTO.setUpdId(memberId);

			Map<String, Object> returnData = positionService.deletePosition(positionDTO);
			List<Integer> undeletedIdxList = (List<Integer>) returnData.get("undeletedIdxList");
			List<String> undeletedNameList = (List<String>) returnData.get("undeletedNameList");

			String msg = undeletedIdxList.isEmpty() ? "직급 미사용처리 완료." : "사용자가 존재하는 직급을 제외한 나머지 직급 미사용처리 완료.";
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
	@Operation(summary = "직급의 사용자 정보 조회", description = "findAllYn이 Y면 미사용 사용자도 포함하여 반환.<br> N이면 사용중인 사용자만")
	public ResponseEntity<?> getPositionMember (@Parameter(description = "직급 IDX") @RequestParam int positionIdx,
												@Parameter(description = "미사용 사용자 포함여부") @RequestParam String findAllYn) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			List<Map<String, Object>> memberList = positionService.getPositionMember(positionIdx, findAllYn);

			responseDTO.setSuccess(true);
			responseDTO.setMsg("직급의 사용자 조회");
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
