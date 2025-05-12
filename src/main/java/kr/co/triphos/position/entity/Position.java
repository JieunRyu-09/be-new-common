package kr.co.triphos.position.entity;

import jakarta.persistence.*;
import kr.co.triphos.position.dto.PositionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

@Data
@Entity(name = "Position")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Table(name = "sy_position_info")
public class Position {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer	positionIdx;
	private String	positionName;
	private String  useYn;
	private String 	insId;
	private LocalDateTime insDt;
	private String 	updId;
	private LocalDateTime updDt;

	public static Position createEntityByDTO(PositionDTO dto) {
		return Position.builder()
				.positionIdx(dto.getPositionIdx())
				.positionName(dto.getPositionName())
				.useYn("Y")
				.insId(dto.getInsId())
				.insDt(LocalDateTime.now())
				.build();
	}

	public void updateEntityByDTO(PositionDTO dto) {
		this.positionName = dto.getPositionName();
		this.updId = dto.getUpdId();
		this.useYn = dto.getUseYn();
		this.updDt = LocalDateTime.now();
	}
}
