package kr.co.triphos.member.service;

import kr.co.triphos.config.JwtUtil;
import kr.co.triphos.member.entity.Member;
import kr.co.triphos.common.entity.MenuInfo;
import kr.co.triphos.member.entity.MenuMemberAuth;
import kr.co.triphos.member.repository.MemberRepository;
import kr.co.triphos.common.repository.MenuInfoRepository;
import kr.co.triphos.member.repository.MenuMemberAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {
    private final JwtUtil               jwtUtil;
    private final PasswordEncoder       passwordEncoder;

    private final MemberRepository          memberRepository;
    private final MenuMemberAuthRepository  menuMemberAuthRepository;
    private final MenuInfoRepository        menuInfoRepository;

    /**
     * login
     * @param memberId
     * @param memberPw
     * @return String JWT token
     */
    public HashMap<String, String> login(String memberId, String memberPw) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("ID, 비밀번호를 다시 확인해 주세요."));

        if (!member.isEnabled()) throw new RuntimeException("미사용처리된 계정입니다.");
        if (member.getDelYn().equals("Y")) throw new RuntimeException("미사용처리된 계정입니다.");

        // 비밀번호 확인 후 JWT 토큰 생성
        if (passwordEncoder.matches(memberPw, member.getMemberPw())) {
            return jwtUtil.generateAccessToken(memberId);
        }
        else {
            throw new RuntimeException("ID, 비밀번호를 다시 확인해 주세요.");
        }
    }

    public String getRefreshToken(String accessToken) {
        return jwtUtil.generateRefreshToken(jwtUtil.extractMemberId(accessToken));
    }

    public  HashMap<String, String> refreshAccessToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid Refresh Token");
        }

        String username = jwtUtil.extractMemberId(refreshToken);
        return jwtUtil.generateAccessToken(username);
    }

    public String getMemberIdByRefreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid Refresh Token");
        }

        return jwtUtil.extractMemberId(refreshToken);
    }

    public List<HashMap<String, Object>> getMemberMenuList(String id) {
        List<HashMap<String, Object>> resultList = new ArrayList<>();
        List<MenuInfo> menuList = menuInfoRepository.findByDisplayYn("Y");
        List<MenuMemberAuth> authList = menuMemberAuthRepository.findByPkMemberIdAndUseYn(id, "Y");

        menuList.forEach(menuItem -> {
            String useYn = authList.stream().filter(authItem ->
                    menuItem.getMenuId().equals(authItem.getPk().getMenuId()))
                    .findFirst().map(MenuMemberAuth::getUseYn)
                    .orElse("N");

            if (useYn.equals("Y")) {
                HashMap<String, Object> resultItem = new HashMap<>();
                resultItem.put("mainCd",    menuItem.getPk().getMainCd());
                resultItem.put("sub1Cd",    menuItem.getPk().getSub1Cd());
                resultItem.put("sub2Cd",    menuItem.getPk().getSub2Cd());
                resultItem.put("orderBy",   menuItem.getOrderBy());
                resultItem.put("menuId",    menuItem.getMenuId());
                resultItem.put("menuTitle", menuItem.getMenuTitle());
                resultItem.put("menuLink",  menuItem.getMenuLink());
                resultItem.put("displayYn", menuItem.getDisplayYn());
                resultList.add(resultItem);
            }
        });

        return resultList;
    }

}
