package kr.co.triphos.position.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
public class PositionDTO {
	private Integer	positionIdx;
	private String	positionName;
	private String  useYn;
	private String 	insId;
	private LocalDateTime insDt;
	private String 	updId;
	private LocalDateTime updDt;

	private List<Integer> deleteIdxList;
}
