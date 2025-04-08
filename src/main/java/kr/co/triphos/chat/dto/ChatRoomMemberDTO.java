package kr.co.triphos.chat.dto;

import kr.co.triphos.chat.entity.pk.ChatRoomMemberPK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
public class ChatRoomMemberDTO {
	private int roomIdx;
	private String memberId;
	private LocalDateTime inviteDt;
	private LocalDateTime lastReadDt;
	private int unreadCount;
}
