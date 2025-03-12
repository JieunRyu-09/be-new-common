package kr.co.triphos.member.service;

import kr.co.triphos.config.JwtUtil;
import kr.co.triphos.member.entity.MemberEntity;
import kr.co.triphos.member.entity.MenuInfoEntity;
import kr.co.triphos.member.entity.MenuMemberAuthEntity;
import kr.co.triphos.member.repository.MemberRepository;
import kr.co.triphos.member.repository.MenuInfoRepository;
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
    public String login(String memberId, String memberPw) {
        MemberEntity memberEntity = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("ID, 비밀번호를 다시 확인해 주세요."));

        if (!memberEntity.isEnabled()) throw new RuntimeException("미사용처리된 계정입니다.");

        // 비밀번호 확인 후 JWT 토큰 생성
        if (passwordEncoder.matches(memberPw, memberEntity.getMemberPw())) {
            return jwtUtil.generateAccessToken(memberId);
        }
        else {
            throw new RuntimeException("ID, 비밀번호를 다시 확인해 주세요.");
        }
    }

    public String getRefreshToken(String accessToken) {
        return jwtUtil.generateRefreshToken(jwtUtil.extractMemberId(accessToken));
    }

    public String refreshAccessToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid Refresh Token");
        }

        String username = jwtUtil.extractMemberId(refreshToken);
        return jwtUtil.generateAccessToken(username);
    }

    public List<HashMap<String, Object>> getMemberMenuList(String id) {
        List<HashMap<String, Object>> resultList = new ArrayList<>();
        List<MenuInfoEntity> menuList = menuInfoRepository.findByDisplayYn("Y");
        List<MenuMemberAuthEntity> authList = menuMemberAuthRepository.findByPkMemberIdAndUseYn(id, "Y");

        menuList.forEach(menuItem -> {
            String useYn = authList.stream().filter(authItem ->
                    menuItem.getMenuId().equals(authItem.getPk().getMenuId()))
                    .findFirst().map(MenuMemberAuthEntity::getUseYn)
                    .orElse("N");

            if (useYn.equals("Y")) {
                HashMap<String, Object> resultItem = new HashMap<>();
                resultItem.put("mainCd",    menuItem.getPk().getMainCd());
                resultItem.put("sub1Cd",    menuItem.getPk().getMainCd());
                resultItem.put("sub2Cd",    menuItem.getPk().getMainCd());
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
