package kr.co.triphos.manage.service;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InQueryDTO {
	private String namespace;
	private String tag;
	private Map<String, Object> param;
	private int pagePerSize = 20;
	private int viewPageNo = 1;
	private String sortIndex;
	private String sortType;
	private String authMenuId;
}
