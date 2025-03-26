package kr.co.triphos.common.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.common.dto.ExcelDTO;
import kr.co.triphos.common.dto.ExcelDataDTO;
import kr.co.triphos.common.dto.ExcelInfoDTO;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.common.service.AuthenticationFacadeService;
import kr.co.triphos.common.service.ExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/excel")
@RequiredArgsConstructor
@Log4j2
public class NewExcelController {
	private final ExcelService excelService;
	private final AuthenticationFacadeService authenticationFacadeService;

	@PostMapping("/info")
	@Tag(name="엑셀 파일", description = "엑셀파일 관련 API")
	@Operation(
			summary = "엑셀 신규저장",
			description = "",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							schema = @Schema(hidden = true),
							examples = @ExampleObject(name = "엑셀 저장 예시", ref = "#/components/examples/excel.post.info")
					)
			)
	)
	public ResponseEntity<?> excelSave(@Parameter(description = "엑셀 정보") @RequestBody ExcelDTO excelDTO) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			String memberId = authenticationFacadeService.getMemberId();
			boolean res = excelService.excelSave(excelDTO, memberId);
			String msg = res ? "엑셀정보를 저장하였습니다." : "엑셀정보 저장에 실패하였습니다.";
			responseDTO.setSuccess(res);
			responseDTO.setMsg(msg);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@PutMapping("/info")
	@Tag(name="엑셀 파일")
	@Operation(
			summary = "저장된 엑셀 수정",
			description = "excelNm, excelDataList는 update, deleteDataList는 delete작업을 실행. List들은 엑셀의 row에 대한 작업",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							schema = @Schema(hidden = true),
							examples = @ExampleObject(name = "엑셀 수정 예시", ref = "#/components/examples/excel.put.info")
					)
			)
	)
	public ResponseEntity<?> excelUpdate(@Parameter(description = "엑셀 정보") @RequestBody ExcelDTO excelDTO) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			String memberId = authenticationFacadeService.getMemberId();
			boolean res = excelService.excelUpdate(excelDTO, memberId);
			String msg = res ? "엑셀정보를 저장하였습니다." : "엑셀정보 저장에 실패하였습니다.";
			responseDTO.setSuccess(res);
			responseDTO.setMsg(msg);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@DeleteMapping("/info")
	@Tag(name="엑셀 파일")
	@Operation(summary = "엑셀 삭제",description = "List<integer> deleteExcelList")
	public ResponseEntity<?> excelDelete(@Parameter(description = "엑셀Idx List") @RequestParam List<Integer> deleteExcelList) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			excelService.excelDelete(deleteExcelList);
			String msg = "엑셀정보를 삭제하였습니다";
			responseDTO.setSuccess(true);
			responseDTO.setMsg(msg);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (RuntimeException ex) {
			responseDTO.setMsg(ex.getMessage());
			log.error(ex);
			return ResponseEntity.internalServerError().body(responseDTO);
		}
		catch (Exception ex) {
			responseDTO.setMsg("엑셀 삭제에 실패하였습니다.");
			log.error(ex);
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@GetMapping("/info")
	@Tag(name="엑셀 파일")
	@Operation(summary = "엑셀 파일 목록 조회", description = "")
	public ResponseEntity<?> getExcelInfoList(@Parameter(description = "엑셀 이름.") @RequestParam(required = false) String excelNm,
											  @Parameter(description = "등록일자 From (type: yyyymmdd)") @RequestParam String fromDate,
											  @Parameter(description = "등록일자 To (type: yyyymmdd)") @RequestParam String toDate) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			List<Map<String, Object>> excelInfoList = excelService.getExcelInfoList(excelNm, fromDate, toDate);
			responseDTO.setSuccess(true);
			responseDTO.addData("excelInfoList", excelInfoList);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@GetMapping("/info/{idx}/data")
	@Tag(name="엑셀 파일")
	@Operation(summary = "엑셀 파일 데이터 조회", description = "")
	public ResponseEntity<?> getExcelDataList(@Parameter(description = "엑셀 Idx") @PathVariable int idx) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			List<ExcelDataDTO> excelDataList = excelService.getExcelDataList(idx);
			responseDTO.setSuccess(true);
			responseDTO.addData("excelDataList", excelDataList);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}
}
