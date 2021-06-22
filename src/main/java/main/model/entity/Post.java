package main.model.entity;

import java.time.Instant;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.dto.request.NewPostRequest;
import main.model.enums.ModerationStatusCode;

@Entity
@Table(name = "posts")
@Data

public class Post {

  public void merge (NewPostRequest newPostRequest){

    Instant time = Instant.ofEpochSecond(newPostRequest.getTimestamp());

    if (time.isBefore(Instant.now())) {
      time = Instant.now();
    }

    this.setActive(newPostRequest.isActive()); // неактивные тоже ModerationStatusCode.NEW (или принят) только не показываются модератору
    this.setTime(time);
    this.setText(newPostRequest.getText());
    this.setTitle(newPostRequest.getTitle());

  }


  @Column(name = "id")
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT")
  private boolean isActive;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "enum('NEW', 'ACCEPTED', 'DECLINED')", nullable = false)
  private ModerationStatusCode moderationStatus = ModerationStatusCode.NEW;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "moderator_id", referencedColumnName = "id")
  private User moderator; // ID пользователя-модератора, принявшего решение

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private User user;

  @Column(name = "time", nullable = false)
  private Instant time;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "text", 	nullable = false, columnDefinition = "LONGTEXT")
  private String text;

  @Column(name = "view_count", nullable = false)
  private int viewCount;  // количество просмотров поста

  // The owner side is the one without the mappedBy attribute.
  // JPA/Hibernate only cares about the owner side.
  // Your code only modifies the inverse side, and not the owner side.

  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinTable(
      name = "tag2post",
      joinColumns = {@JoinColumn(name = "post_id")},
      inverseJoinColumns = {@JoinColumn(name = "tag_id")}
  )
  private List<Tag> tags;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PostVote> postVotes;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PostComment> postComments;

   @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<Tag2post> tag2posts;


}
