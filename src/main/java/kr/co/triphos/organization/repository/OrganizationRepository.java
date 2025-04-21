package kr.co.triphos.organization.repository;

import kr.co.triphos.member.entity.Member;
import kr.co.triphos.organization.entity.Organization;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
	List<Organization> findByUseYn(String useYn, Sort sort);

	Organization findByOrganizationKey(String organizationKey);

}
