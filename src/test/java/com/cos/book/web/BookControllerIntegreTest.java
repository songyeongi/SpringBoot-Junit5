package com.cos.book.web;

import com.cos.book.domain.Book;
import com.cos.book.domain.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 통합 테스트 (모든 Bean들을 똑같이 IoC에 올리고 테스트 하는 것)
 * Bean : 메모리에 저장되는 객체 (싱글톤으로 저장되는 객체들)
 * 통합테스트는 내 프로젝트 전체를 테스트 한다는 것
 * MVC로 치면 지금 Book에 관련된 MVC밖에 없다.
 * Book에 관련된 MVC가 메모리에 다 뜬다. Service,Repository가 뜬다
 *
 * WebEnvironment.MOCK = 실제 톰캣을 올리는 게 아니라, 다른 톰캣으로 테스트
 * -모의 윕 환경 제공
 * WebEnvironment.RANDOM_PORT = 실제 톰캣으로 테스트
 * - 실제 웹 환경을 로드하고 제공
 * 테스트할 때 실제 웹 환경 올릴 필요없다. mock으로 올리자
 *
 * @AutoConfigureMockMvc - MockMvc를 IoC에 등록해줌
 * @Transactional은 각각의 테스트함수가 종료될 때마다 트랜잭션을 rollback해주는 어노테이션 // 독립적으로 테스트 가능
 */

@Slf4j
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc // 이걸 안 넣으면 MockMvc dependency injection을 못한다. (IoC에 없다는 뜻)
// mockito를 메모리에 띄워준다. (IoC컨테이너에) - SpringBootTest에는 해당 어노테이션이 없어서 직접 넣어줘야함
public class BookControllerIntegreTest {

    //@SpringBootTest를 쓸 때 Mvc라는 걸 테스트 할 수 있다. Mock이
    @Autowired //Autowired 한다는 건 메모리에 떠 있어야한다.
    private MockMvc mockMvc;
    //mockito라고 하는 라이브러리가 하는 역할 mockMvc.perform에서 mockito로
    // controller의 주소로 테스트 해 볼 수 있는 라이브러리
    // mockMvc를 통해 테스트할 거다

    // 통합테스트니깐 bookRepository @Autowired 가능 / 실제 db에 넣는다.
    @Autowired
    private BookRepository bookRepository;

    // 통합테스트는 메모리에 다 떠있다.
    // jpa는 EntityManager의 구현체이다. 구현체 : 더 쉽게 사용할 수 있게 추상화되어있다.
    // 데이터베이스 관련된 EntityManager
    @Autowired
    private EntityManager entityManager;

    // 연결되서 테스트하는 것이 아닌 분리해서 테스트 해야한다.
    // 완전히 독립적으로 분리 시키는게 목적이다.
    // 모든 테스트 함수가 실행되기 전 각각 실행된다.
    @BeforeEach
    public void init() {
//        entityManager.persist(new Book()); // 원래는 여기다가 객체 넣고 쓴다. persist 영속화시키다
        // h2 db 문법 (db에 저장되는 번호 초기화)
        entityManager.createNativeQuery("ALTER TABLE book ALTER COLUMN id RESTART WITH 1").executeUpdate();
        // 실제로 데이터를 집어넣고 테스트

//        // 메서드마다 넣기 번거로울 때 beforeEach 객체 넣기
//        List<Book> books = new ArrayList<>();
//        books.add(new Book(1L, "스프링 따라하기", "코스"));
//        books.add(new Book(2L, "리액트 따라하기", "코스"));
//        books.add(new Book(3L, "junit 따라하기", "코스"));
//        bookRepository.saveAll(books); // 한꺼번에 여러개 저장
    }

//    @AfterEach
//    public void end() {
//        bookRepository.deleteAll();
//    }

    @Test
    public void save_테스트() throws Exception {
        // given (테스트를 하기 위한 준비)
        Book book = new Book(null, "스프링 따라하기", "코스");
        String content = new ObjectMapper().writeValueAsString(book); // Object를 json으로 바꾸는 함수
        // json 파싱해서 string으로 보여준다.

        // 단위테스트 작성 후 통합테스트에 옮겨도 된다.
        // stub만들기 가정법 미리 행동지정
        // 통합 테스트는 실제 서비스가 메모리에 뜬다. 실제 서비스가 실행되니깐 stub이 필요없다.
//        when(bookService.저장하기(book)).thenReturn(new Book(1L, "스프링 따라하기", "코스"));
        // stub이 필요없다는 건 가짜 @MockBean bookservice가 필요없다. 진짜가 있으니깐
        // 차이는 통합은 전체를 테스트하니깐 bean이 다 뜬다. @WebMvcTest는 컨트롤러만 띄우니깐 가볍다. 통합은 무거움
        // 통합테스트는 쉽다 근데 디버깅하기 어렵다. validation, security, filter들이 다 들어가면 검증하기 어렵다.

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
        // 통합테스트에서는 실제 service, repository를 띄워서 할 거니깐 아래 stub 필요없음 (실제 db는 아니고 가짜이다 mock으로 하니깐)
        List<Book> books = new ArrayList<>();
        books.add(new Book(1L, "스프링 따라하기", "코스"));
        books.add(new Book(2L, "리액트 따라하기", "코스"));
        books.add(new Book(3L, "junit 따라하기", "코스"));
        bookRepository.saveAll(books); // 한꺼번에 여러개 저장

        // when
        ResultActions resultActions = mockMvc.perform(get("/book")
                .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        // 단위테스트 100% 보장 못하는 이유 내가 then을 제대로 적지 않으면 100% 보장안됨
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(1L))
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.[2].title").value("junit 따라하기"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findById_테스트() throws Exception {
        // given
        // 실제로 데이터를 집어넣고 테스트
        List<Book> books = new ArrayList<>();
        books.add(new Book(1L, "스프링 따라하기", "코스"));
        books.add(new Book(2L, "리액트 따라하기", "코스"));
        books.add(new Book(3L, "junit 따라하기", "코스"));
        bookRepository.saveAll(books); // 한꺼번에 여러개 저장
        // 내가 찾을 데이터
        Long id=2L;

        // when find는 내가 줄 content랑 데이터가 없다. 기대하는 거만 있으면 된다.
        ResultActions resultActions = mockMvc.perform(get("/book/{id}", id)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        // then 검증
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("리액트 따라하기"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void update_테스트() throws Exception {
        // given
        // 실제로 데이터를 집어넣고 테스트
        List<Book> books = new ArrayList<>();
        books.add(new Book(1L, "스프링 따라하기", "코스"));
        books.add(new Book(2L, "리액트 따라하기", "코스"));
        books.add(new Book(3L, "junit 따라하기", "코스"));
        bookRepository.saveAll(books); // 한꺼번에 여러개 저장
        // 업데이트에 필요한 id
        Long id =3L;
        Book book = new Book(null, "C++ 따라하기", "코스");
        String content = new ObjectMapper().writeValueAsString(book);

        // when
        ResultActions resultActions = mockMvc.perform(put("/book/{id}", id)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.title").value("C++ 따라하기"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delete_테스트() throws Exception {
        // given
        // 실제로 데이터를 집어넣고 테스트
        List<Book> books = new ArrayList<>();
        books.add(new Book(1L, "스프링 따라하기", "코스"));
        books.add(new Book(2L, "리액트 따라하기", "코스"));
        books.add(new Book(3L, "junit 따라하기", "코스"));
        bookRepository.saveAll(books); // 한꺼번에 여러개 저장

        Long id = 1L;

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
