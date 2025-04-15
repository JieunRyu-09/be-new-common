package kr.co.triphos.chat.repository;

import kr.co.triphos.chat.entity.ChatFileInfo;
import kr.co.triphos.common.entity.FileInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatFileInfoRepository extends JpaRepository<ChatFileInfo, Long> {
}
