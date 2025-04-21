package kr.co.triphos.organization.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.common.service.AuthenticationFacadeService;
import kr.co.triphos.member.dto.MemberDTO;
import kr.co.triphos.member.dto.MenuMemberAuthDTO;
import kr.co.triphos.member.service.AuthService;
import kr.co.triphos.member.service.MemberService;
import kr.co.triphos.organization.dto.OrganizationDTO;
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
@RequestMapping("/v1/organization")
@RequiredArgsConstructor
@Log4j2
public class OrganizationController {
	private final AuthenticationFacadeService authFacadeService;
	private final OrganizationService organizationService;

	@GetMapping("/organization")
	@Tag(name = "조직도")
	@Operation(summary = "조직도 정보 조회", description = "")
	public ResponseEntity<?> getOrganization () {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			List<OrganizationDTO> organization = organizationService.getOrganization();

			responseDTO.setSuccess(true);
			responseDTO.setMsg("조직도 조회");
			responseDTO.addData("organization", organization);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (RuntimeException ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
