package kr.co.triphos.organization.repository;

import kr.co.triphos.organization.entity.Organization;
import kr.co.triphos.organization.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface PositionRepository extends JpaRepository<Position, Long> {

}
