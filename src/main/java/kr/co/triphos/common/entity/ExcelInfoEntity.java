package kr.co.triphos.common.entity;

import kr.co.triphos.common.entity.pk.ExcelEntityPK;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Table(name = "mm_excel_info")
public class ExcelInfoEntity {
	@Id
	@Nullable
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer			idx;
	private String			excelNm;
	private LocalDateTime 	insDt;
	private String			insMember;
	private LocalDateTime	updDt;
	private String			updMember;

	public ExcelInfoEntity(String excelNm, LocalDateTime insDt, String insMember) {
		this.excelNm 	= excelNm;
		this.insDt		= insDt;
		this.insMember	= insMember;
	}

	public void updateExcelInfoEntity(String excelNm, LocalDateTime updDt, String updMember) {
		this.excelNm 	= excelNm;
		this.updDt		= updDt;
		this.updMember	= updMember;
	}
}
