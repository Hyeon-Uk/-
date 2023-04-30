package com.hyeonuk.chatting.board.dto;

import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Builder
@AllArgsConstructor
@Setter
@Getter
public class PageRequestDto {
    private int page;
    private int size;
    
    //default 생성자에서 default값 셋팅
    public PageRequestDto(){
        this.page=0;
        this.size=10;
    }

    public Pageable getPageable(Sort sort){
        return PageRequest.of(page,size,sort);
    }
}
