package main.repository;

import java.util.Optional;
import main.model.entity.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostVoteRepository extends JpaRepository<PostVote, Integer> {

  @Query(nativeQuery = true, value =
      "select * from post_votes where user_id = :userId && post_id = :postId")
  Optional<PostVote> findByUserAndPostIds(@Param("userId") int userId, @Param("postId") int postId);

  @Query(nativeQuery = true, value =
      "select count(*) from post_votes where value = 1")
  Optional<Integer> countLikes();

  @Query(nativeQuery = true, value =
      "select count(*) from post_votes where value = 0")
  Optional<Integer> countDislikes();

  @Query(nativeQuery = true, value =
      "select count(*) from post_votes "
          + "join posts on post_votes.post_id = posts.id where posts.user_id = :myId && post_votes.value = 1")
  Optional<Integer> countMyPubsLikes(@Param("myId") int myId);

  @Query(nativeQuery = true, value =
      "select count(*) from post_votes "
          + "join posts on post_votes.post_id = posts.id where posts.user_id = :myId && post_votes.value = 0")
  Optional<Integer> countMyPubsDislikes(@Param("myId") int myId);


}
