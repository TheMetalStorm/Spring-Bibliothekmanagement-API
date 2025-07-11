package com.themetalstorm.bibliothekssystem.controller;

import com.themetalstorm.bibliothekssystem.dto.AuthorDTO;
import com.themetalstorm.bibliothekssystem.service.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

//TODO: check permissions once authorization is implemented
//TODO: return Response Entity when appropriate

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("")
    public Page<AuthorDTO> getAllAuthors(@RequestParam(required = false) Integer page,
                                         @RequestParam(required = false) Integer size,
                                         @RequestParam(defaultValue = "lastName") String sortField,
                                         @RequestParam(defaultValue = "ASC") String sortDirection)  {
        return authorService.getAllAuthors(page, size, sortField, sortDirection);
    }

    @GetMapping("/{id}")
    public AuthorDTO getAuthor(@PathVariable int id) {
        return authorService.getAuthorById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteAuthor(@PathVariable int id) {
        authorService.deleteById(id);
    }
}
