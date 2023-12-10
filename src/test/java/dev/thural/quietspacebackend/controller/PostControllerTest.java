package dev.thural.quietspacebackend.controller;

import dev.thural.quietspacebackend.model.Comment;
import dev.thural.quietspacebackend.model.Post;
import dev.thural.quietspacebackend.service.PostService;
import dev.thural.quietspacebackend.service.PostServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PostController.class)
public class PostControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

    @Autowired
    PostService postServiceImpl;

    @Test
    void getPostById() throws Exception {

        Post testPost = postServiceImpl.getAll().get(0);

        given(postService.getById(testPost.getId()))
                .willReturn(Optional.of(testPost));

        mockMvc.perform(get("/api/v1/posts" + "/" + testPost.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testPost.getId().toString())))
                .andExpect(jsonPath("$.text", is(testPost.getText())));
    }
}
