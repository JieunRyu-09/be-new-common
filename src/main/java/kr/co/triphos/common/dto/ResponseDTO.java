package kr.co.triphos.common.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ResponseDTO {
	private boolean success = false;    // default true
	private HashMap<String, Object> data; 	// 데이터 저장용 Map
	private String msg;               	// 메시지

	public ResponseDTO() {
		this.data = new HashMap<>();
	}

	public ResponseDTO(boolean success, String msg) {
		this.success = success;
		this.msg = msg;
		this.data = new HashMap<>();
	}

	public void addData(String key, Object value) {
		this.data.put(key, value);
	}

	// Map을 한번에 저장
	public void addData(Map<String, Object> data) {this.data.putAll(data);}

	public void deleteData() {this.data = null;}

	// ResponseDTO 생성
	public static ResponseDTO create(String message) {
		return create(message, null);
	}

	// ResponseDTO 생성
	public static ResponseDTO create(String message, Map<String, Object> data) {
		ResponseDTO dto = new ResponseDTO();
		dto.setSuccess(false);
		dto.setMsg(message);
		if(dto != null) dto.addData(data);
		return dto;
	}
}
