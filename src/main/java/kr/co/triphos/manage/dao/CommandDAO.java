package kr.co.triphos.manage.dao;

import kr.co.triphos.common.service.CommonFunc;
import kr.co.triphos.common.service.JSON;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

@Repository
@Log4j2
@SuppressWarnings("unchecked")
public class CommandDAO extends AbstractDAO {

	public List<JSON> selectRows(String namespace, String tag, Map<String, Object> parameters) {
		ArrayList<JSON> rows = new ArrayList<>();

		String queryId = String.format("%s.%s", namespace, tag);
		List<HashMap<String, Object>> results = selectList(queryId, parameters);
		int index = (parameters.get("_page") == null) ? 0 : (int) parameters.get("_page");

		for (HashMap<String, Object> row : results) {
			if (row == null) {
				continue;
			}

			JSON o = new JSON();
			o.putAll(row);

			for (Object o2 : o.keySet()) {
				String key = String.valueOf(o2);
				Object value = o.get(key);

				// 날짜+시간 형식을 문자열로 변환
				if (value == null) {
					o.put(key, "");
				}
				else if (value instanceof Date) {
					o.put(key, String.valueOf(value));
				}
				else if (value instanceof Time) {
					o.put(key, String.valueOf(value));
				}
				else if (value instanceof Timestamp) {
					o.put(key, String.valueOf(value));
				}
			}

			o.put("_id", index);

			rows.add(CommonFunc.convertFromJSONObject(o));
			++index;
		}

		return rows;
	}
}
