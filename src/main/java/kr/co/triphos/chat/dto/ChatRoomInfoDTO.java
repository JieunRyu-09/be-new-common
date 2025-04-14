package kr.co.triphos.chat.dto;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Log4j2
@NoArgsConstructor
@Builder
public class ChatRoomInfoDTO {
	private int		roomIdx;
	private String 	title;
	private int 	memberCnt;
	private String 	lastChatMsg;
	private int 	unreadCount;

	@Builder(builderClassName = "createChatRoomInfo", builderMethodName = "createChatRoomInfo")
	public ChatRoomInfoDTO(int roomIdx,
						   String title,
						   int memberCnt,
						   String lastChatMsg,
						   int unreadCount) {
		this.roomIdx 		= roomIdx;
		this.title 			= title;
		this.memberCnt 		= memberCnt;
		this.lastChatMsg 	= lastChatMsg;
		this.unreadCount 	= unreadCount;
	}
}
