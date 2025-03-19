package kr.co.triphos.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.common.dto.ExcelDTO;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.common.service.AuthenticationFacadeService;
import kr.co.triphos.common.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Log4j2
public class FileController {
	private final FileService fileService;
	private final AuthenticationFacadeService authenticationFacadeService;

	@PostMapping(value = "/fileSave", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Tag(name="파일")
	@Operation(summary = "파일 신규저장", description = "")
	public ResponseEntity<?> fileSave(@Parameter(description = "파일") @RequestParam("fileList") List<MultipartFile> fileList) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			if (fileList.isEmpty()) throw new RuntimeException("파일을 업로드 후 진행하여주십시오.");

			String memberId = authenticationFacadeService.getMemberId();
			boolean res = fileService.fileSave(fileList, memberId);
			String msg = res ? "파일을 저장하였습니다." : "파일저장에 실패하였습니다.";
			responseDTO.setSuccess(res);
			responseDTO.setMsg(msg);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}
}
