package com.cos.book.web;

import com.cos.book.domain.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .contentType(MediaType.APPLICATION_JSON) //지금 내가 던지는 데이터가 뭔지 알려주기
                .content(content) // 실제로 던질 데이터
                .accept(MediaType.APPLICATION_JSON)); //응답은 똑같이 json

        // then (검증)
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("스프링 따라하기")) // json의 데이터를 return 할거다 그 때 쓰는 jsonPath 함수
                .andDo(MockMvcResultHandlers.print()); // 결과를 console에 보여준다.
    }
}
