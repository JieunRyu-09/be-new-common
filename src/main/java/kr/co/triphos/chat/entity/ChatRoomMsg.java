package kr.co.triphos.chat.entity;

import lombok.*;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "ChatRoomMsg")
@Log4j2
@Table(name = "ch_chat_room_msg")
public class ChatRoomMsg {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer msgIdx;

	private int roomIdx;
	private String memberId;
	private String content;
	private String messageType;
	private LocalDateTime insDt;
	private String delYn;

	@Builder(builderClassName = "createTextChatRoomMsg", builderMethodName = "createTextChatRoomMsg")
	public ChatRoomMsg(int roomIdx,
					   @NonNull String memberId,
					   @NonNull String content,
					   String messageType) {
		this.roomIdx = roomIdx;
		this.memberId = memberId;
		this.content = content;
		this.messageType = messageType;
		this.insDt = LocalDateTime.now();
		this.delYn = "N";

	}
}
