package kr.co.triphos.member.entity.pk;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MenuMemberAuthPK implements Serializable {
	private String 	menuId;
	private String 	memberId;

}
