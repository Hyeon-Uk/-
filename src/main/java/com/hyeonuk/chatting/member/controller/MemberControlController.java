package com.hyeonuk.chatting.member.controller;

import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.dto.control.FriendAddDto;
import com.hyeonuk.chatting.member.dto.control.MemberSearchDto;
import com.hyeonuk.chatting.member.service.control.MemberControlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/member")
@Slf4j
@RequiredArgsConstructor
public class MemberControlController {
    private final MemberControlService memberControlService;

    /*
    *
    * 이름을 포함한 유저 찾기
    * JSON형태로 return
    * */
    @ResponseBody
    @GetMapping
    public ResponseEntity<List<MemberDto>> findMemberByNickname(@Validated MemberSearchDto dto) {
        return new ResponseEntity<>(memberControlService.findAllByNickname(dto),HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping
    public ResponseEntity<Boolean> addMember(@SessionAttribute("member")MemberDto member,@RequestBody FriendAddDto dto){
        memberControlService.addFriend(member,MemberDto.builder().id(dto.getId()).build());
        return new ResponseEntity<>(true,HttpStatus.OK);
    }
}
