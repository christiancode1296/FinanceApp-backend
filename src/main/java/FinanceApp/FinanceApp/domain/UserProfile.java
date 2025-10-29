package FinanceApp.FinanceApp.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @NoArgsConstructor, @AllArgsConstructor, and @Data annotations belong to the Lombok library.
 * The first two, as implied by their names, automatically generate a default constructor and another
 * constructor containing all the attributes, respectively. The @Data annotation, on the other hand,
 * generates getters, setters, toString(), and other essential methods.
 * When using @Entity, JPA assumes the existence of a corresponding table in the database to store instances of this entity.
 * The @Table annotation allows customization by specifying the table name, schema, and additional attributes.
 *
 * The @Id annotation is employed to declare the primary key of an entity class.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "user_profile")
public class UserProfile {

    @Id
    @GeneratedValue // uuid wird automnatisch erstellt
    private UUID id; //Primary

    @Column(unique = true, nullable = false)
    private String oktaUserId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true) // oneToMany = 1 User mehrere listen, orphansRemoval löscht abhängigkeiten automatisch beim löschen
    private List<Watchlist> watchlists = new ArrayList<>();

}
