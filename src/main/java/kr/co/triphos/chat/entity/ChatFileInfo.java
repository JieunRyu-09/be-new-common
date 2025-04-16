package kr.co.triphos.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Table(name = "ch_chat_file_info")
public class ChatFileInfo {
	@Id
	@Nullable
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer			fileIdx;
	private Integer			roomIdx;
	private Integer			msgIdx;
	private String 			fileType;
	private String 			filePath;
	private String 			fileNm;
	private String 			realFileNm;
	private long  			fileSize;
	private LocalDateTime 	insDt;
	private String 			insMemberId;
}
