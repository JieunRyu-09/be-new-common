package kr.co.triphos.chat.entity.pk;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMemberPK implements Serializable {
	private int 	roomIdx;
	private String 	memberId;

}
