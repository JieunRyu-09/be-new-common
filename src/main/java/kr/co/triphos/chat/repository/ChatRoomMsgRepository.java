package kr.co.triphos.chat.repository;

import kr.co.triphos.chat.entity.ChatRoomMsg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomMsgRepository extends JpaRepository<ChatRoomMsg, Long> {
}
