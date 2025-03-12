package kr.co.triphos.member.entity.pk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;


@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MenuMemberAuthEntityPK implements Serializable {
	private String 	menuId;
	private String 	memberId;

}
