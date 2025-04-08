package kr.co.triphos.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
public class ChatRoomMsgDTO {
	private int msgIdx;
	private int roomIdx;
	private String memberId;
	private String content;
	private String messageType;
	private LocalDateTime insDt;
	private Boolean delYn;
}
