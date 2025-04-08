package kr.co.triphos.chat.entity;

import kr.co.triphos.chat.entity.pk.ChatRoomMemberPK;
import kr.co.triphos.member.entity.pk.MenuMemberAuthPK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "ChatRoomMember")
@Log4j2
@Table(name = "chat_room_member")
public class ChatRoomMember {
	@EmbeddedId
	private ChatRoomMemberPK pk;
	@NotNull
	private LocalDateTime inviteDt;
	private LocalDateTime lastReadDt;
	private int unreadCount;
}
