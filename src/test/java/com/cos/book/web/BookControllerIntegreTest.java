package com.cos.book.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
}
