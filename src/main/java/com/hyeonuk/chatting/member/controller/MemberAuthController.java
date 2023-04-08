package com.hyeonuk.chatting.member.controller;

import com.hyeonuk.chatting.member.dto.auth.JoinDto;
import com.hyeonuk.chatting.member.dto.auth.LoginDto;
import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.exception.auth.join.AlreadyExistException;
import com.hyeonuk.chatting.member.exception.auth.join.PasswordNotMatchException;
import com.hyeonuk.chatting.member.exception.auth.login.UserNotFoundException;
import com.hyeonuk.chatting.member.exception.auth.login.RestrictionException;
import com.hyeonuk.chatting.member.service.auth.MemberAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class MemberAuthController {
    private final MemberAuthService authService;

    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("dto", new JoinDto());
        return "auth/join";
    }

    @PostMapping("/join")
    public String joinProc(@Validated @ModelAttribute("dto") JoinDto dto, BindingResult bindingResult) {
        try {
            authService.save(dto);
        } catch (IllegalArgumentException | AlreadyExistException exception) {
            bindingResult.addError(new ObjectError("dto", exception.getMessage()));
            return "auth/join";
        }

        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("dto", new LoginDto());
        return "auth/login";
    }

    @PostMapping("/login")
    public String loginProc(@Validated @ModelAttribute("dto") LoginDto dto,
                            BindingResult bindingResult,
                            HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        MemberDto loginMember = null;
        try {
            loginMember = authService.login(dto);
        } catch (UserNotFoundException | RestrictionException exception) {
            bindingResult.addError(new ObjectError("dto", exception.getMessage()));
            return "auth/login";
        }

        session.setAttribute("member", loginMember);

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
