package kr.co.triphos.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.common.dto.MenuInfoDTO;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.common.service.AuthenticationFacadeService;
import kr.co.triphos.common.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
@Log4j2
public class MenuController {
	private final MenuService menusService;
	private final AuthenticationFacadeService authenticationFacadeService;

	@PostMapping(value = "/updateMenu")
	@Tag(name="메뉴")
	@Operation(summary = "메뉴정보 수정", description = "mainCd, sub1Cd, sub2Cd, orderBy, menuTitle, displayYn 필요")
	public ResponseEntity<?> updateMenu(@Parameter(description = "파일") @RequestBody List<MenuInfoDTO> menuInfoDTOList) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			String memberId = authenticationFacadeService.getMemberId();
			boolean res = menusService.updateMenu(menuInfoDTOList, memberId);
			String msg = res ? "파일을 저장하였습니다." : "파일저장에 실패하였습니다.";
			responseDTO.setSuccess(res);
			responseDTO.setMsg(msg);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (RuntimeException ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg("메뉴정보 수정에 실패하였습니다.");
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

}
