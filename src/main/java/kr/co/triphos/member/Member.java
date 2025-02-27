package kr.co.triphos.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Collection;
import java.util.Collections;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Table(name = "member")
public class Member {
	@Id
	private String memberId;
	private String memberPw;
	private String memberNm;
	private String memberEmail;
	private String memberPhone;
	private String authority;

}
