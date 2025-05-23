package kr.co.triphos.common.entity.pk;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Embeddable
public class ExcelDataPK implements Serializable {
	private int idx;
	private int rowIdx;
}
