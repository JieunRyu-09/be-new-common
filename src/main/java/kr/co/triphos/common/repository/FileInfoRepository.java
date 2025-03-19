package kr.co.triphos.common.repository;

import kr.co.triphos.common.entity.ExcelInfo;
import kr.co.triphos.common.entity.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
	List<FileInfo> findByRealFileNmLike(String fileNm);
	Optional<FileInfo> findByFileIdx(Integer fileIdx);
}
