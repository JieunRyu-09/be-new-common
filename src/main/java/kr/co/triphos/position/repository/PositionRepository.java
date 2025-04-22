package kr.co.triphos.position.repository;

import kr.co.triphos.position.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {
	Optional<Position> findByPositionIdx(int positionIdx);

	Position findByPositionName(String positionName);

	List<Position> findByUseYn(String useYn);
}
