package kr.co.triphos.development_standard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@RestController
class DevelopmentStandardApplicationTests {

	@Test
	void divisionTrueTest() {
		// 좌측 화살표 눌러서 테스트 진행
		int result = 10 / 2;
		assertEquals(5, result);
	}

	@Test
	void divisionFalseTest() {
		// 좌측 화살표 눌러서 테스트 진행
		int result = 10 / 2;
		assertNotEquals(5, result);
	}

}
