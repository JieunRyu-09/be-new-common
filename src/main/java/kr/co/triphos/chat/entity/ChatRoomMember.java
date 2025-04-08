package kr.co.triphos.chat.entity;

import kr.co.triphos.chat.entity.pk.ChatRoomMemberPK;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "ChatRoomMember")
@Log4j2
@Table(name = "chat_room_member")
public class ChatRoomMember {
	@EmbeddedId
	private ChatRoomMemberPK pk;
	@NotNull
	private String inviteId;
	@NotNull
	private LocalDateTime inviteDt;
	private LocalDateTime lastReadDt;
	@NotNull
	private int unreadCount;

	@Builder(builderClassName = "createChatRoomMember", builderMethodName = "createChatRoomMember")
	public ChatRoomMember(int roomIdx,
						  String memberId,
						  String inviteId) {
		this.pk = new ChatRoomMemberPK(roomIdx, memberId); //
		this.inviteId = inviteId;
		this.inviteDt = LocalDateTime.now();
		this.unreadCount = 0;
	}
}
