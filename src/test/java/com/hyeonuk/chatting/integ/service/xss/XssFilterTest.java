package com.hyeonuk.chatting.integ.service.xss;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class XssFilterTest {
    XssFilter xssFilter = new XssFilterImpl();

    @Test
    public void xssFilterSuccess(){
        String text = "title !!!! <script>alert('helloworld');</script>";
        String expected = "title !!!! &lt;script&gt;alert('helloworld');&lt;/script&gt;";
        assertThat(xssFilter.filter(text)).isEqualTo(expected);
    }

    @Test
    public void nullTest(){
        String text = null;
        assertThat(xssFilter.filter(text)).isNull();
    }
}