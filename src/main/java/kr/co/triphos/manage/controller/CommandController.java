package kr.co.triphos.manage.controller;

import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.common.service.AuthenticationFacadeService;
import kr.co.triphos.common.service.CommonFunc;
import kr.co.triphos.common.service.JSON;
import kr.co.triphos.manage.service.CommandService;
import kr.co.triphos.manage.service.InQueryDTO;
import kr.co.triphos.manage.service.InQueryValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/command")
@RequiredArgsConstructor
@Log4j2
public class CommandController {
    private final InQueryValidator validator;
    private final CommandService commandService;
    private final AuthenticationFacadeService authFacadeService;

    @PostMapping("/{method}/inQuery.do")
    public ResponseEntity<?> inQuery(@PathVariable String method, @RequestBody InQueryDTO inQuery) {
        ResponseDTO responseDTO = new ResponseDTO();

        if (!CommonFunc.isVal(method, "^(select|page)$")) {
            method = "select";
        }

        if (!validator.isValid(inQuery)) {
            return ResponseEntity.badRequest().body("유효하지 않은 요청입니다.");
        }

        try {
            Map<String, Object> parameters = inQuery.getParam();

            // 예약어
            // ------------------------------
            parameters.put("_limit", inQuery.getPagePerSize());
            parameters.put("_page", (inQuery.getViewPageNo() - 1) * inQuery.getPagePerSize());
            parameters.put("_sort", inQuery.getSortIndex() + " " + inQuery.getSortType());
            parameters.put("_memberId_", authFacadeService.getMemberId());

            // TODO 사용자별 권한
            parameters.put("_menuAuth_view_", null);

//            if (menuAuthVO == null) {
//                parameters.put("_menuAuth_view_", null);
//            }
//            else {
//                parameters.put("_menuAuth_view_", menuAuthVO.getSearchType());
//            }
            // ------------------------------

            List<JSON> results = commandService.select(inQuery.getNamespace(), inQuery.getTag(), parameters);

            responseDTO.setSuccess(true);
            responseDTO.addData("rows", results);
            responseDTO.addData("userData", results);

            int recordTotalCount = results.size();

            if (method.equals("page")) {
                recordTotalCount = commandService.selectCount(inQuery.getNamespace(), inQuery.getTag() + "Count", parameters);
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
