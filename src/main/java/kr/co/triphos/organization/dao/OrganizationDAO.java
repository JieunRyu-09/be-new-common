package kr.co.triphos.organization.dao;

import kr.co.triphos.chat.dto.ChatMessageDTO;
import kr.co.triphos.chat.dto.ChatRoomInfoDTO;
import kr.co.triphos.manage.dao.AbstractDAO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("organizationDAO")
@Log4j2
public class OrganizationDAO extends AbstractDAO {
	private final String _queryNamespace = "organization.";

	/** 조직의 사용자 목록 조회 */
	public List<HashMap<String, Object>> getOrganizationMember(int organizationIdx, String includeAllYn) throws Exception {
		Map<String, Object> dto = new HashMap<>();
		dto.put("organizationIdx", organizationIdx);
		dto.put("includeAllYn", includeAllYn);
		return selectList(_queryNamespace + "getOrganizationMember", dto);
	}

}
