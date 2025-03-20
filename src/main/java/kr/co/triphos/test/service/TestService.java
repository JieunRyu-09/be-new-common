package kr.co.triphos.test.service;

import kr.co.triphos.test.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class TestService {
	private final TestRepository testRepository;


	public boolean testDBTimeout() throws Exception {
		testRepository.testDBTimeout();
		return true;
	}
}
