package kr.co.triphos.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Log4j2
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomInfoDTO {
	private int	roomIdx;
	private String title;
	private int memberCnt;
	private String lastChatMsg;
}
