package kr.co.triphos.common.entity;

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
@Table(name = "sy_file_info")
public class FileInfo {
	@Id
	@Nullable
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer			fileIdx;
	private String 			fileGroup;
	private String 			fileType;
	private String 			filePath;
	private String 			fileNm;
	private String 			realFileNm;
	private long  			fileSize;
	private LocalDateTime 	insDt;
	private String 			insMember;
}
