package kr.co.triphos.chat.dao;

import kr.co.triphos.chat.dto.ChatMessageDTO;
import kr.co.triphos.manage.dao.AbstractDAO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import kr.co.triphos.chat.dto.ChatRoomInfoDTO;

import java.util.*;

import static java.lang.Math.E;

@Repository("chatDAO")
@Log4j2
public class ChatDAO extends AbstractDAO {
	private final String _queryNamespace = "chat.";

	/** 채팅방 목록 조회 */
	public List<ChatRoomInfoDTO> getChatRoomList(String memberId) throws Exception {
		return selectList(_queryNamespace + "getChatRoomList", memberId);
	}

	/** 채팅방 채팅내역 조회 */
	public List<ChatMessageDTO> getChatMessages(int roomIdx, Integer startIdx, int pageSize) throws Exception {
		Map<String, Object> dto = new HashMap<>();
		dto.put("roomIdx", roomIdx);
		dto.put("startIdx", startIdx);
		dto.put("pageSize", pageSize);
		return selectList(_queryNamespace + "getChatMessages", dto);
	}

	/** 입력한 사용자가 공통으로 참여해있는 채팅방 조회 */
	public List<ChatRoomInfoDTO> getCommonChatRooms(String memberId, List<String> memberIdList) throws Exception {
		Map<String, Object> dto = new HashMap<>();
		dto.put("memberId", memberId);
		dto.put("memberIdList", memberIdList);
		dto.put("memberCnt", memberIdList.size());
		return selectList(_queryNamespace + "getCommonChatRooms", dto);
	}

}
