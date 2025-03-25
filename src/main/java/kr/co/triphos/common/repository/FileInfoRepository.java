package kr.co.triphos.common.repository;

import kr.co.triphos.common.entity.FileInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
	Optional<FileInfo> findByFileIdx(Integer fileIdx);
	void deleteByFileIdx(Integer fileIdx);

	@Query(value = "SELECT * FROM sy_file_info T1 WHERE DATE_FORMAT(T1.INS_DT, '%Y%m%d') BETWEEN :fromDate AND :toDate", nativeQuery = true)
	List<FileInfo> findByPeriod(@Param("fromDate") String fromDate, @Param("toDate") String toDate);

	@Query(value = "SELECT * FROM sy_file_info T1 WHERE DATE_FORMAT(T1.INS_DT, '%Y%m%d') BETWEEN :fromDate AND :toDate AND REAL_FILE_NM LIKE :fileNm", nativeQuery = true)
	List<FileInfo> findByRealFileNmAndPeriod(@Param("fromDate") String fromDate, @Param("toDate") String toDate, @Param("fileNm")String fileNm);
}
