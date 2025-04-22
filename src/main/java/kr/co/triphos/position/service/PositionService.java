package kr.co.triphos.position.service;

import kr.co.triphos.member.entity.Member;
import kr.co.triphos.member.repository.MemberRepository;
import kr.co.triphos.organization.dao.OrganizationDAO;
import kr.co.triphos.organization.dto.OrganizationDTO;
import kr.co.triphos.organization.dto.OrganizationInfoDTO;
import kr.co.triphos.organization.entity.Organization;
import kr.co.triphos.organization.repository.OrganizationRepository;
import kr.co.triphos.organization.service.OrganizationService;
import kr.co.triphos.position.dao.PositionDAO;
import kr.co.triphos.position.dto.PositionDTO;
import kr.co.triphos.position.dto.PositionInfoDTO;
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
public class PositionService {
	private final MemberRepository memberRepository;
	private final PositionRepository positionRepository;
	private final OrganizationRepository organizationRepository;

	private final PositionDAO positionDAO;
	private PositionDTO positionDTO;

	public List<PositionInfoDTO> getPosition(String findAllYn) throws Exception {
		List<PositionInfoDTO> positionInfoDTOList = new ArrayList<>();
		List<Position> positionList = new ArrayList<>();

		if (findAllYn.equals("Y")) {
			positionList = positionRepository.findAll();
		}
		else {
			positionList = positionRepository.findByUseYn("N");
		}
		positionList.forEach(position -> {
			PositionInfoDTO positionInfoDTO = new PositionInfoDTO();
			positionInfoDTO.createPositionInfo(position);
			positionInfoDTOList.add(positionInfoDTO);
		});
		return positionInfoDTOList;
	}

	@Transactional
	public void createPosition(PositionDTO positionDTO) throws Exception {
		Position existNamePosition = positionRepository.findByPositionName(positionDTO.getPositionName());

		if (existNamePosition != null) {
			throw new RuntimeException("동일한 이름의 직급이 이미 존재합니다.");
		}
		Position position = Position.createEntityByDTO(positionDTO);
		positionRepository.save(position);
	}

	@Transactional
	public void updatePosition(PositionDTO positionDTO) throws Exception {
		Position existNamePosition = positionRepository.findByPositionName(positionDTO.getPositionName());
		Position position = positionRepository.findByPositionIdx(positionDTO.getPositionIdx())
				.orElseThrow(() -> new RuntimeException("잘못된 직급정보입니다."));

		if (existNamePosition != null && !Objects.equals(existNamePosition.getPositionIdx(), position.getPositionIdx())) {
			throw new RuntimeException("동일한 이름의 직급이 이미 존재합니다.");
		}

		if ("N".equals(positionDTO.getUseYn())) {
			List<Member> memberList = memberRepository.findByPositionIdxAndDelYn(position.getPositionIdx(), "N");
			if (!memberList.isEmpty()) throw new RuntimeException("해당 직급의 사용자가 존재합니다.");
		}

		position.updateEntityByDTO(positionDTO);
		positionRepository.save(position);
	}

	@Transactional
	public Map<String, Object> deletePosition(PositionDTO positionDTO) throws Exception {
		List<Position> positionList = new ArrayList<>();
		List<Integer> undeletedIdxList = new ArrayList<>();
		List<String> undeletedNameList = new ArrayList<>();

		positionDTO.getDeleteIdxList().forEach(deleteIdx -> {
			Position position = positionRepository.findByPositionIdx(deleteIdx)
					.orElseThrow(() -> new RuntimeException("잘못된 직급정보입니다."));
			List<Member> memberList = memberRepository.findByPositionIdxAndDelYn(deleteIdx, "N");

			if (memberList.isEmpty()) {
				position.setUseYn("N");
				position.setUpdId(positionDTO.getUpdId());
				position.setUpdDt(LocalDateTime.now());
				positionList.add(position);
			}
			else {
				undeletedIdxList.add(deleteIdx);
				undeletedNameList.add(position.getPositionName());
			}
		});

		positionRepository.saveAll(positionList);
		Map<String, Object> returnData = new HashMap<>();
		returnData.put("undeletedIdxList", undeletedIdxList);
		returnData.put("undeletedNameList", undeletedNameList);
		return  returnData;
	}

	public List<Map<String, Object>> getPositionMember (int positionIdx, String findAllYn) throws Exception {
		List<Map<String, Object>> memberList = new ArrayList<>();
		List<Organization> organizationList = organizationRepository.findAll();
		List<Member> memberInfoList = new ArrayList<>();

		if (findAllYn.equals("Y")) {
			memberInfoList = memberRepository.findByPositionIdx(positionIdx);
		}
		else {
			memberInfoList = memberRepository.findByPositionIdxAndDelYn(positionIdx, "N");
		}

		memberInfoList.forEach(member -> {
			Organization organization = organizationList.stream()
					.filter(org -> org.getOrganizationIdx() == 5)
					.findFirst().orElse(null);

			Map<String, Object> dto = new HashMap<>();
			dto.put("memberId", member.getMemberId());
			dto.put("memberNm", member.getMemberNm());
			dto.put("organizationIdx", member.getOrganizationIdx());
			if (organization != null) {
				dto.put("organizationId", organization.getOrganizationId());
				dto.put("organizationName", organization.getOrganizationName());
			}
			memberList.add(dto);
		});
		return memberList;
	}

}
