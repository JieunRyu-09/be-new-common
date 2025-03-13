package kr.co.triphos.common.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.common.dto.ExcelDTO;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.common.service.AuthenticationFacadeService;
import kr.co.triphos.common.service.ExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
@Log4j2
public class ExcelController {
	private final ExcelService excelService;
	private final AuthenticationFacadeService authenticationFacadeService;

	@PostMapping("/save")
	@Tag(name="엑셀 파일")
	@Operation(summary = "엑셀 저장", description = "신규저장, 수정 등 전부 사용가능. 저장/수정하고싶은 데이터만 송신")
	public ResponseEntity<?> excelSave(@Parameter(description = "엑셀 정보") @RequestBody ExcelDTO ExcelDTO) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			String memberId = authenticationFacadeService.getMemberId();
			//excelService.excelSave(excelDTO, memberId);
			responseDTO.setSuccess(true);

			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}
}
