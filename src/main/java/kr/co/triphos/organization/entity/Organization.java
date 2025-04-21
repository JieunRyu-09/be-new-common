package kr.co.triphos.organization.entity;

import kr.co.triphos.member.dto.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.STATIC;

@Data
@Entity(name = "Organization")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Table(name = "sy_organization_info")
public class Organization {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer	organizationIdx;

	@Column(name = "DEPTH_1")
	private Integer depth1;
	@Column(name = "DEPTH_2")
	private Integer depth2;
	@Column(name = "DEPTH_3")
	private Integer depth3;
	@Column(name = "DEPTH_4")
	private Integer depth4;
	@Column(name = "DEPTH_5")
	private Integer depth5;
	private int		level;
	private String 	organizationName;
	private String 	useYn;
	private String 	insId;
	private LocalDateTime insDt;
	private String 	updId;
	private LocalDateTime updDt;
}
