alter table post_comments add constraint fk_post_comments_post_comments foreign key (parent_id) references post_comments (id) ON DELETE CASCADE;

alter table post_comments add constraint fk_post_comments_posts foreign key (post_id) references posts (id) ON DELETE CASCADE;

alter table post_comments add constraint fk_post_comments_users foreign key (user_id) references users (id) ON DELETE CASCADE;

alter table post_votes add constraint fk_post_votes_posts foreign key (post_id) references posts (id) ON DELETE CASCADE;

alter table post_votes add constraint fk_post_votes_users foreign key (user_id) references users (id) ON DELETE CASCADE;

alter table posts add constraint fk_posts_users_m foreign key (moderator_id) references users (id) ON DELETE CASCADE;

alter table posts add constraint  fk_posts_users foreign key (user_id) references users (id) ON DELETE CASCADE;

alter table tag2post add constraint fk_tag2post_posts foreign key (post_id) references posts (id) ON DELETE CASCADE;

alter table tag2post add constraint fk_tag2post_tags foreign key (tag_id) references tags (id) ON DELETE CASCADE;

