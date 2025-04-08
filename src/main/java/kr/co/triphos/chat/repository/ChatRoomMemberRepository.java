package kr.co.triphos.chat.repository;

import kr.co.triphos.chat.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
	List<ChatRoomMember> findByPkRoomIdx(Integer roomIdx);
}
