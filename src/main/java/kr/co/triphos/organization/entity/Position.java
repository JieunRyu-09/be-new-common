package kr.co.triphos.organization.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
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
	private Integer	positionId;
	private String	positionName;
	private String  useYn;
	private String 	insId;
	private LocalDateTime insDt;
	private String 	updId;
	private LocalDateTime updDt;
}
