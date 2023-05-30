package com.hyeonuk.chatting.integ.service.xss;

import org.springframework.stereotype.Component;

@Component
public class XssFilterServiceImpl implements XssFilterService {
    @Override
    public String filter(String text) {
        if (text != null) {
            text = text.replaceAll("\\<", "&lt;").replaceAll("\\>", "&gt;").replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        }
        return text;
    }
}
