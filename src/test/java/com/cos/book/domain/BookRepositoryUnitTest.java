package com.cos.book.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * 단위테스트 메모리에 띄울 거는 (DB관련된 Bean들이 IoC에 등록되면 됨)
 * 서비스 띄울 필요없고, 컨트롤러도 띄울 필요 없다.
 * 실제 db로 테스트할 필요없다. 단위 테스트 할거라서 통합테스트할 때 실제 db 쓰자
 * 데이터베이스 관련된 로직 다 떴는데 얘가 연결된 db는 가짜 db이다. 모의로 만들어진 db
 * @Transactional은 각각의 테스트함수가 종료될 때마다 트랜잭션을 rollback해주는 어노테이션 // 독립적으로 테스트 가능
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
}
