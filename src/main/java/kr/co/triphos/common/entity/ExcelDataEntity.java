package kr.co.triphos.common.entity;

import kr.co.triphos.common.entity.pk.ExcelDataEntityPK;
import kr.co.triphos.common.entity.pk.ExcelEntityPK;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Table(name = "mm_excel_data")
public class ExcelDataEntity {
	@EmbeddedId
	ExcelDataEntityPK pk;

	private String orderNo;
	private String productCode;
	private String productName;
	private float  quantity;
	private float  price;
	private float  totalAmount;
	private String orderDate;
	private String customerName;
	private String status;
}
