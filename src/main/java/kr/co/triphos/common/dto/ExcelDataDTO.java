package kr.co.triphos.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.triphos.common.entity.ExcelData;
import kr.co.triphos.common.entity.ExcelInfo;
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

	public ExcelDataDTO(ExcelData excelData) {
		this.rowIdx		= excelData.getPk().getRowIdx();
		this.orderNo		= excelData.getOrderNo();
		this.productCode	= excelData.getProductCode();
		this.productName	= excelData.getProductName();
		this.quantity		= excelData.getQuantity();
		this.price			= excelData.getPrice();
		this.totalAmount	= excelData.getTotalAmount();
		this.orderDate		= excelData.getOrderDate();
		this.customerName	= excelData.getCustomerName();
		this.status			= excelData.getStatus();
	}
}
