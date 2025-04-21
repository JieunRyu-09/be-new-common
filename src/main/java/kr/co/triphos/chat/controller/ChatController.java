package kr.co.triphos.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.chat.dto.ChatMessageDTO;
import kr.co.triphos.chat.dto.ChatRoomDTO;
import kr.co.triphos.chat.dto.ChatRoomInfoDTO;
import kr.co.triphos.chat.dto.ChatRoomMemberDTO;
import kr.co.triphos.chat.entity.ChatRoomMember;
import kr.co.triphos.chat.service.ChatService;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.common.dto.WebhookDTO;
import kr.co.triphos.common.service.AuthenticationFacadeService;
import kr.co.triphos.member.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
@Log4j2
public class ChatController {
	private final ChatService chatService;
	private final AuthenticationFacadeService authenticationFacadeService;

	@GetMapping("/common-chat-rooms")
	@Tag(name = "채팅방")
	@Operation(
			summary = "공통채팅방 조회",
			description = "입력한 사용자의 id를 기반으로 공통으로 참여하고있는 채팅방을 조회<br>조회 시 자기 자신 자동 포함"
	)
	public ResponseEntity<?> getCommonChatRooms(@Parameter(description = "사용자 ID") @RequestParam(required = false) List<String> memberIdList) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			String memberId = authenticationFacadeService.getMemberId();
			memberIdList.add(memberId);
			List<ChatRoomInfoDTO> chatRoomList = chatService.getCommonChatRooms(memberId, memberIdList);
			responseDTO.addData("chatRoomList", chatRoomList);
			responseDTO.setSuccess(true);
			responseDTO.setMsg("공통참여중인 채팅방 조회");
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg("서버에 문제가 발생하였습니다.");
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@PostMapping("/chat-rooms")
	@Tag(name = "채팅방")
	@Operation(
			summary = "채팅방 생성",
			description = "채팅방을 생성합니다.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							schema = @Schema(hidden = true),
							examples = @ExampleObject(name = "채팅방 생성 예시", ref = "#/components/examples/chat.post.chat-rooms")
					)
			)
	)
	public ResponseEntity<?> createChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			String memberId = authenticationFacadeService.getMemberId();
			boolean res = chatService.createChatRoom(chatRoomDTO, memberId);
			String msg = res ? "채팅방을 생성하였습니다" : "채팅방 생성에 실패하였습니다.";
			responseDTO.setSuccess(res);
			responseDTO.setMsg(msg);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg("서버에 문제가 발생하였습니다.");
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@PutMapping("/chat-rooms/{roomIdx}")
	@Tag(name = "채팅방")
	@Operation(
			summary = "채팅방 수정",
			description = "채팅방을 수정합니다.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							schema = @Schema(hidden = true),
							examples = @ExampleObject(name = "채팅방 수정 예시", ref = "#/components/examples/chat.put.chat-rooms")
					)
			)
	)
	public ResponseEntity<?> updateChatRoom(@Parameter(description = "채팅방 IDX") Integer roomIdx,
											@RequestBody ChatRoomDTO chatRoomDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			chatRoomDTO.setRoomIdx(roomIdx);
			String memberId = authenticationFacadeService.getMemberId();
			boolean res = chatService.updateChatRoom(chatRoomDTO, memberId);
			String msg = res ? "채팅방을 수정하였습니다" : "채팅방 수정에 실패하였습니다.";
			responseDTO.setSuccess(res);
			responseDTO.setMsg(msg);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg("서버에 문제가 발생하였습니다.");
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@GetMapping("/members/chat-rooms")
	@Tag(name = "채팅방")
	@Operation(
			summary = "사용자의 채팅방 목록 조회",
			description = "현재 사용자가 참가해있는 채팅방 목록을 조회."
	)
	public ResponseEntity<?> getChatRoomList() {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			String memberId = authenticationFacadeService.getMemberId();
			List<ChatRoomInfoDTO> chatRoomList = chatService.getChatRoomList(memberId);
			responseDTO.addData("chatRoomList", chatRoomList);
			responseDTO.setSuccess(true);
			responseDTO.setMsg("채팅방 목록 조회");
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg("서버에 문제가 발생하였습니다.");
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@GetMapping(value={"/chat-rooms/{roomIdx}/org-invitable", "/chat-rooms/org-invitable"})
	@Tag(name = "사용자")
	@Operation(summary = "조직도를 통해 채팅방에 초대가능한 사용자 조회", description = "이미 초대되어 있는 사람도 조회.<br>이미 초대된 사람의 경우 invited = 'Y'로 반환")
	public ResponseEntity<?> getOrganizationMember (@Parameter(description = "조직도 IDX") @RequestParam int organizationIdx,
													@Parameter(description = "하위 조직 멤버 포함여부") @RequestParam(required = false) String includeAllYn,
													@Parameter(description = "Room Idx") @PathVariable(required = false) String roomIdx) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			if (!"Y".equals(includeAllYn)) includeAllYn = "N";

			List<HashMap<String, Object>> memberList = chatService.getInvitableOrganizationMember(organizationIdx, includeAllYn, roomIdx);

			responseDTO.setSuccess(true);
			responseDTO.setMsg("조직의 사용자 조회");
			responseDTO.addData("memberList", memberList);
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

	@GetMapping(value={"/chat-rooms/{roomIdx}/invitable", "/chat-rooms/invitable"})
	@Tag(name = "사용자")
	@Operation(
			summary = "채팅방 초대가능 사용자 조회",
			description = "채팅방을 생성 시, 혹은 생성된 방에 사용자를 초대할 경우 추가 가능한 사용자 목록 조회."
	)
	public ResponseEntity<?> getInvitableMember(@Parameter(description = "채팅방 IDX")	@PathVariable(required = false) Integer roomIdx) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			LinkedList<Map<String, String>> invitableMemberList = chatService.getInvitableMember(roomIdx);
			responseDTO.addData("memberList", invitableMemberList);
			responseDTO.setSuccess(true);
			responseDTO.setMsg("초대가능한 사용자 목록 조회 성공");
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg("서버에 문제가 발생하였습니다.");
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@PostMapping("/chat-rooms/{roomIdx}/members")
	@Tag(name = "사용자")
	@Operation(
			summary = "채팅방 사용자 초대",
			description = "채팅방에 사용자를 초대합니다.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							schema = @Schema(hidden = true),
							examples = @ExampleObject(name = "채팅방 초대 예시", ref = "#/components/examples/chat.post.chat-rooms.roomId.members")
					)
			)
	)
	public ResponseEntity<?> inviteMember(@Parameter(description = "사용자 Id") int roomIdx,
										  @RequestBody ChatRoomMemberDTO chatRoomMemberDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			String memberId = authenticationFacadeService.getMemberId();
			chatRoomMemberDTO.setRoomIdx(roomIdx);
			boolean res = chatService.inviteMember(chatRoomMemberDTO, memberId);
			String msg = res ? "사용자를 초대하였습니다" : "사용자 초대에 실패하였습니다.";
			responseDTO.setSuccess(res);
			responseDTO.setMsg(msg);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg("서버에 문제가 발생하였습니다.");
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@GetMapping("/chat-rooms/{roomIdx}/chat-messages")
	@Tag(name = "채팅 메세지")
	@Operation(
			summary = "채팅방의 채팅조회",
			description = "현재 사용자가 참가해있는 채팅방 목록을 조회."
	)
	public ResponseEntity<?> getChatMessages(@PathVariable int roomIdx) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			String memberId = authenticationFacadeService.getMemberId();
			List<ChatMessageDTO> chatMessageList = chatService.getChatMessages(roomIdx, memberId);
			responseDTO.addData("chatMessageList", chatMessageList);
			responseDTO.setSuccess(true);
			responseDTO.setMsg("채팅방의 채팅내역 조회");
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg("서버에 문제가 발생하였습니다.");
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}

	@PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Tag(name="파일")
	@Operation(summary = "채팅방 파일 전송", description = "채팅방에 파일을 전송")
	public ResponseEntity<?> chatFilesSave(@Parameter(description = "RoomIdx") 			@RequestParam int roomIdx,
									  	   @Parameter(description = "파일") 				@RequestParam("fileList") List<MultipartFile> fileList,
										   @Parameter(description = "묶음 파일 여부") 	@RequestParam String bundleYn,
										   @Parameter(description = "타입(FILE/IMG)")	@RequestParam String messageType) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			if (fileList.isEmpty()) throw new RuntimeException("파일을 업로드 후 진행하여주십시오.");

			String memberId = authenticationFacadeService.getMemberId();
			boolean res = false;

			if ("Y".equals(bundleYn)) {
				res = chatService.chatBundleFilesSave(roomIdx, fileList, memberId, messageType);
			}
			else {
				res = chatService.chatFilesSave(roomIdx, fileList, memberId, messageType);
			}
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

	@GetMapping(value = "/files/{fileIdx}/download")
	@Tag(name="파일")
	@Operation(summary = "채팅방 파일 다운로드", description = "채팅방의 파일을 다운로드")
	public ResponseEntity<?> downloadFile(@Parameter(description = "파일 idx") @PathVariable Integer fileIdx) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			HashMap<String, Object> fileData = chatService.downloadFile(fileIdx);

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
}
