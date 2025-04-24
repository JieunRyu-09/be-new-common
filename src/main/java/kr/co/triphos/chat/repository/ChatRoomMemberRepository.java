package kr.co.triphos.chat.repository;

import kr.co.triphos.chat.entity.ChatRoomMember;
import kr.co.triphos.chat.entity.pk.ChatRoomMemberPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
	List<ChatRoomMember> findByPkRoomIdx(Integer roomIdx);

	List<ChatRoomMember> findByPkRoomIdxAndDelYn(Integer roomIdx, String delYn);

	ChatRoomMember findByPk(ChatRoomMemberPK pk);

	ChatRoomMember findByPkAndDelYn(ChatRoomMemberPK pk, String delYn);
}
