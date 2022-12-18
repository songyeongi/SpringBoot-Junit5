package com.cos.book.web;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

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
 */

@WebMvcTest
public class BookControllerUnitTest {
}
