package kr.co.triphos.common.repository;

import kr.co.triphos.common.entity.ExcelInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExcelInfoRepository extends JpaRepository<ExcelInfo, Long> {
	Optional<ExcelInfo> findByIdx(Integer idx);

	List<ExcelInfo> findAll();

	List<ExcelInfo> findByExcelNmLike(String excelNm);

	@Query(value = "SELECT IFNULL(MAX(IDX), 1) FROM mm_excel_info", nativeQuery = true)
	int getExcelInfoMaxIdx();

	void deleteByIdx(int idx);
}
