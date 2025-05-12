package kr.co.triphos.chat.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import kr.co.triphos.chat.entity.pk.ChatRoomMemberPK;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "ChatRoomMember")
@Log4j2
@Table(name = "ch_chat_room_member")
public class ChatRoomMember {
	@EmbeddedId
	private ChatRoomMemberPK pk;
	@NotNull
	private String delYn;
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
		this.delYn = "N";
	}
}
