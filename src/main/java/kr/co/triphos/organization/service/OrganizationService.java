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
import kr.co.triphos.organization.dto.OrganizationInfoDTO;
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

import javax.transaction.Transactional;
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

	public List<OrganizationInfoDTO> getOrganization() throws Exception{
		List<Organization> entities = organizationRepository.findByUseYn("Y", Sort.by("level", "depth1", "depth2", "depth3", "depth4", "depth5"));
		List<OrganizationInfoDTO> dtos = entities.stream()
				.map(OrganizationInfoDTO::new)
				.collect(Collectors.toList());

		// selfKey 기준으로 Map 구성
		Map<String, OrganizationInfoDTO> dtoMap = dtos.stream()
				.collect(Collectors.toMap(OrganizationInfoDTO::getSelfKey, Function.identity()));

		List<OrganizationInfoDTO> roots = new ArrayList<>();

		for (OrganizationInfoDTO dto : dtos) {
			if (dto.getParentKey() == null) {
				// 최상위 조직이면 root에 추가
				roots.add(dto);
			} else {
				// 부모가 있으면 children에 추가
				OrganizationInfoDTO parent = dtoMap.get(dto.getParentKey());
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

	@Transactional
	public void createOrganization (OrganizationDTO organizationDTO) throws Exception {
		Integer depth1 = organizationDTO.getDepth1();
		Integer depth2 = organizationDTO.getDepth2();
		Integer depth3 = organizationDTO.getDepth3();
		Integer depth4 = organizationDTO.getDepth4();
		Integer depth5 = organizationDTO.getDepth5();
		int level = organizationDTO.getLevel();

		int newValue = organizationDAO.getPreviousDepthValue(organizationDTO) + 1;

		String organizationKey = organizationDTO.getOrganizationKey() + "-" + newValue;
		organizationDTO.setOrganizationKey(organizationKey);

		if (depth1 == null) organizationDTO.setDepth1(newValue);
		else if (depth2 == null) organizationDTO.setDepth2(newValue);
		else if (depth3 == null) organizationDTO.setDepth3(newValue);
		else if (depth4 == null) organizationDTO.setDepth4(newValue);
		else if (depth5 == null) organizationDTO.setDepth5(newValue);


		Organization organization = Organization.createEntityByDTO(organizationDTO);
		organizationRepository.save(organization);
	}



}
