package kr.co.triphos.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.chat.service.ChatService;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.member.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
@Log4j2
public class ChatController {
	private final ChatService chatService;

	@PostMapping("/chat-room")
	@Tag(name = "채팅")
	@Operation(
			summary = "채팅방 생성",
			description = "채팅방을 생성합니다.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							schema = @Schema(hidden = true),
							examples = @ExampleObject(name = "채팅방 생성 예시", ref = "#/components/examples/chat.post.chat-room")
					)
			)
	)
	public ResponseEntity<?> updateMyInfo(@RequestBody MemberDTO memberDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			boolean res = true;
			String msg = res ? "채팅방을 생성하였습니다" : "채팅방 생성에 실패하였습니다.";
			responseDTO.setSuccess(res);
			responseDTO.setMsg(msg);
			return ResponseEntity.ok().body(responseDTO);
		}
		catch (Exception ex) {
			log.error(ex);
			responseDTO.setMsg(ex.getMessage());
			return ResponseEntity.internalServerError().body(responseDTO);
		}
	}



}
