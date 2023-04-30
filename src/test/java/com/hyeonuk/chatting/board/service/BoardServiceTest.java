package com.hyeonuk.chatting.board.service;

import com.hyeonuk.chatting.board.repository.BoardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Nested
    @DisplayName("findAll Test")
    public class FindAllTests{
        @Nested
        @DisplayName("success case")
        class Success{

        }

        @Nested
        @DisplayName("failure case")
        class Failure{

        }
    }
}