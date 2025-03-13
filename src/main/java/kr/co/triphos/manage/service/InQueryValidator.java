package kr.co.triphos.manage.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InQueryValidator {

    private final JSONParser jsonParser = new JSONParser();

    public boolean isValid(InQueryDTO dto) {
        return isValidNamespace(dto.getNamespace()) &&
                isValidTag(dto.getTag()) &&
                isValidSortIndex(dto.getSortIndex()) &&
                isValidSortType(dto.getSortType());
    }

    private boolean isValidNamespace(String namespace) {
        return namespace != null && namespace.matches("^[0-9A-Za-z.]+?$");
    }

    private boolean isValidTag(String tag) {
        return tag != null && tag.matches("^[0-9A-Za-z_]+?$");
    }

    private boolean isValidSortIndex(String sortIndex) {
        return sortIndex == null || sortIndex.matches("^[A-Za-z0-9_,. ]+$");
    }

    private boolean isValidSortType(String sortType) {
        return sortType == null || sortType.matches("^(asc|desc)$");
    }

    public Map<String, Object> parseParams(String params) {
        try {
            JSONObject temp = (JSONObject) jsonParser.parse(params);
            return (Map<String, Object>) temp;
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("파라미터 파싱 실패");
        }
    }
}
