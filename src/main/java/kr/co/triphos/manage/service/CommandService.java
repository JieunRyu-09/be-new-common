package kr.co.triphos.manage.service;

import kr.co.triphos.common.service.JSON;
import kr.co.triphos.manage.dao.CommandDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class CommandService {

    private final CommandDAO commandDAO;

    public List<JSON> select(String namespace, String tag, Map<String, Object> parameters) {
        return commandDAO.selectRows(namespace, tag, parameters);
    }

    public int selectCount(String namespace, String tag, Map<String, Object> parameters) {
        return (int) commandDAO.selectOne(namespace + "." + tag, parameters);
    }

}
