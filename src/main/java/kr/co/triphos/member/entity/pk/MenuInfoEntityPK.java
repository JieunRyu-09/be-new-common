package kr.co.triphos.member.entity.pk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;


@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MenuInfoEntityPK implements Serializable {
	private int	mainCd;
	@Column(name = "SUB1_CD")
	private int sub1Cd;
	@Column(name = "SUB2_CD")
	private int sub2Cd;
}
