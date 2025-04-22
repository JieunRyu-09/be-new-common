package kr.co.triphos.position.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.triphos.organization.entity.Organization;
import kr.co.triphos.position.entity.Position;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PositionInfoDTO {
	private Integer positionIdx;
	private String 	positionName;
	private String	useYn;

	public void createPositionInfo(Position position) {
		this.positionIdx 	= position.getPositionIdx();
		this.positionName 	= position.getPositionName();
		this.useYn 			= position.getUseYn();
	}
}
