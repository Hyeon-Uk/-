package com.hyeonuk.chatting.member.dto.control;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSearchDto {
    @NotBlank(message="검색할 이름을 입력해주세요")
    private String nickname;
}
