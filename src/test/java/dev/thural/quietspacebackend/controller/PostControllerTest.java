package dev.thural.quietspacebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.thural.quietspacebackend.mapper.PostMapper;
import dev.thural.quietspacebackend.model.PostDTO;
import dev.thural.quietspacebackend.repository.PostRepository;
import dev.thural.quietspacebackend.repository.UserRepository;
import dev.thural.quietspacebackend.service.PostService;
import dev.thural.quietspacebackend.service.impls.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PostController.class)
public class PostControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

    @Captor
    ArgumentCaptor<UUID> objectIdArgumentCaptor;

    @Captor
    ArgumentCaptor<PostDTO> postArgumentCaptor;

    @Autowired
    ObjectMapper objectMapper;

    PostService postServiceImpl;

    @Autowired
    PostRepository postRepository;

    UserRepository userRepository;

    PostMapper postMapper;

    @BeforeEach
    void setUp() {
        postServiceImpl = new PostServiceImpl(postMapper, postRepository, userRepository);
    }

    @Test
    void getAllPosts() throws Exception {
        List<PostDTO> testPosts = postServiceImpl.getAll(, );

        given(postService.getAll(, )).willReturn(testPosts);

        mockMvc.perform(get(PostController.POST_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(testPosts.size())));
    }

    @Test
    void getPostById() throws Exception {
        PostDTO testPost = postServiceImpl.getAll(, ).get(1);

        given(postService.getById(testPost.getId()))
                .willReturn(Optional.of(testPost));

        mockMvc.perform(get(PostController.POST_PATH + "/" + testPost.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testPost.getId().toString())))
                .andExpect(jsonPath("$.text", is(testPost.getText())));
    }

    @Test
    void createPost() throws Exception {
        List<PostDTO> testPosts = postServiceImpl.getAll(, );
        PostDTO testPost = testPosts.get(0);
        testPost.setText("testText");

        given(postService.addOne(any(PostDTO.class)))
                .willReturn(testPosts.get(1));

        mockMvc.perform(post(PostController.POST_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPost)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void updatePost() throws Exception {
        List<PostDTO> testPosts = postServiceImpl.getAll(, );
        PostDTO testPost = testPosts.get(0);
        testPost.setText("testText");

        mockMvc.perform(put(PostController.POST_PATH + "/" + testPost.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPost)))
                .andExpect(status().isNoContent());

        verify(postService).updateOne(any(UUID.class), any(PostDTO.class), jwtToken);
    }

    @Test
    void deletePost() throws Exception {
        List<PostDTO> testPosts = postServiceImpl.getAll(, );
        PostDTO testPost = testPosts.get(0);

        mockMvc.perform(delete(PostController.POST_PATH + "/" + testPost.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        ArgumentCaptor<UUID> objectIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);

        verify(postService).deleteOne(objectIdArgumentCaptor.capture(), jwtToken);

        assertThat(testPost.getId()).isEqualTo(objectIdArgumentCaptor.getValue());
    }

    @Test
    void patchPost() throws Exception {
        List<PostDTO> testPosts = postServiceImpl.getAll(, );

        PostDTO testPost = testPosts.get(0);
        testPost.setUsername("testUser");
        testPost.setText("testText");

        mockMvc.perform(patch(PostController.POST_PATH + "/" + testPost.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPost)))
                .andExpect(status().isNoContent());

        verify(postService).patchOne(jwtToken, objectIdArgumentCaptor.capture(), postArgumentCaptor.capture());

        assertThat(testPost.getId()).isEqualTo(objectIdArgumentCaptor.getValue());
        assertThat(testPost.getUsername()).isEqualTo(postArgumentCaptor.getValue().getUsername());
        assertThat(testPost.getText()).isEqualTo(postArgumentCaptor.getValue().getText());
    }
}