package kr.co.triphos.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.triphos.chat.entity.pk.ChatRoomMemberPK;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor  // 기본 생성자 추가 (롬복 사용 시)
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
	private int msgIdx;
	private String memberId;
	private String memberNm;
	private String content;
	@Builder.Default
	private MessageType type = MessageType.TEXT;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime sendTime;
	@Builder.Default
	private String bundleYn = "N";

	public enum MessageType {
		TEXT, JOIN, LEAVE, FILE, IMG
	}
}
