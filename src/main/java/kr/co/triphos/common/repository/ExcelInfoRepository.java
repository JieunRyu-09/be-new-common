package kr.co.triphos.common.repository;

import kr.co.triphos.common.entity.ExcelInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExcelInfoRepository extends JpaRepository<ExcelInfo, Long> {
	Optional<ExcelInfo> findByIdx(Integer idx);

	List<ExcelInfo> findAll();

	@Query(value = "SELECT IFNULL(MAX(IDX), 1) FROM mm_excel_info", nativeQuery = true)
	int getExcelInfoMaxIdx();

	void deleteByIdx(int idx);

	@Query(value = "SELECT * FROM mm_excel_info WHERE DATE_FORMAT(INS_DT, '%Y%m%d') BETWEEN :fromDate AND :toDate", nativeQuery = true)
	List<ExcelInfo> findByPeriod(@Param("fromDate")String fromDate, @Param("toDate") String toDate);

	@Query(value = "SELECT * FROM mm_excel_info WHERE DATE_FORMAT(INS_DT, '%Y%m%d') BETWEEN :fromDate AND :toDate AND EXCEL_NM LIKE :excelNm", nativeQuery = true)
	List<ExcelInfo> findByExcelNmAndPeriod(@Param("fromDate")String fromDate, @Param("toDate") String toDate, @Param("excelNm")String excelNm);
}
