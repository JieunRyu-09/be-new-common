package kr.co.triphos.chat.entity;

import lombok.*;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "ChatRoom")
@Log4j2
@Table(name = "ch_chat_room")
public class ChatRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer	roomIdx;
	@NotNull
	private String title;
	@NotNull
	private int memberCnt;
	@NotNull
	private String insId;
	@NotNull
	private LocalDateTime insDt;
	private String updId;
	private LocalDateTime updDt;
	private String lastChatMemberId;
	private String lastChatMsg;
	private LocalDateTime lastChatDt;

	@Builder(builderClassName = "createChatRoom", builderMethodName = "createChatRoom")
	public ChatRoom(@NonNull String memberId,
					@NonNull String title,
					int memberCnt) {
		this.title	= title;
		this.insId	= memberId;
		this.memberCnt = memberCnt;
		this.insDt	= LocalDateTime.now();
	}
}
