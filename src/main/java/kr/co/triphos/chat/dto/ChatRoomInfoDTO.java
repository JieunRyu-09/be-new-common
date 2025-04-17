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
	private List<String> participantIds;
	private String 	lastChatMsg;
	private int 	unreadMessageCount;
	private LocalDateTime 	timestamp;

	@Builder(builderClassName = "createChatRoomInfo", builderMethodName = "createChatRoomInfo")
	public ChatRoomInfoDTO(int roomIdx,
						   String title,
						   List<String> participantIds,
						   String lastChatMsg,
						   int unreadMessageCount,
						   LocalDateTime timestamp) {
		this.roomIdx 				= roomIdx;
		this.title 					= title;
		this.participantIds 		= participantIds;
		this.lastChatMsg 			= lastChatMsg;
		this.unreadMessageCount 	= unreadMessageCount;
		this.timestamp				= timestamp;
	}
}
