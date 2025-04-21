package kr.co.triphos.organization.dao;

import kr.co.triphos.chat.dto.ChatMessageDTO;
import kr.co.triphos.chat.dto.ChatRoomInfoDTO;
import kr.co.triphos.manage.dao.AbstractDAO;
import kr.co.triphos.organization.dto.OrganizationDTO;
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

	/** 조직생성 시 현재까지 생성된 조직중 동레벨의 가장 큰 depth value 조회 */
	public int getPreviousDepthValue(OrganizationDTO dto) throws Exception {
		return selectOne(_queryNamespace + "getPreviousDepthValue", dto);
	}

}
