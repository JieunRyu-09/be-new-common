package kr.co.triphos.chat.entity.pk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;


@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMemberPK implements Serializable {
	private int 	roomIdx;
	private String 	memberId;

}
