package com.project.bookstudy.category.domain;

import com.project.bookstudy.common.domain.BaseTimeEntity;
import com.project.bookstudy.study_group.domain.StudyGroup;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"childCategories", "parentCategory"})
@SQLDelete(sql = "UPDATE category SET is_deleted = true WHERE category_id = ?")
@Where(clause = "is_deleted = false")
@EqualsAndHashCode(of = {"id"})
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    private Category parentCategory;

    @OneToMany(mappedBy = "id", cascade = CascadeType.ALL)
    private List<Category> childCategories = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id")
    private StudyGroup studyGroup;
    private String subject;

    private Boolean isDeleted = Boolean.FALSE;

    @Builder(access = AccessLevel.PRIVATE)
    private Category(Category parentCategory, StudyGroup studyGroup, String subject) {
        this.parentCategory = parentCategory;
        this.studyGroup = studyGroup;
        this.subject = subject;
    }

    public static Category from(Category parentCategory, StudyGroup studyGroup, String subject) {
        Category category = Category.builder()
                .parentCategory(parentCategory)
                .subject(subject)
                .studyGroup(studyGroup)
                .build();

        //양방향 연관관계
        if (parentCategory != null) {
            parentCategory.getChildCategories().add(category);
        }
        return category;
    }
}
