package kr.co.triphos.chat.service;

import kr.co.triphos.chat.dao.ChatDAO;
import kr.co.triphos.chat.dto.ChatMessageDTO;
import kr.co.triphos.chat.dto.ChatRoomDTO;
import kr.co.triphos.chat.dto.ChatRoomInfoDTO;
import kr.co.triphos.chat.dto.ChatRoomMemberDTO;
import kr.co.triphos.chat.entity.ChatFileInfo;
import kr.co.triphos.chat.entity.ChatRoom;
import kr.co.triphos.chat.entity.ChatRoomMember;
import kr.co.triphos.chat.repository.ChatFileInfoRepository;
import kr.co.triphos.chat.repository.ChatRoomMemberRepository;
import kr.co.triphos.chat.repository.ChatRoomRepository;
import kr.co.triphos.common.entity.FileInfo;
import kr.co.triphos.common.service.AuthenticationFacadeService;
import kr.co.triphos.member.entity.Member;
import kr.co.triphos.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChatService {
    @Value("${chat.upload-dir}")
    private String rootPath;

    private final ChatDAO chatDAO;

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatFileInfoRepository chatFileInfoRepository;

    private final ChatWebSocketService chatWebSocketService;
    private final AuthenticationFacadeService authenticationFacadeService;


    @Transactional
    public boolean createChatRoom(ChatRoomDTO chatRoomDTO, String memberId) throws Exception {
        LocalDateTime nowDate = LocalDateTime.now();

        // 채팅방 생성
        int memberCnt = chatRoomDTO.getInviteMemberIdList().size() + 1;
        ChatRoom chatRoom = ChatRoom.createChatRoom()
                .memberId(memberId)
                .title(chatRoomDTO.getTitle())
                .memberCnt(memberCnt)
                .build();
        chatRoomRepository.save(chatRoom);
        chatRoomRepository.flush();

        // 채팅방에 사람 추가
        List<ChatRoomMember> chatRoomMemberList = new ArrayList<>();
        int roomIdx = chatRoom.getRoomIdx();
        // 생성자 추가
        ChatRoomMember chatRoomCreator = ChatRoomMember.createChatRoomMember()
                .roomIdx(roomIdx)
                .memberId(memberId)
                .inviteId(memberId)
                .build();
        chatRoomMemberList.add(chatRoomCreator);
        // 초대받은 사람들 추가
        chatRoomDTO.getInviteMemberIdList().forEach(chatRoomMemberId -> {
            ChatRoomMember chatRoomMember = ChatRoomMember.createChatRoomMember()
                    .roomIdx(roomIdx)
                    .memberId(chatRoomMemberId)
                    .inviteId(memberId)
                    .build();
            chatRoomMemberList.add(chatRoomMember);
        });
        chatRoomMemberRepository.saveAll(chatRoomMemberList);
        return true;
    }

    @Transactional
    public boolean updateChatRoom(ChatRoomDTO chatRoomDTO, String memberId) throws Exception {
        ChatRoom chatRoom = chatRoomRepository.findByRoomIdx(chatRoomDTO.getRoomIdx())
                .orElseThrow(() -> new RuntimeException("채팅방 정보를 다시 확인하여주십시오."));
        chatRoom.setTitle(chatRoomDTO.getTitle());
        chatRoom.setUpdId(memberId);
        chatRoom.setUpdDt(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);
        return true;
    }

    @Transactional
    public boolean inviteMember(ChatRoomMemberDTO chatRoomMemberDTO, String inviteId) throws Exception{
        List<ChatRoomMember> chatRoomMemberList = new ArrayList<>();
        LocalDateTime nowDate = LocalDateTime.now();
        int roomIdx = chatRoomMemberDTO.getRoomIdx();
        int newMemberCnt = chatRoomMemberDTO.getInviteMemberIdList().size();

        // 채팅방 인원수 증가
        ChatRoom chatRoom = chatRoomRepository.findByRoomIdx(roomIdx)
                .orElseThrow(() -> new RuntimeException("채팅방 정보를 다시 확인하여주십시오."));
        int memberCnt = chatRoom.getMemberCnt();
        chatRoom.setMemberCnt(memberCnt + newMemberCnt);
        chatRoomRepository.save(chatRoom);

        // 채팅방에 인원 추가
        chatRoomMemberDTO.getInviteMemberIdList().forEach(memberId -> {
            ChatRoomMember chatRoomMember = ChatRoomMember.createChatRoomMember()
                    .roomIdx(roomIdx)
                    .memberId(memberId)
                    .inviteId(inviteId)
                    .build();
            chatRoomMemberList.add(chatRoomMember);
        });
        chatRoomMemberRepository.saveAll(chatRoomMemberList);
        return true;
    }

    public LinkedList<Map<String, String>> getInvitableMember(Integer roomIdx) throws Exception{
        LinkedList<Map<String, String>> invitableMemberList = new LinkedList<>();
        List<Member> memberList = memberRepository.findByDelYnOrderByMemberNmAsc("N");
        List<ChatRoomMember> chatRoomMemberList = chatRoomMemberRepository.findByPkRoomIdx(roomIdx);

        if (roomIdx == null) {
            // 방번호 없는경우, 최초 생성인 경우 모든 사용자 초대가능
            memberList.forEach(member -> {
                Map<String, String> memberInfo = new HashMap<>();
                memberInfo.put("memberId", member.getMemberId());
                memberInfo.put("memberNm", member.getMemberNm());
                invitableMemberList.push(memberInfo);
            });
        }
        else {
            // 방번호가 없는 경우 이미 초대된 사용자 제외하고 보여주기
            List<String> existMemberList = new ArrayList<>();
            // 이미 초대된 사용자 목록
            chatRoomMemberList.forEach(existMember -> {
                existMemberList.add(existMember.getPk().getMemberId());
            });
            // 이미 초대된 사용자는 제외하고 나머지를 초대가능한 사용자 목록에 추가
            memberList.forEach(member -> {
                String memberId = member.getMemberId();
                String memberNm = member.getMemberNm();
                if (!existMemberList.contains(memberId)) {
                    Map<String, String> memberInfo = new HashMap<>();
                    memberInfo.put("memberId", memberId);
                    memberInfo.put("memberNm", memberNm);
                    invitableMemberList.push(memberInfo);
                }
            });
        }
        return invitableMemberList;
    }

    public List<ChatRoomInfoDTO> getChatRoomList(String memberId) throws Exception {
		return chatDAO.getChatRoomList(memberId);
    }

    public List<ChatMessageDTO> getChatMessages(int roomIdx, int page) throws Exception {
        int pageSize = 20;
        int startIdx = pageSize * (page - 1);

        return chatDAO.getChatMessages(roomIdx, startIdx, pageSize);
    }

    @Transactional
    public boolean chatFilesSave (int roomIdx, List<MultipartFile> fileList, String memberId) throws Exception {
        /**
         * 채팅 메세지 데이터 생성(broadcasting X) => msgIdx얻기 위함
         * 파일 저장
         * 파일 저장 성공 시 파일정보를 msgIdx와 함께 db에 저장
         * 이후 추가적인 broadcasting 로직 실행
         */
        // 채팅 메세지 데이터 생성, chatMsgIdx를 얻기 위함
        String memberNm = authenticationFacadeService.getMemberNm();
        LocalDateTime nowDate = LocalDateTime.now();

        // 파일 개별 저장
        fileList.forEach(fileItem -> {
            try {
                String contentType = fileItem.getContentType();
                // 채팅 데이터 취득
                Map<String, Object> chatDataMap = chatWebSocketService.receiveFilesMessage(memberId, roomIdx, contentType, "N");
                int msgIdx = Integer.parseInt(chatDataMap.get("msgIdx").toString());
                String messageType = chatDataMap.get("messageType").toString();
                String content = chatDataMap.get("content").toString();
                ChatRoomInfoDTO chatRoomInfoDTO = (ChatRoomInfoDTO) chatDataMap.get("chatRoomInfoDTO");

                // 파일 데이터 취득
                Map<String, Object> fileDataMap = saveFile(roomIdx, msgIdx, memberId, nowDate, fileItem);
                String filePath = fileDataMap.get("filePath").toString().replace("\\", "/");
                String realFileNm = fileDataMap.get("realFileNm").toString();
                int fileIdx = Integer.parseInt(fileDataMap.get("fileIdx").toString());

                // 채팅메세지 객체 생성
                ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
                        .fileIdx(Collections.singletonList(fileIdx))
                        .fileUrl(Collections.singletonList(filePath.toString()))
                        .fileName(Collections.singletonList(realFileNm))
                        .type(ChatMessageDTO.MessageType.valueOf(messageType))
                        .content(content)
                        .memberId(memberId)
                        .memberNm(memberNm)
                        .sendTime(LocalDateTime.now())
                        .build();

                chatWebSocketService.sendToChannel("/topic/chat/" + roomIdx, chatMessageDTO);
                chatWebSocketService.updateChatRoomMemberUnreadCount(roomIdx, chatRoomInfoDTO);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return true;
    }

    @Transactional
    public boolean chatBundleFilesSave (int roomIdx, List<MultipartFile> fileList, String memberId) throws Exception {
        /**
         * 채팅 메세지 데이터 생성(broadcasting X) => msgIdx얻기 위함
         * 파일 저장
         * 파일 저장 성공 시 파일정보를 msgIdx와 함께 db에 저장
         * 이후 추가적인 broadcasting 로직 실행
         */
        // 채팅 메세지 데이터 생성, chatMsgIdx를 얻기 위함
        String memberNm = authenticationFacadeService.getMemberNm();
        LocalDateTime nowDate = LocalDateTime.now();

        // 채팅 데이터 취득
        String contentType = "image/";
        Map<String, Object> chatDataMap = chatWebSocketService.receiveFilesMessage(memberId, roomIdx, contentType, "Y");
        int msgIdx = Integer.parseInt(chatDataMap.get("msgIdx").toString());
        String messageType = chatDataMap.get("messageType").toString();
        String content = chatDataMap.get("content").toString();
        ChatRoomInfoDTO chatRoomInfoDTO = (ChatRoomInfoDTO) chatDataMap.get("chatRoomInfoDTO");

        List<Integer> fileIdxList = new ArrayList<>();
        List<String> filePathList = new ArrayList<>();
        List<String> fileNameList = new ArrayList<>();

        // 파일 개별 저장
        fileList.forEach(fileItem -> {
            try {
                // 파일 데이터 취득
                Map<String, Object> fileDataMap = saveFile(roomIdx, msgIdx, memberId, nowDate, fileItem);
                String filePath = fileDataMap.get("filePath").toString().replace("\\", "/");
                String realFileNm = fileDataMap.get("realFileNm").toString();
                int fileIdx = Integer.parseInt(fileDataMap.get("fileIdx").toString());

                fileIdxList.add(fileIdx);
                filePathList.add(filePath);
                fileNameList.add(realFileNm);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        // 전송할 메세지 데이터 생성
        // 채팅메세지 객체 생성
        ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
                .fileIdx(fileIdxList)
                .fileUrl(filePathList)
                .fileName(fileNameList)
                .type(ChatMessageDTO.MessageType.valueOf(messageType))
                .content(content)
                .memberId(memberId)
                .memberNm(memberNm)
                .sendTime(LocalDateTime.now())
                .build();
        chatWebSocketService.sendToChannel("/topic/chat/" + roomIdx, chatMessageDTO);
        chatWebSocketService.updateChatRoomMemberUnreadCount(roomIdx, chatRoomInfoDTO);
        return true;
    }

    public Map<String, Object> saveFile(int roomIdx, int msgIdx, String memberId, LocalDateTime nowDate, MultipartFile fileItem) throws IOException {
        // 파일정보
        String yearMonth 	= nowDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String contentType  = fileItem.getContentType();
        String fileType     = contentType.startsWith("image/") ? "IMG" : "FILE";
        long fileSize       = fileItem.getSize();
        String realFileNm   = fileItem.getOriginalFilename();
        if (realFileNm == null) throw new RuntimeException("잘못된 파일정보입니다.");

        // FileInfo Entity 생성
        ChatFileInfo chatFileInfo = ChatFileInfo.builder()
                .roomIdx(roomIdx)
                .msgIdx(msgIdx)
                .fileType(fileType)
                .filePath("temp")
                .fileNm("temp")
                .realFileNm(realFileNm)
                .fileSize(fileSize)
                .insDt(nowDate)
                .insMemberId(memberId)
                .build();
        // FileInfo Entity 저장 및 저장하면서 생긴 IDX 조회
        // 조회한 IDX로 추후 파일명 등 설정
        chatFileInfoRepository.save(chatFileInfo);
        chatFileInfoRepository.flush();
        Integer idx = chatFileInfo.getFileIdx();
        if (idx == null) throw new RuntimeException("파일 저장 실패.");

        // 확장자, 파일경로 등 파일정보 설정
        // 경로 체크 후 없으면 경로까지 생성
        String fileExtender = realFileNm.substring(realFileNm.lastIndexOf("."));
        String fileName = yearMonth + "_" + idx + fileExtender;
        String shortPath = "/" + yearMonth + "/";
        Path filePath = Paths.get(rootPath + shortPath + fileName);
        Path existPath = Paths.get(rootPath + shortPath);
        if (!Files.exists(existPath)) Files.createDirectories(existPath);
        // 파일 저장(출력)
        fileItem.transferTo(filePath.toFile());
        // 파일 경로 및 파일명 정보 저장
        chatFileInfo.setFileNm(fileName);
        chatFileInfo.setFilePath(existPath.toString());
        chatFileInfoRepository.save(chatFileInfo);
        chatFileInfoRepository.flush();

        Map<String, Object> returnData = new HashMap<>();
        returnData.put("fileIdx", chatFileInfo.getFileIdx());
        returnData.put("fileType", fileType);
        returnData.put("filePath", filePath);
        returnData.put("realFileNm", realFileNm);

        return returnData;
    }
}
