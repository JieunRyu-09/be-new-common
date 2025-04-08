package kr.co.triphos.chat.entity;

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
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "ChatRoom")
@Log4j2
@Table(name = "chat_room")
public class ChatRoom {
	@Id
	private int	roomIdx;
	@NotNull
	private String title;
	@NotNull
	private String createMemberId;
	private LocalDateTime createDt;
	private String lastChatMemberId;
	private String lastChatMsg;
	private LocalDateTime lastChatDt;
}
