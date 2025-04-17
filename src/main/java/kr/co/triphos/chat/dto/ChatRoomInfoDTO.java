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
	private String  chatRoomType;
	private int 	memberCnt;
	private String 	lastChatMsg;
	private int 	unreadMessageCount;
	private LocalDateTime 	lastChatDt;

	@Builder(builderClassName = "createChatRoomInfo", builderMethodName = "createChatRoomInfo")
	public ChatRoomInfoDTO(int roomIdx,
						   String title,
						   String chatRoomType,
						   int memberCnt,
						   String lastChatMsg,
						   int unreadMessageCount,
						   LocalDateTime lastChatDt) {
		this.roomIdx 				= roomIdx;
		this.title 					= title;
		this.chatRoomType			= chatRoomType;
		this.memberCnt 				= memberCnt;
		this.lastChatMsg 			= lastChatMsg;
		this.unreadMessageCount 	= unreadMessageCount;
		this.lastChatDt				= lastChatDt;
	}
}
