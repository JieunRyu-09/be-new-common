package kr.co.triphos.chat.repository;

import kr.co.triphos.chat.entity.ChatRoomMsg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomMsgRepository extends JpaRepository<ChatRoomMsg, Long> {
	List<ChatRoomMsg> findByRoomIdxOrderByMsgIdxDesc(int roomIdx, Pageable pageable);
}
