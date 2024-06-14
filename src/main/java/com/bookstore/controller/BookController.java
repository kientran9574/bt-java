package com.bookstore.controller;

import com.bookstore.entity.Book;
import com.bookstore.services.BookServices;
import com.bookstore.services.CategoryServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookServices bookService;

    @Autowired
    private CategoryServices categoryService;

    @GetMapping
    public String showAllBooks(Model model) {
        List<Book> books = bookService.getALlBooks();
        model.addAttribute("books", books);
        return "book/list";
    }

    @GetMapping("/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/add";
    }

    @PostMapping("/add")
    public ResponseEntity<String> addBook(@Valid @ModelAttribute("book") Book book, BindingResult result) {
        if (result.hasErrors()) {
            // Here you could include detailed error information if needed
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không được để dữ liệu trống");
        }

        try {
            bookService.addBook(book);
        } catch (Exception e) {
            // Here you can handle specific exceptions and customize the response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không được bỏ soát 1 trường nào ");
        }

        // Successful creation and redirect
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/books"));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/edit/{id}")
    public String editBookForm(@PathVariable("id") Long id, Model model) {
        Book book = bookService.getBookById(id);
        if (book != null) {
            model.addAttribute("book", book);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "book/edit";
        }
        return "redirect:/books";
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<?> updateBook(@PathVariable("id") Long id, @Valid @ModelAttribute("book") Book book, BindingResult result) {
        if (result.hasErrors()) {
            // You can include error details if needed
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid book data");
        }

        try {
            // Check if the book exists
            Book existingBook = bookService.getBookById(id);
            if (existingBook == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
            }

            bookService.updateBook(book);
        } catch (Exception e) {
            // Handle specific exceptions if necessary
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the book");
        }

        // Successful update and redirect
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/books"));
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 Found for redirection
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }
}
