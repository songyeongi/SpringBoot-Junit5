package com.cos.book.web;

import com.cos.book.domain.Book;
import com.cos.book.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 단위 테스트 (Controller 관련 로직만 띄우기) Controller만 분리해서 테스트
 * Controller 관련 로직들이 꼭 Controller 말고도 여러가지 있다
 * controller로 들어올 때 filter - 이런 것들이 메모리에 뜬다
 * filter, ControllerAdvice(Exception 처리할 때) 이런 것들이 메모리에 같이 뜬다
 * @WebMvcTest를 사용하면 Controller, Filter, ControllerAdvice가 메모리에 뜬다.
 * Controller를 위한 객체들만 메모리에 뜬다 service, repository가 안뜬다. 전체가 뜨지 않으니 가볍다
 * @ExtendWith(SpringExtension.class) 스프링 환경으로 확장하고 싶을 때 사용
 * junit 테스트를 할 때 어떤 클래스 파일이 스프링 환경에서 테스트하고 싶으면 이 어노테이션을 붙인다.
 * 스프링에서 junit 테스트를 할 때 스프링으로 확장해줘야 한다. @ExtendWith(SpringExtension.class)는 필수
 *
 * 실제서버가 아닌 Mock 서버이다.
 */

@Slf4j
@WebMvcTest
public class BookControllerUnitTest {
    @Autowired
    private MockMvc mockMvc; // 주소 호출해서 테스트 해주는 도구 @WebMvcTest안에 @AutoConfigureMockMvc가 있다.

    // @Mock // Mock은 mockito 환경에 메모리가 뜬다. 가짜
    @MockBean // BookService가 IoC 환경에 bean으로 등록됨 / 가짜 서비스 실제로 동작 제대로 안함 / 스프링 환경
    // 가짜 bookservice 객체를 IoC에 등록
    private BookService bookService;

    // BDDMockito 패턴 given, when, then
    @Test
    public void save_테스트() throws Exception {
        // given (테스트를 하기 위한 준비)
        Book book = new Book(null, "스프링 따라하기", "코스");
        String content = new ObjectMapper().writeValueAsString(book); // Object를 json으로 바꾸는 함수
        // json 파싱해서 string으로 보여준다.

        // stub만들기 가정법 미리 행동지정
        when(bookService.저장하기(book)).thenReturn(new Book(1L, "스프링 따라하기", "코스"));

        // when (테스트 실행)
        ResultActions resultActions = mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON_UTF8) //지금 내가 던지는 데이터가 뭔지 알려주기
                .content(content) // 실제로 던질 데이터
                .accept(MediaType.APPLICATION_JSON_UTF8)); //응답은 똑같이 json

        // then (검증)
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("스프링 따라하기")) // json의 데이터를 return 할거다 그 때 쓰는 jsonPath 함수
                .andDo(MockMvcResultHandlers.print()); // 결과를 console에 보여준다.
    }

    @Test
    public void findAll_테스트() throws Exception {
        // given
        // 데이터베이스에 값이 하나도 없다. 그래서 stub이 필요
        // 통합테스트에서는 실제 service, repository를 띄워서 할 거니깐 아래 stub 필요없음 (실제 db는 아니고 가짜이다 mock으로 하니깐)
        List<Book> books = new ArrayList<>();
        books.add(new Book(1L, "스프링 따라하기", "코스"));
        books.add(new Book(2L, "리액트 따라하기", "코스"));
        when(bookService.모두가져오기()).thenReturn(books);

        // when
        ResultActions resultActions = mockMvc.perform(get("/book")
                .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        // 단위테스트 100% 보장 못하는 이유 내가 then을 제대로 적지 않으면 100% 보장안됨
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.[0].title").value("스프링 따라하기"))
                .andDo(MockMvcResultHandlers.print());
    }

    // 테스트 코드와 포스트맨 차이
    // 테스트 코드는 실행만 하면된다. 배포해서도 실행만 하면된다.
    // 포스트맨은 계속 실행해 봐야하고 테스트 해야하고 눈으로 봐야한다..

    @Test
    public void findById_테스트() throws Exception {
        // given
        Long id=1L;
        // stub이 필요해
        when(bookService.한건가져오기(id)).thenReturn(new Book(1L, "자바 공부하기", "쌀"));

        // when find는 내가 줄 content랑 데이터가 없다. 기대하는 거만 있으면 된다.
        ResultActions resultActions = mockMvc.perform(get("/book/{id}", id)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        // then 검증
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("자바 공부하기"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void update_테스트() throws Exception {
        // given
        // 업데이트에 필요한 id / stub을 만들었으니 업데이트할 객체 만들필요없음
        Long id =1L;
        Book book = new Book(null, "C++ 따라하기", "코스");
        String content = new ObjectMapper().writeValueAsString(book);
        when(bookService.수정하기(id, book)).thenReturn(new Book(1L, "C++ 따라하기", "코스"));

        // when
        ResultActions resultActions = mockMvc.perform(put("/book/{id}", id)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("C++ 따라하기"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delete_테스트() throws Exception {
        // given
        Long id = 1L;
        when(bookService.삭제하기(id)).thenReturn("ok"); // 삭제한 거는 return 해줄 수 없다. deleteById가 void라서

        // when
        ResultActions resultActions = mockMvc.perform(delete("/book/{id}", id)
                .accept(MediaType.TEXT_PLAIN));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // 문자를 응답으로 받을 때 사용
        MvcResult requestResult = resultActions.andReturn();
        String result = requestResult.getResponse().getContentAsString();
        assertEquals("ok", result);
    }
}
