package ru.netology.repository;

import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Repository;

@Repository
public class PostRepository {
  private final ConcurrentHashMap<Long, Post> posts;
  private final Lock idLock;
  private long count = 1;

  public PostRepository() {
    this.posts = new ConcurrentHashMap<>();
    this.idLock = new ReentrantLock();
  }

  public List<Post> all() {
    return new ArrayList<>(posts.values());
  }

  public Optional<Post> getById(long id) {
    return Optional.ofNullable(posts.get(id));
  }

  public Post save(Post post) {
    if (post.getId() == 0) {
      long newId;
      idLock.lock();
      try {
        newId = count++;
        post.setId(newId);
      } finally {
        idLock.unlock();
      }
      posts.put(newId, post);
    } else {
      posts.compute(post.getId(), (id, existingPost) -> {
        if (existingPost == null) {
          throw new IllegalArgumentException("Post with id " + id + " not found");
        }
        return post;
      });
    }
    return post;
  }

  public void removeById(long id) {
    posts.remove(id);
  }
}
