package kr.co.triphos.common.entity.pk;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MenuInfoPK implements Serializable {
	private int	mainCd;
	@Column(name = "SUB1_CD")
	private int sub1Cd;
	@Column(name = "SUB2_CD")
	private int sub2Cd;
}
