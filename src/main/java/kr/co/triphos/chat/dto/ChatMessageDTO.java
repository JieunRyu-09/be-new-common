package kr.co.triphos.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // 기본 생성자 추가 (롬복 사용 시)
@AllArgsConstructor
public class ChatMessageDTO {
	private String sender;
	private String content;
	private MessageType type = MessageType.TEXT;

	public enum MessageType {
		TEXT, JOIN, LEAVE, FILE, IMG
	}
}
