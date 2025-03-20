package kr.co.triphos.test.repository;

import kr.co.triphos.test.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TestRepository extends JpaRepository<Test, Long> {
	@Query(value = "SELECT SLEEP(15)", nativeQuery = true)
	void testDBTimeout();

}
