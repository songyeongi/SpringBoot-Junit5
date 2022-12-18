package com.cos.book.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 단위테스트 메모리에 띄울 거는 (DB관련된 Bean들이 IoC에 등록되면 됨)
 * 서비스 띄울 필요없고, 컨트롤러도 띄울 필요 없다.
 * 실제 db로 테스트할 필요없다. 단위 테스트 할거라서 통합테스트할 때 실제 db 쓰자
 * 데이터베이스 관련된 로직 다 떴는데 얘가 연결된 db는 가짜 db이다. 모의로 만들어진 db
 * @Transactional은 각각의 테스트함수가 종료될 때마다 트랜잭션을 rollback해주는 어노테이션 // 독립적으로 테스트 가능
 *
 * repository는 stub이 필요없다. repository 자체에서 관계하는게 없기 때문에
 * service입장에서는 repository랑 관계를 하고 controller입장에서는 service랑 관계한다. 단위테스트 할 때는 그런걸 stub으로 만들어야한다.
 * 편한건 통합테스트이다.
 */

@Transactional
@AutoConfigureTestDatabase(replace = Replace.ANY) // Replace.ANY : 가짜 DB로 테스트, Replace.NONE : 실제 DB로 테스트
// DB를 실제 DB를 쓸건지 가짜 다른 DB를 쓸건지 설정
@DataJpaTest // jpa 관련된 애들만 메로리에 뜬다.
// @DataJpaTest - Repository들을 다 IoC에 등록해준다.
public class BookRepositoryUnitTest {

    // @Mock 으로 띄울 필요없다. IoC에 등록되어있다. @ExtendWith(SpringExtension.class) 스프링환경이다. 떠있어서 @Mock으로 띄울필요없다.
    // Mock으로 띄우면 가짜 db이다. Mock으로 띄우면 널만 아니라는 거지 bookrepository가 할 수 있는 기능이 없다. 그냥 껍데기다
    @Autowired
    private BookRepository bookRepository;

    @Test
    public void save_테스트() {
        // given 실제 데이터베이스 환경을 들고왔다. bookRepository를 bean에 등록하고 테스트한다. IoC에
        Book book = new Book(null, "책제목1", "책저자1");

        // when 실제 save되서 그걸 응답 받을거다
        Book bookEntity = bookRepository.save(book);

        // then
        assertEquals("책제목1", bookEntity.getTitle());
    }
}
