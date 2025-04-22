package kr.co.triphos.organization.service;

import kr.co.triphos.member.entity.Member;
import kr.co.triphos.member.repository.MemberRepository;
import kr.co.triphos.organization.dao.OrganizationDAO;
import kr.co.triphos.organization.dto.OrganizationDTO;
import kr.co.triphos.organization.dto.OrganizationInfoDTO;
import kr.co.triphos.organization.entity.Organization;
import kr.co.triphos.organization.repository.OrganizationRepository;
import kr.co.triphos.position.entity.Position;
import kr.co.triphos.position.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrganizationService {
	private final MemberRepository memberRepository;
	private final PositionRepository positionRepository;
	private final OrganizationRepository organizationRepository;
	private final OrganizationDAO organizationDAO;

	public List<OrganizationInfoDTO> getOrganization(String findAllYn) throws Exception{
		List<Organization> entities = new ArrayList<>();
		if (findAllYn.equals("Y")) {
			entities = organizationRepository.findAll(Sort.by("level", "depth1", "depth2", "depth3", "depth4", "depth5"));
		}
		else {
			entities = organizationRepository.findByUseYn("Y", Sort.by("level", "depth1", "depth2", "depth3", "depth4", "depth5"));
		}
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

	@Transactional
	public void createOrganization (OrganizationDTO organizationDTO) throws Exception {
		// dto 초기설정
		organizationDTO.setDepthById(organizationDTO.getOrganizationId());

		// 신규 깊이값 조회 및 설정
		int newDepth = organizationDAO.getPreviousDepthValue(organizationDTO);

		// 1-1-1-1와 같은 ID값 생성 및 설정
		String organizationId = organizationDTO.getOrganizationId() + "-" + newDepth;
		organizationDTO.updateDepthById(organizationId);

		// entity 저장
		Organization organization = Organization.createEntityByDTO(organizationDTO);
		organizationRepository.save(organization);
	}

	@Transactional
	public void updateOrganization (OrganizationDTO organizationDTO) throws Exception {
		LocalDateTime nowDate = LocalDateTime.now();
		Organization organization = organizationRepository.findByOrganizationIdx(organizationDTO.getOrganizationIdx())
				.orElseThrow(() -> new RuntimeException("잘못된 조직정보입니다."));
		organization.setUpdId(organizationDTO.getUpdId());
		organization.setUpdDt(nowDate);

		String dtoId = organizationDTO.getOrganizationId();
		boolean isSameName = Objects.equals(organizationDTO.getOrganizationName(), organization.getOrganizationName());
		boolean isSameId = organizationDTO.getOrganizationId().equals(organization.getOrganizationId());

		// 이름만 변경할려고 했다면
		if ((dtoId == null || dtoId == "")) {
			if (isSameName) {
				throw new RuntimeException("변경사항이 없습니다.");
			}
			else {
				organization.setOrganizationName(organizationDTO.getOrganizationName());
				organizationRepository.save(organization);
				return;
			}
		}
		if (isSameId) {
			throw new RuntimeException("스스로를 하위 조직으로 설정하실 수 없습니다.");
		}

		// 신규 깊이값 생성 및 설정
		organizationDTO.setDepthById(dtoId);
		int newDepth = organizationDAO.getPreviousDepthValue(organizationDTO);
		dtoId += "-" + newDepth;
		organizationDTO.updateDepthById(dtoId);

		// 저장
		organization.updateEntityByDTO(organizationDTO);
		organizationRepository.save(organization);
	}

	@Transactional
	public Map<String, Object> deleteOrganization (OrganizationDTO organizationDTO) throws Exception {
		List<Integer> deletedIdxList = new ArrayList<>();
		List<Integer> undeletedIdxList = new ArrayList<>();
		List<String> undeletedNameList = new ArrayList<>();

		// idx로 하위조직 포함 사용자 있는지 확인
		organizationDTO.getDeleteIdxList().forEach(deleteIdx -> {
			try {
				int orgMemberCount = organizationDAO.getOrganizationMember(deleteIdx, "Y").size();
				if (orgMemberCount > 0) {
					Organization organization = organizationRepository.findByOrganizationIdx(deleteIdx)
							.orElseThrow(() -> new RuntimeException("잘못된 조직정보입니다."));
					undeletedIdxList.add(deleteIdx);
					undeletedNameList.add(organization.getOrganizationName());
				}
				else {
					deletedIdxList.add(deleteIdx);
				}
			}
			catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});
		// 하위조직 포함 사용자 없는 idx만 미사용처리
		// 사용자 있는 idx는 클라이언트에 반환처리
		if (!deletedIdxList.isEmpty()) {
			organizationDTO.setDeleteIdxList(deletedIdxList);
			organizationDAO.deleteOrganization(organizationDTO);
		}

		Map<String, Object> returnData = new HashMap<>();
		returnData.put("undeletedIdxList", undeletedIdxList);
		returnData.put("undeletedNameList", undeletedNameList);
		return returnData;
	}

	public List<Map<String, Object>> getOrganizationMember (int organizationIdx, String includeAllYn, String findAllYn) throws Exception {
		Organization organization = organizationRepository.findByOrganizationIdx(organizationIdx)
				.orElseThrow(() -> new RuntimeException("잘못된 조직 정보입니다."));
		List<Position> positionList = positionRepository.findAll();
		List<Member> memberList = new ArrayList<>();
		List<Map<String, Object>> returnData = new ArrayList<>();

		if (findAllYn.equals("Y")) {
			memberList = memberRepository.findByOrganizationIdx(organizationIdx);
		}
		else {
			memberList = memberRepository.findByOrganizationIdxAndDelYn(organizationIdx, "N");
		}

		memberList.forEach(member -> {
			String positionName = positionList.stream()
					.filter(pos -> pos.getPositionIdx() == member.getPositionIdx())
					.findFirst().map(Position::getPositionName).orElse(null);

			Map<String, Object> dto = new HashMap<>();
			dto.put("memberId", member.getMemberId());
			dto.put("memberNm", member.getMemberNm());
			dto.put("organizationName", organization.getOrganizationName());
			dto.put("positionIdx", member.getPositionIdx());
			dto.put("positionName", positionName);

			returnData.add(dto);
		});

		return returnData;
	}


}
