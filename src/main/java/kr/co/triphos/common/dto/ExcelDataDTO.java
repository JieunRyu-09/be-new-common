package kr.co.triphos.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.triphos.common.entity.ExcelDataEntity;
import kr.co.triphos.common.entity.ExcelInfoEntity;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

@Data
@Log4j2
public class ExcelDataDTO {
	@Schema(description = "데이터 순서(row순번)")
	public int rowIdx;
	@Schema(description = "주문번호")
	private String orderNo;
	@Schema(description = "제품코드")
	private String productCode;
	@Schema(description = "제품명")
	private String productName;
	@Schema(description = "수량")
	private float  quantity;
	@Schema(description = "단가")
	private float  price;
	@Schema(description = "금액")
	private float  totalAmount;
	@Schema(description = "주문일 ex)20250313")
	private String orderDate;
	@Schema(description = "고객명")
	private String customerName;
	@Schema(description = "상태 (텍스트)")
	private String status;

	public ExcelDataDTO(ExcelDataEntity excelDataEntity) {
		this.rowIdx		= excelDataEntity.getPk().getRowIdx();
		this.orderNo		= excelDataEntity.getOrderNo();
		this.productCode	= excelDataEntity.getProductCode();
		this.productName	= excelDataEntity.getProductName();
		this.quantity		= excelDataEntity.getQuantity();
		this.price			= excelDataEntity.getPrice();
		this.totalAmount	= excelDataEntity.getTotalAmount();
		this.orderDate		= excelDataEntity.getOrderDate();
		this.customerName	= excelDataEntity.getCustomerName();
		this.status			= excelDataEntity.getStatus();
	}
}
