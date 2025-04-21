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
	private String 	organizationKey;
	private int		level;
	private String 	organizationName;
	private String 	useYn;
	private String 	insId;
	private LocalDateTime insDt;
	private String 	updId;
	private LocalDateTime updDt;
}
