package kr.co.triphos.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
public class OrganizationDTO {
	private Integer	organizationIdx;
	private Integer depth1;
	private Integer depth2;
	private Integer depth3;
	private Integer depth4;
	private Integer depth5;
	private String 	organizationId;
	private int		level;
	private String 	organizationName;
	private String 	useYn;
	private String 	insId;
	private LocalDateTime insDt;
	private String 	updId;
	private LocalDateTime updDt;

	/** 초기에 ID가 미확정된 경우 */
	public void setDepthById(String organizationId) {
		this.organizationId = organizationId;
		String[] depthList = organizationId.split("-");
		int size = depthList.length;
		this.level = size;

		setDepth(depthList, size);
	}

	/** ID가 확정된 경우 */
	public void updateDepthById(String organizationId) {
		this.organizationId = organizationId;
		String[] depthList = organizationId.split("-");
		int size = depthList.length;
		this.level = size - 1;

		setDepth(depthList, size);
	}

	private void setDepth(String[] depthList, int size) {
		for (int i = 0; i < size; i++) {
			int depth = Integer.parseInt(depthList[i]);
			switch (i + 1) {
				case 1: this.depth1 = depth; break;
				case 2: this.depth2 = depth; break;
				case 3: this.depth3 = depth; break;
				case 4: this.depth4 = depth; break;
				case 5: this.depth5 = depth; break;
			}
		}
	}
}
