package kr.co.triphos.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.log4j.Log4j2;

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
	private String bundleYn;
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
		this.bundleYn = "N";
		this.insDt = LocalDateTime.now();
		this.delYn = "N";
	}

	@Builder(builderClassName = "createFilesChatRoomMsg", builderMethodName = "createFilesChatRoomMsg")
	public ChatRoomMsg(int roomIdx,
					   @NonNull String memberId,
					   @NonNull String content,
					   String messageType,
					   String bundleYn) {
		this.roomIdx = roomIdx;
		this.memberId = memberId;
		this.content = content;
		this.messageType = messageType;
		this.bundleYn = bundleYn;
		this.insDt = LocalDateTime.now();
		this.delYn = "N";
	}
}
