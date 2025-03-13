package kr.co.triphos.manage.controller;

import kr.co.triphos.common.service.CommonFunc;
import kr.co.triphos.common.service.ResponseDTO;
import kr.co.triphos.manage.service.CustomQueryExecutor;
import kr.co.triphos.manage.service.InQueryDTO;
import kr.co.triphos.manage.service.InQueryValidator;
import kr.co.triphos.member.dto.CustomUserDetailsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/command")
@RequiredArgsConstructor
@Log4j2
public class CommandController {
    private final CustomQueryExecutor queryExecutor;
    private final InQueryValidator validator;

    @PostMapping("/{method}/InJson.do")
    public ResponseEntity<?> InJson(@PathVariable String method, @RequestBody InQueryDTO inQuery, @AuthenticationPrincipal CustomUserDetailsDTO mi) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            if (!CommonFunc.isVal(method, "^(select|page)$")) {
                method = "select";
            }

            if (!validator.isValid(inQuery)) {
                return ResponseEntity.badRequest().body("유효하지 않은 요청입니다.");
            }
            boolean isPage = "page".equalsIgnoreCase(method);

            Map<String, Object> params = inQuery.getParam();
            // TODO 메뉴권한 체크 추가 필요
            params.put("_menuAuth_view_", null);

            if (params.containsKey("_memberId_")) {
                params.put("_memberId_", mi.getUsername());
            }

            List<?> rows;
            if (isPage) {
                params.put("_limit", inQuery.getPagePerSize());
                params.put("_page", (inQuery.getViewPageNo() - 1) * inQuery.getPagePerSize());
                rows = queryExecutor.executeDynamicQuery(method, inQuery);
            }
            else {
                rows = queryExecutor.executeQuery(inQuery.getNamespace(), inQuery.getTag(), params);
            }

            responseDTO.setSuccess(true);
            responseDTO.addData("rows", rows);
            responseDTO.addData("userData", rows);

            int recordTotalCount = rows.size();

            if (isPage) {
                params.remove("_sort");
                List<Integer> count = (List<Integer>) queryExecutor.executeQuery(inQuery.getNamespace(), inQuery.getTag() + "Count", params);
                if (count != null && count.size() > 0) {
                    recordTotalCount = count.get(0);
                }
            }

            responseDTO.addData("page", inQuery.getViewPageNo());
            responseDTO.addData("total", getPageTotalCount(recordTotalCount, inQuery.getPagePerSize()));
            responseDTO.addData("records", recordTotalCount);
        }
        catch (Exception ex) {
            log.error(CommonFunc.getExceptionMessage(ex));

            responseDTO.setSuccess(false);
            responseDTO.setMsg(ex.toString());
        }

        return ResponseEntity.ok().body(responseDTO);
    }

    private int getPageTotalCount(int recordTotalCount, int pagePerSize) {
        try {
            int pageTotalCount = (int) Math.floor(recordTotalCount / (float) pagePerSize);
            if ((recordTotalCount % pagePerSize) > 0) pageTotalCount = pageTotalCount + 1;
            return pageTotalCount;
        }
        catch (Exception ignored) {}

        return 0;
    }

}
