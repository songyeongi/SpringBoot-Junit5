package com.cos.book.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.book.domain.Book;
import com.cos.book.domain.BookRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BookService {

	private final BookRepository bookRepository;
	
	@Transactional // 서비스 함수가 종료될 때 commit할지 rollback할지 트랜잭션 관리하겠다.
	public Book 저장하기(Book book) {
		return bookRepository.save(book);
	}
	
	@Transactional(readOnly = true) // JPA 변경감지라는 내부 기능 활성화 X, update시의 정합성을 유지해줌, insert의 유령데이터현상(팬텀현상) 못막을..
	public Book 한건가져오기(Long id) {
		return bookRepository.findById(id)
			.orElseThrow(()-> new IllegalArgumentException("Book id를 확인해주세요."));
	}
	
	@Transactional(readOnly = true)
	public List<Book> 모두가져오기() {
		return bookRepository.findAll();
	}
	
	@Transactional
	public Book 수정하기(Long id, Book book) {
		Book bookEntity = bookRepository.findById(id)
			.orElseThrow(()-> new IllegalArgumentException("Book id를 확인해주세요."));
		bookEntity.setTitle(book.getTitle());
		bookEntity.setAuthor(book.getAuthor());
		return bookEntity;
	}
	
	@Transactional
	public String 삭제하기(Long id) {
		bookRepository.deleteById(id);
		return "ok";
	}
}
