package kr.co.triphos.chat.repository;

import kr.co.triphos.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	Optional<ChatRoom> findByRoomIdx(int roomIdx);
}
