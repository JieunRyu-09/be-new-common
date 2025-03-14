package kr.co.triphos.common.repository;

import kr.co.triphos.common.entity.ExcelDataEntity;
import kr.co.triphos.common.entity.ExcelInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExcelDataRepository extends JpaRepository<ExcelDataEntity, Long> {

}
