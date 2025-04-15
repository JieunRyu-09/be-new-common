package kr.co.triphos.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.triphos.chat.entity.pk.ChatRoomMemberPK;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor  // 기본 생성자 추가 (롬복 사용 시)
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
	private String memberId;
	private String memberNm;
	private String content;
	private MessageType type = MessageType.TEXT;
	private LocalDateTime sendTime;

	private int fileIdx;
	private String fileUrl;
	private String fileName;

	public enum MessageType {
		TEXT, JOIN, LEAVE, FILE, IMG
	}
}
