package com.cos.book.service;

import com.cos.book.domain.BookRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * 단위테스트 (Service와 관련된 애들만 메모리에 띄우면 됨)
 * 메모리에 띄울 거는 (BookService 안에서만 필요한 거를 띄우면 된다.)
 * BookRepository 가 필요하다, 이게 없으면 서비스가 테스트 안된다.
 * BookService에서만 필요한 것들을 메모리에 띄우면 된다.
 *
 * 서비스를 테스트할 때는 띄워야할 객체가 없다 서비스는 보통 기능로직이다
 * 서비스에서 띄워야할 거는 bookResporitory 밖에 없다.
 * 나는 단위테스트를 할 건데 bookService가 저장하기가 정상적으로 실행하는지 확인할거다 그런데 저장하기를 실행할 때
 * bookRepository는 의존적이다. 얘가 메모리에 떠야한다. 그러면 이건 단위테스트가 아니다. 통합이다.
 * 저장하기를 테스트하려면 repository가 bean으로 올라와있어야한다. 그러면 이건 단위테스트가 아니다
 *
 * BookRepository를 실제로 빈에 띄우면 결국 데이터베이스랑 같이 테스트하는 거랑 다름없다.
 * => 가짜 객체로 만들 수 있다. 그 환경을 @ExtendWith(MockitoExtension.class)가 지원해준다. //스프링이랑 상관없다
 */

@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {
    // test하고 싶은 서비스도 메모리에 떠야한다
    // @Mock // 스프링 빈에 뜨는게 아니라, 스프링 IoC에 등록되는게 아니라 Mockito 환경의 메모리에 등록된다.
    // mockito라는 메모리가 있는 공간에 bookservice랑 bookrepository 가 뜬다.

    @InjectMocks // BookService 객체가 만들어질 때 BookServiceUnitTest 파일에 @Mock으로 등록된 모든 애들을 주입받는다.
    // bookservice가 mockito 메모리에 뜰 때 bookRepository가 null이 아니라 mock으로 띄워진 객체이다. 가짜객체
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;
}
