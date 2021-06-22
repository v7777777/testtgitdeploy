package main.model.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tags")
@Data

public class Tag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "name", nullable = false)
  private String name;

  // The owner side is the one without the mappedBy attribute.
  // JPA/Hibernate only cares about the owner side.
  // Your code only modifies the inverse side, and not the owner side.

  @ManyToMany(mappedBy = "tags", cascade = CascadeType.ALL)
  private List<Post> posts;

  @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Tag2post> tag2posts;


}


