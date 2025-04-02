package kr.co.triphos.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.common.service.AuthenticationFacadeService;
import kr.co.triphos.common.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/v1/files")
@RequiredArgsConstructor
@Log4j2
public class FileController {
	private final FileService fileService;
	private final AuthenticationFacadeService authenticationFacadeService;

	@PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

	@GetMapping(value = "")
	@Tag(name="파일")
	@Operation(summary = "파일 목록조회", description = "")
	public ResponseEntity<?> getFileList(@Parameter(description = "파일명") @RequestParam(required = false) String fileNm,
										 @Parameter(description = "등록일자 From (type: yyyymmdd)") @RequestParam String fromDate,
										 @Parameter(description = "등록일자 To (type: yyyymmdd)") @RequestParam String toDate) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			List<HashMap<String, Object>> fileList = fileService.getFileList(fileNm, fromDate, toDate);
			responseDTO.addData("fileList", fileList);
			responseDTO.setSuccess(true);
			responseDTO.setMsg("");
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex.getMessage());
			responseDTO.setMsg("파일목록 조회에 실패하였습니다.");
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@GetMapping(value = "/{fileIdx}/download")
	@Tag(name="파일")
	@Operation(summary = "파일 다운로드", description = "")
	public ResponseEntity<?> downloadFile(@Parameter(description = "파일 idx") @PathVariable Integer fileIdx) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			HashMap<String, Object> fileData = fileService.downloadFile(fileIdx);

			String encodedFileName = fileData.get("encodedFileName").toString();
			InputStreamResource resource = (InputStreamResource) fileData.get("resource");
			String fileSize = fileData.get("fileSize").toString();

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName);
			headers.add(HttpHeaders.CONTENT_LENGTH, fileSize);  // 파일 크기 지정

			// 다운로드 시 원본 파일명 유지
			return ResponseEntity.ok()
					.headers(headers)
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(resource);
		}
		catch (RuntimeException ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex.toString());
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@DeleteMapping(value = "")
	@Tag(name="파일")
	@Operation(summary = "파일 삭제", description = "List<Integer> deleteFileList")
	public ResponseEntity<?> deleteFile(@Parameter(description = "파일Idx List") @RequestParam List<Integer> deleteFileList) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			fileService.deleteFile(deleteFileList);
			String msg = "파일을 삭제하였습니다";
			responseDTO.setSuccess(true);
			responseDTO.setMsg(msg);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (RuntimeException ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex.getMessage());
			responseDTO.setMsg("파일 삭제에 실패하였습니다.");
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}
}
