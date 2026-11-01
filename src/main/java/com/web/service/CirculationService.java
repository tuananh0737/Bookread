package com.web.service;

import com.web.config.MessageException;
import com.web.entity.Book;
import com.web.entity.BorrowBook;
import com.web.entity.User;
import com.web.repository.BookRepository;
import com.web.repository.BorrowBookRepository;
import com.web.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class CirculationService {
    private final BookRepository books;
    private final BorrowBookRepository loans;
    private final UserRepository users;
    private final BorrowRequestService borrowRequests;
    public CirculationService(BookRepository books, BorrowBookRepository loans, UserRepository users,
                              BorrowRequestService borrowRequests) {
        this.books=books; this.loans=loans; this.users=users; this.borrowRequests=borrowRequests;
    }
    @Transactional
    public BorrowBook borrow(Long userId, Long bookId) {
        Book book = books.lockById(bookId).orElseThrow(() -> new MessageException("Book not found",404));
        User user = users.findById(userId).orElseThrow(() -> new MessageException("User not found",404));
        if (book.getQuantity()==null || book.getQuantity()<=0) throw new MessageException("No available copies",409);
        if (loans.existsActiveLoan(userId, bookId)) throw new MessageException("Book already on loan to this user",409);
        book.setQuantity(book.getQuantity()-1);
        BorrowBook loan = new BorrowBook();
        loan.setBook(book); loan.setUser(user); loan.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        loan.setReturnDueDate(Timestamp.valueOf(LocalDateTime.now().plusDays(10)));
        loan.setReturned(false);
        return loans.save(loan);
    }
    @Transactional
    public void returnLoan(Long id) {
        BorrowBook loan = loans.lockById(id).orElseThrow(() -> new MessageException("Loan not found",404));
        if (Boolean.TRUE.equals(loan.getReturned())) throw new MessageException("Already returned",409);
        Book book = books.lockById(loan.getBook().getId())
                .orElseThrow(() -> new MessageException("Book not found",404));
        loan.setReturned(true);
        loan.setActualReturnDate(new Timestamp(System.currentTimeMillis()));
        book.setQuantity(book.getQuantity()+1);
        borrowRequests.notifyUsersWhenBookAvailable(book);
    }
}
