package kr.co.triphos.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Log4j2
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
	private int	roomIdx;
	private String title;
	private String createMemberId;
	private LocalDateTime createDt;
	private String lastChatMemberId;
	private String lastChatMsg;
	private LocalDateTime lastChatDt;
}
