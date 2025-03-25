package kr.co.triphos.common.repository;

import kr.co.triphos.common.entity.ExcelData;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExcelDataRepository extends JpaRepository<ExcelData, Long> {
	List<ExcelData> findByPkIdx(int idx);

	@Modifying
	@Query(value = "DELETE FROM mm_excel_data WHERE IDX = :idx AND ROW_IDX = :rowIdx", nativeQuery = true)
	void deleteByIdxAndRowIdx(@Param("idx") int idx, @Param("rowIdx") int rowIdx);

	void deleteByPkIdx(int idx);
}
