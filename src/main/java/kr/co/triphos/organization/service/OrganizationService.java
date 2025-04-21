package kr.co.triphos.organization.service;

import kr.co.triphos.common.entity.MenuInfo;
import kr.co.triphos.common.repository.MenuInfoRepository;
import kr.co.triphos.config.JwtUtil;
import kr.co.triphos.member.entity.Member;
import kr.co.triphos.member.entity.MenuMemberAuth;
import kr.co.triphos.member.repository.MemberRepository;
import kr.co.triphos.member.repository.MenuMemberAuthRepository;
import kr.co.triphos.organization.dao.OrganizationDAO;
import kr.co.triphos.organization.dto.OrganizationDTO;
import kr.co.triphos.organization.entity.Organization;
import kr.co.triphos.organization.repository.OrganizationRepository;
import kr.co.triphos.organization.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrganizationService {
	private final OrganizationRepository organizationRepository;
	private final PositionRepository positionRepository;

	private final OrganizationDAO organizationDAO;

	public List<OrganizationDTO> getOrganization() throws Exception{
		List<Organization> entities = organizationRepository.findByUseYn("Y", Sort.by("level", "depth1", "depth2", "depth3", "depth4", "depth5"));
		List<OrganizationDTO> dtos = entities.stream()
				.map(OrganizationDTO::new)
				.collect(Collectors.toList());

		// selfKey 기준으로 Map 구성
		Map<String, OrganizationDTO> dtoMap = dtos.stream()
				.collect(Collectors.toMap(OrganizationDTO::getSelfKey, Function.identity()));

		List<OrganizationDTO> roots = new ArrayList<>();

		for (OrganizationDTO dto : dtos) {
			if (dto.getParentKey() == null) {
				// 최상위 조직이면 root에 추가
				roots.add(dto);
			} else {
				// 부모가 있으면 children에 추가
				OrganizationDTO parent = dtoMap.get(dto.getParentKey());
				if (parent != null) {
					parent.getChildren().add(dto);
				}
			}
		}

		return roots;
	}

	public List<HashMap<String, Object>> getOrganizationMember (int organizationIdx, String includeAllYn) throws Exception {
		return organizationDAO.getOrganizationMember(organizationIdx, includeAllYn);
	}




}
