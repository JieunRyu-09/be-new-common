package kr.co.triphos.common.entity.pk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Embeddable
public class ExcelEntityPK implements Serializable {
	private String	dataType;
	private String	memberId;
	private int		dataIdx;
}
