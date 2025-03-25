package kr.co.triphos.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class RequestDTO {
	@Schema
	private List<String> sort;
	private List<String> orderBy;
	private int 	page;
	private int		size;

	@JsonIgnore
	private Pageable pageable;

	public Pageable getPageable() {
		if (pageable == null) {
			pageable = createPageable();
		}
		return pageable;
	}

	private Pageable createPageable() {
		Sort sortObj = Sort.unsorted();

		if (sort != null && orderBy != null && sort.size() == orderBy.size()) {
			List<Sort.Order> orders = new ArrayList<>();
			for (int i = 0; i < sort.size(); i++) {
				Sort.Direction direction = "desc".equalsIgnoreCase(orderBy.get(i)) ? Sort.Direction.DESC : Sort.Direction.ASC;
				orders.add(new Sort.Order(direction, sort.get(i)));
			}
			sortObj = Sort.by(orders);
		}

		return (Pageable) PageRequest.of(page, size, sortObj);
	}

}
