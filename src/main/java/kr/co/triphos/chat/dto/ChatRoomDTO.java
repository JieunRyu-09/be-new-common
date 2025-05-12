package kr.co.triphos.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Log4j2
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
	private int	roomIdx;
	private String title;
	private String chatRoomType;
	private int memberCnt;
	private String insId;
	private LocalDateTime insDt;
	private String updId;
	private LocalDateTime updDt;
	private String lastChatMemberId;
	private String lastChatMsg;
	private LocalDateTime lastChatDt;

	private List<String> inviteMemberIdList;
}
