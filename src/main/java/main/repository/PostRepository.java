package main.repository;

import java.util.List;
import java.util.Optional;
import main.model.dto.DateAmountView;
import main.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends CrudRepository<Post, Integer> {

  // Должны выводиться только активные (поле is_active в таблице posts равно 1), утверждённые
  //модератором (поле moderation_status равно ACCEPTED) посты с датой публикации не позднее
  //текущего момента (движок должен позволять откладывать публикацию).
  @Query(nativeQuery = true, value =
      "select * from posts "
          + "where posts.is_active = 1 and posts.moderation_status = 'accepted' and posts.time <= NOW() and id = :id")
  Optional<Post> findActivePostById(@Param("id") int id);

  @Query(nativeQuery = true, value = "select * from posts where  id = :id")
  Optional<Post> findAnyPostById(@Param("id") int id);

  @Query(nativeQuery = true, value =
      "select count(*) from posts where is_active = 1 && moderation_status = 'accepted' && time <= NOW()")
  int countAllActivePosts();

  @Query(nativeQuery = true, value =
      "select count(*) from posts where is_active = 1 && moderation_status = 'new'")
  int countNewPosts();

  @Query(nativeQuery = true, value =
      "select count(*) from posts")
  Optional<Integer> countAllPosts();

  @Query(nativeQuery = true, value =
      "select count(*) from posts where user_id = :myId && is_active = 1 && moderation_status = 'accepted' && time <= NOW()")
  Optional<Integer> countMyPublishedPosts(@Param("myId") int myId);

  @Query(nativeQuery = true, value =
      "select SUM(view_count) from posts")
  Optional<Integer> countAllPostsViews();

  @Query(nativeQuery = true, value =
      "select SUM(view_count) from posts where user_id = :myId")
  Optional<Integer> countAllMyPostsViews(@Param("myId") int myId);


  @Query(nativeQuery = true, value =
      "select * from posts order by time asc limit 1")
  Optional <Post> getFirstPublication();


  @Query(nativeQuery = true, value =
      "select * from posts where user_id = :myId order by time asc limit 1")
  Optional <Post> getMyFirstPublication(@Param("myId") int myId);

  @Query(nativeQuery = true, value =
      "select * from posts where is_active = 1 && moderation_status = 'accepted' && time <= NOW()",
      countQuery = "select count(*) from posts where is_active = 1 && moderation_status = 'accepted' && time <= NOW()")
  Page<Post> findAllActive(Pageable pageable);


  @Query(nativeQuery = true, value =
      "select posts.*, COUNT(post_comments.id) as post_comments_count from posts "
          + "left join post_comments on post_comments.post_id = posts.id "
          + "where posts.is_active = 1 and posts.moderation_status = 'accepted' and posts.time <= NOW()  "
          + "group by posts.id "
          + "order by post_comments_count DESC ",
      countQuery = "select count(*) from posts where is_active = 1 && moderation_status = 'accepted' && time <= NOW()")
  Page<Post> findAllByCommentsAmount(Pageable pageable);

  @Query(nativeQuery = true, value =
      "select posts.*,  "
          + "COUNT(case when post_votes.value = 1 && post_votes.post_id = posts.id then 1 else null end) as post_votes_likes, "
          + "COUNT(case when post_votes.value = 0 && post_votes.post_id = posts.id then 1 else null end) as post_votes_dislikes "
          + "from posts "
          + "left join post_votes on post_votes.post_id = posts.id "
          + "where posts.is_active = 1 and posts.moderation_status = 'accepted' and posts.time <= NOW()  "
          + "group by posts.id "
          + "order by post_votes_likes DESC, post_votes_dislikes ASC",
      countQuery = "select count(*) from posts where is_active = 1 && moderation_status = 'accepted' && time <= NOW()")
  Page<Post> findAllByLikesAmount(Pageable pageable);

  @Query(nativeQuery = true, value =
      "select posts.* from posts join users on users.id = posts.user_id where posts.is_active = 1 && posts.moderation_status = 'accepted' && posts.time <= NOW() "
          + "&& (posts.title like :query OR posts.text like :query OR users.name like :query) order by posts.time desc"
      , countQuery =
      "select count(posts.id) from posts join users on users.id = posts.user_id where posts.is_active = 1 && posts.moderation_status = 'accepted' && posts.time <= NOW() "
          + "&& (posts.title like :query OR posts.text like :query OR users.name like :query)")
  Page<Post> findByTextOrTitle(@Param("query") String query, Pageable pageable);

  @Query(nativeQuery = true,
      value = "select date(p.time) as time , count(date(p.time)) as count from posts as p "
          + "where p.is_active = 1 && p.moderation_status = 'accepted' && p.time <= NOW() && EXTRACT( YEAR FROM p.time) = :year "
          + "group by date(p.time) order by date(p.time) desc")
  List<DateAmountView> getStatisticsPostsFromYear(@Param("year") int year);

  @Query(nativeQuery = true,
      value = "select EXTRACT(YEAR FROM time) from posts "
          + "where is_active = 1 && moderation_status = 'accepted' && time <= NOW() group by EXTRACT(YEAR FROM time)")
  List<Integer> getYearsWithActivePosts();

  @Query(nativeQuery = true, value =
      "select * from posts where is_active = 1 && moderation_status = 'accepted' && time <= NOW() && "
          + "date(time) = :date",
      countQuery = "select count(*) from posts where is_active = 1 && moderation_status = 'accepted' && time <= NOW() && date(time) = :date")
  Page<Post> findAllActivePostsByDate(Pageable pageable, @Param("date") String date);

  @Query(nativeQuery = true, value =
      "select * from posts join tag2post on tag2post.post_id=posts.id join tags on tags.id=tag2post.tag_id "
          + "where is_active = 1 && moderation_status = 'accepted' && time <= NOW() && tags.name = :tag order by posts.time desc",
      countQuery =
          "select count(*) from posts join tag2post on tag2post.post_id=posts.id join tags on tags.id=tag2post.tag_id "
              + "where is_active = 1 && moderation_status = 'accepted' && time <= NOW() && tags.name = :tag")
  Page<Post> findAllActivePostsByTag(Pageable pageable, @Param("tag") String tag);

  @Query(nativeQuery = true, value =
      "select posts.* from posts join users on users.id = posts.user_id where posts.is_active = 1 && posts.moderation_status = 'accepted' "
    + "&& users.id = :id  order by posts.time desc"
      , countQuery =
      "select count(posts.id) from posts join users on users.id = posts.user_id where "
          + "posts.is_active = 1 && posts.moderation_status = 'accepted' && users.id = :id ")
  Page<Post> findPublishedPostsById(@Param("id") int id, Pageable pageable);

  @Query(nativeQuery = true, value =
      "select posts.* from posts join users on users.id = posts.user_id where posts.is_active = 0 && users.id = :id order by posts.time desc"
      , countQuery =
      "select count(posts.id) from posts join users on users.id = posts.user_id where posts.is_active = 0 && users.id = :id ")
  Page<Post> findInactivePostsById(@Param("id") int id, Pageable pageable);

  @Query(nativeQuery = true, value =
      "select posts.* from posts join users on users.id = posts.user_id where posts.is_active = 1 && posts.moderation_status = 'new' "
          + "&& users.id = :id  order by posts.time desc"
      , countQuery =
      "select count(posts.id) from posts join users on users.id = posts.user_id where "
          + "posts.is_active = 1 && posts.moderation_status = 'new' && users.id = :id ")
  Page<Post> findPendingPostsById(@Param("id") int id, Pageable pageable);

  @Query(nativeQuery = true, value =
      "select * from posts where posts.is_active = 1 && posts.moderation_status = 'new' "
          + "order by posts.time desc"
      , countQuery =
      "select count(*) from posts where "
          + "posts.is_active = 1 && posts.moderation_status = 'new'")
  Page<Post> getAllPendingPosts(Pageable pageable);

  @Query(nativeQuery = true, value =
      "select posts.* from posts join users on users.id = posts.user_id where posts.is_active = 1 && posts.moderation_status = 'DECLINED' "
          + "&& users.id = :id  order by posts.time desc"
      , countQuery =
      "select count(posts.id) from posts join users on users.id = posts.user_id where "
          + "posts.is_active = 1 && posts.moderation_status = 'DECLINED' && users.id = :id ")
  Page<Post> findDeclinedPostsById(@Param("id") int id, Pageable pageable);

  @Query(nativeQuery = true, value =
      "select * from posts where is_active = 1 && moderation_status = 'DECLINED' "
          + "&& moderator_id = :moderatorId order by time desc"
      , countQuery =
      "select count(*) from posts where is_active = 1 && moderation_status = 'DECLINED' "
          + "&& moderator_id = :moderatorId")
  Page<Post> getAllDeclinedByMePosts(@Param("moderatorId") int moderatorId, Pageable pageable);

  @Query(nativeQuery = true, value =
      "select * from posts where is_active = 1 && moderation_status = 'ACCEPTED' "
          + " && moderator_id = :moderatorId order by time desc"
      , countQuery =
      "select count(*) from posts where is_active = 1 && moderation_status = 'ACCEPTED' "
          + " && moderator_id = :moderatorId")
  Page<Post> getAllAcceptedByMePosts(@Param("moderatorId") int moderatorId, Pageable pageable);



}
