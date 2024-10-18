package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
  private PostController controller;

  private static final String PATH = "/api/posts";
  private static final String PATH_D = "/api/posts/\\d+";
  private static final String GET_METHOD = "GET";
  private static final String POST_METHOD = "POST";
  private static final String DELETE_METHOD = "DELETE";

  @Override
  public void init() {
    final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("ru.netology");
    controller = context.getBean(PostController.class);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();

      if (path.equals("/")) {
        resp.setStatus(HttpServletResponse.SC_OK);
      } else if (path.startsWith(PATH)) {
        switch (method) {
          case GET_METHOD -> handleGetRequests(path, resp);
          case POST_METHOD -> handlePostRequests(path, req.getReader(), resp);
          case DELETE_METHOD -> handleDeleteRequests(path, resp);
          default -> resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
      } else {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      }
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }


  private void handleGetRequests(String path, HttpServletResponse resp) throws Exception {
    if (path.equals(PATH)) {
      controller.all(resp);
    } else if (path.matches(PATH_D)) {
      final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
      controller.getById(id, resp);
    } else {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  private void handlePostRequests(String path, java.io.BufferedReader reader, HttpServletResponse resp) throws Exception {
    if (path.equals(PATH)) {
      controller.save(reader, resp);
    } else {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  private void handleDeleteRequests(String path, HttpServletResponse resp) throws Exception {
    if (path.matches(PATH_D)) {
      final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
      controller.removeById(id, resp);
    } else {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
  }
}
