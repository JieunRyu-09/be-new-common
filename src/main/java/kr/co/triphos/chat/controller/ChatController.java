package kr.co.triphos.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.chat.dto.ChatRoomDTO;
import kr.co.triphos.chat.dto.ChatRoomMemberDTO;
import kr.co.triphos.chat.entity.ChatRoomMember;
import kr.co.triphos.chat.service.ChatService;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.common.service.AuthenticationFacadeService;
import kr.co.triphos.member.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
@Log4j2
public class ChatController {
	private final ChatService chatService;
	private final AuthenticationFacadeService authenticationFacadeService;

	@PostMapping("/chat-rooms")
	@Tag(name = "채팅")
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

	@GetMapping(value={"/chat-rooms/{roomIdx}/members", "/chat-rooms/members"})
	@Tag(name = "채팅")
	@Operation(
			summary = "채팅방 사용자 조회",
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

	@PutMapping("/chat-rooms/{roomIdx}")
	@Tag(name = "채팅")
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

	@PostMapping("/chat-rooms/{roomIdx}/members")
	@Tag(name = "채팅")
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



}
