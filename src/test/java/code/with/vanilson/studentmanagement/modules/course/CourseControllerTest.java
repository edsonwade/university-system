package code.with.vanilson.studentmanagement.modules.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import code.with.vanilson.studentmanagement.config.JwtUtils;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CourseService courseService;

        @MockBean
        private JwtUtils jwtUtils;

        @MockBean
        private UserDetailsService userDetailsService;

        @MockBean
        private AuthenticationProvider authenticationProvider;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("POST /api/v1/courses - Success")
        @WithMockUser(roles = "ADMIN")
        void createCourse_Success() throws Exception {
                Course course = Course.builder()
                                .title("Computer Science")
                                .description("Intro to CS")
                                .credits(4)
                                .build();

                when(courseService.createCourse(any(Course.class))).thenReturn(course);

                mockMvc.perform(post("/api/v1/courses")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(course)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.title").value("Computer Science"))
                                .andExpect(jsonPath("$.message").value("Course created successfully"));
        }

        @Test
        @DisplayName("GET /api/v1/courses - Success")
        @WithMockUser
        void getAllCourses_Success() throws Exception {
                Course course = Course.builder()
                                .title("Computer Science")
                                .description("Intro to CS")
                                .credits(4)
                                .build();
                List<Course> courses = Collections.singletonList(course);

                when(courseService.getAllCourses()).thenReturn(courses);

                mockMvc.perform(get("/api/v1/courses"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data[0].title").value("Computer Science"))
                                .andExpect(jsonPath("$.message").value("Courses retrieved successfully"));
        }

        @Test
        @DisplayName("GET /api/v1/courses/{id} - Success")
        @WithMockUser
        void getCourseById_Success() throws Exception {
                Course course = Course.builder()
                                .title("Computer Science")
                                .description("Intro to CS")
                                .credits(4)
                                .build();

                when(courseService.getCourseById(1L)).thenReturn(course);

                mockMvc.perform(get("/api/v1/courses/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.title").value("Computer Science"))
                                .andExpect(jsonPath("$.message").value("Course retrieved successfully"));
        }
}
