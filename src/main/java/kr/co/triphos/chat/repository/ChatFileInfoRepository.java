package kr.co.triphos.chat.repository;

import kr.co.triphos.chat.entity.ChatFileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatFileInfoRepository extends JpaRepository<ChatFileInfo, Integer> {
	Optional<ChatFileInfo> findByFileIdx(int fileIdx);
}
