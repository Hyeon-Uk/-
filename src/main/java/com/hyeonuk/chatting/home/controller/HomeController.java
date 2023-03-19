package com.hyeonuk.chatting.home.controller;

import com.hyeonuk.chatting.member.dto.MemberDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@Slf4j
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public String home(@SessionAttribute(value = "member",required = true)MemberDto member, Model model){
        model.addAttribute("member",member);
        return "home";
    }
}
