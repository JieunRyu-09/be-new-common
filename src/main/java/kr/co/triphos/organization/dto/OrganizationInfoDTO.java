package kr.co.triphos.organization.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.triphos.organization.entity.Organization;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrganizationInfoDTO {
	private Integer organizationIdx;
	private String organizationId;        // 조직 ID (ex: "1-2-1")
	private String organizationName;      // 조직명
	private int level;                    // 조직 단계

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<OrganizationInfoDTO> children = new ArrayList<>();

	@JsonIgnore
	private String parentKey;
	@JsonIgnore
	private String selfKey;

	public OrganizationInfoDTO(Organization entity) {
		this.organizationIdx	= entity.getOrganizationIdx();
		this.organizationName 	= entity.getOrganizationName();
		this.level 				= entity.getLevel();
		this.selfKey 			= buildKey(entity.getDepth1(), entity.getDepth2(), entity.getDepth3(), entity.getDepth4(), entity.getDepth5());
		this.parentKey 			= buildParentKey(entity);
		this.organizationId 	= this.selfKey;
	}

	private String buildKey(Integer d1, Integer d2, Integer d3, Integer d4, Integer d5) {
		StringBuilder sb = new StringBuilder();
		if (d1 != null) sb.append(d1);
		if (d2 != null) sb.append("-").append(d2);
		if (d3 != null) sb.append("-").append(d3);
		if (d4 != null) sb.append("-").append(d4);
		if (d5 != null) sb.append("-").append(d5);
		return sb.toString();
	}

	private String buildParentKey(Organization entity) {
		int level = entity.getLevel();
		if (level == 0) {
			return null;
		} else if (level == 1) {
			return buildKey(entity.getDepth1(), null, null, null, null);
		} else if (level == 2) {
			return buildKey(entity.getDepth1(), entity.getDepth2(), null, null, null);
		} else if (level == 3) {
			return buildKey(entity.getDepth1(), entity.getDepth2(), entity.getDepth3(), null, null);
		} else if (level == 4) {
			return buildKey(entity.getDepth1(), entity.getDepth2(), entity.getDepth3(), entity.getDepth4(), null);
		}
		return null;
	}
}
