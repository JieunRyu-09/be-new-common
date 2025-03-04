package kr.co.triphos.common.service;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SuppressWarnings("unchecked")
public class CommonFunc {

	public static boolean isBlank(Object object) {
		String parseObject = object.toString().trim();
		return parseObject.isEmpty() || parseObject == null;
	}
}
