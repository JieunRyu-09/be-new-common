package kr.co.triphos.common.repository;

import kr.co.triphos.common.entity.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
	List<FileInfo> findByRealFileNmLike(String fileNm);
	Optional<FileInfo> findByFileIdx(Integer fileIdx);
	void deleteByFileIdx(Integer fileIdx);
}
