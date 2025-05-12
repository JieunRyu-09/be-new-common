package kr.co.triphos.common.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kr.co.triphos.common.dto.ExcelDTO;
import kr.co.triphos.common.entity.pk.ExcelDataPK;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Table(name = "mm_excel_data")
public class ExcelData {
	@EmbeddedId
	ExcelDataPK pk;

	private String orderNo;
	private String productCode;
	private String productName;
	private float  quantity;
	private float  price;
	private float  totalAmount;
	private String orderDate;
	private String customerName;
	private String status;

	public ExcelData(Integer idx, Integer rowIdx, ExcelDTO.excelData excelData) {
		this.pk 			= new ExcelDataPK(idx, rowIdx);
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
