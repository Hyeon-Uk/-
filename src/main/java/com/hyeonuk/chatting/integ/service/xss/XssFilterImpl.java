package com.hyeonuk.chatting.integ.service.xss;

import org.springframework.stereotype.Component;

@Component
public class XssFilterImpl implements XssFilter{
    @Override
    public String filter(String text){
        if(text == null) return null;

        String encoded = text.replaceAll("<","&lt;").replaceAll(">","&gt;");
        return encoded;
    }
}
