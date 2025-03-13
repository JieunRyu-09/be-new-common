package kr.co.triphos.common.entity;

import kr.co.triphos.common.entity.pk.ExcelEntityPK;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Table(name = "mm_excel_info")
public class ExcelInfoEntity {
	@Id
	private int 			idx;
	private String			excelNm;
	private LocalDateTime 	insDt;
	private String			insMember;
	private LocalDateTime	updDt;
	private String			updMember;
}
