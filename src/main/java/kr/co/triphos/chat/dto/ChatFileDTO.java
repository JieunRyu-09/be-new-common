package kr.co.triphos.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor  // 기본 생성자 추가 (롬복 사용 시)
@AllArgsConstructor
@Builder
public class ChatFileDTO {
	private int 	fileIdx;
	private String 	fileUrl;
	private String 	fileName;
}
