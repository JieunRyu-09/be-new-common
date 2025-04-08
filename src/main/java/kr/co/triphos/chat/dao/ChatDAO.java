package kr.co.triphos.chat.dao;

import kr.co.triphos.manage.dao.AbstractDAO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import kr.co.triphos.chat.dto.ChatRoomInfoDTO;

import java.util.LinkedList;
import java.util.List;

@Repository("chatDAO")
@Log4j2
public class ChatDAO extends AbstractDAO {
	private final String _queryNamespace = "chat.";

	/**수급계획 작성 */
	public List<ChatRoomInfoDTO> getChatRoomList(String memberId) throws Exception {
		return selectList(_queryNamespace + "getChatRoomList", memberId);
	}

}
