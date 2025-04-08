package kr.co.triphos.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "ChatRoomMsg")
@Log4j2
@Table(name = "chat_room_msg")
public class ChatRoomMsg {
	@Id
	private int msgIdx;
	private int roomIdx;
	private String memberId;
	private String content;
	private String messageType;
	private LocalDateTime insDt;
	private Boolean delYn;
}
