package main.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import main.model.dto.response.TagResponse;
import main.model.dto.response.listResponses.ListTagResponse;
import main.model.entity.Tag;
import main.repository.PostRepository;
import main.repository.TagRepository;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TagService {

  private final TagRepository tagRepository;
  private final PostRepository postRepository;

  public ListTagResponse getTags(String query) {

    double totalPosts = postRepository.countAllActivePosts();

    List<TagResponse> tags = new ArrayList<>();
    ListTagResponse listTagResponse = new ListTagResponse(tags);
    List<Tag> tagsFound;

    if (query == null) {
      tagsFound = tagRepository.findAll();
    }
    else {
      tagsFound = tagRepository.findByNameStartingWith(query);
    }

    double maxPostsAmountOfOneTag = tagsFound.get(0).getPosts().size();

    // ненормированный вес:

    for (int i = 0; i < tagsFound.size(); i++) {

      TagResponse tagResponse = new TagResponse();
      Tag currentTag = tagsFound.get(i);
      tagResponse.setName(currentTag.getName());
      double postsCountForCurrentTag = currentTag.getPosts().size();
      double notNormilizedWeight = roundDouble(roundDouble(postsCountForCurrentTag / totalPosts)*0.75);
   //   double notNormilizedWeight = roundDouble(postsCountForCurrentTag / totalPosts); // не нормализованный вес
      tagResponse.setWeight(notNormilizedWeight);
      tags.add(tagResponse);

      if (postsCountForCurrentTag > maxPostsAmountOfOneTag) {
        maxPostsAmountOfOneTag = postsCountForCurrentTag;
      } // найти тег с наибольшим количеством постов
    }

    double k = roundDouble (1.0 / roundDouble(maxPostsAmountOfOneTag / totalPosts)); // найти коэффициент k



    tags.forEach(tag -> tag.setWeight(roundDouble(tag.getWeight() * k)));  // нормировать вес в tags

    return listTagResponse;

  }

  double roundDouble(double d){

    String s = new DecimalFormat("#.##").format(d);

    s = s.replaceAll(",", ".");

    return Double.parseDouble(s);
  }

}
