package com.hyeonuk.chatting.integ.service.xss;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class XssFilterTest {
    XssFilterService xssFilterService = new XssFilterServiceImpl();

    @Test
    public void xssFilterSuccess(){
        String text = "title !!!! <script>alert('helloworld');</script>";
        String expected = "title !!!! &lt;script&gt;alert('helloworld');&lt;/script&gt;";
        assertThat(xssFilterService.filter(text)).isEqualTo(expected);
    }

    @Test
    public void nullTest(){
        String text = null;
        assertThat(xssFilterService.filter(text)).isNull();
    }
}