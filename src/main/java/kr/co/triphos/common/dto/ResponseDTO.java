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
}
