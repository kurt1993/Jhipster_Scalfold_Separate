package net.madicorp.smartinvestplus.config.dbmigrations;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.DB;
import net.madicorp.smartinvestplus.domain.Authority;
import net.madicorp.smartinvestplus.domain.User;
import net.madicorp.smartinvestplus.service.mustache.ListStringMustacheTemplate;
import net.madicorp.smartinvestplus.service.mustache.MustacheService;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static net.madicorp.smartinvestplus.config.CommonDbConfiguration.jongoMapper;

/**
 * Creates the initial database setup
 */
@ChangeLog(order = "001")
public class InitialSetupMigration {
    private static final Authority ROLE_ADMIN = authority("ROLE_ADMIN");

    private static final Authority ROLE_USER = authority("ROLE_USER");

    private final MustacheService mustacheService = new MustacheService();

    @ChangeSet(order = "01", author = "initiator", id = "01-addAuthorities")
    public void addAuthorities(DB db) {
        MongoCollection authorities = collection(db, "sip_authority");
        authorities.insert(ROLE_ADMIN);
        authorities.insert(ROLE_USER);
    }

    @ChangeSet(order = "02", author = "initiator", id = "02-addUsers")
    public void addUsers(DB db) {
        MongoCollection users = collection(db, "sip_user");
        users.ensureIndex(mongoIndex("login"));
        users.ensureIndex(mongoIndex("email"));
        users.insert(user("user-0", "system", "$2a$10$mE.qmcV0mFU5NcKh73TZx.z4ueI/.bDWbj0T1BYyqP481kGGarKLG", "",
                          "System", "system@localhost", true, "en", "system", ROLE_ADMIN, ROLE_USER));
        users.insert(user("user-1", "anonymousUser", "$2a$10$j8S5d7Sr7.8VTOYNviDPOeWX8KcYILUVJBsYV83Y5NtECayypx9lO",
                          "Anonymous", "User", "anonymous@localhost", true, "en", "system"));
        users.insert(user("user-2", "admin", "$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC", "admin",
                          "Administrator", "admin@localhost", true, "en", "system", ROLE_ADMIN, ROLE_USER));
        users.insert(user("user-3", "user", "$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K", "", "User",
                          "user@localhost", true, "en", "system", ROLE_USER));
    }

    private static MongoCollection collection(DB db, String name) {
        Jongo jongo = new Jongo(db, jongoMapper());
        return jongo.getCollection(name);
    }

    private String mongoIndex(String field, String... fields) {
        ListStringMustacheTemplate<String> indices;
        try {
            indices = mustacheService.compileList("mongo_indices");
        } catch (IOException e) {
            throw new MigrationException("Unable to find mongo_indices template", e);
        }
        ArrayList<String> indexFields = new ArrayList<>();
        indexFields.add(field);
        Collections.addAll(indexFields, fields);
        return indices.render(indexFields);
    }

    private static User user(String id, String login, String password, String firstName, String lastName,
                             String email, boolean activated, String langKey, String creator,
                             Authority... authorities) {
        User user = new User();
        user.setId(id);
        user.setLogin(login);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setActivated(activated);
        user.setLangKey(langKey);
        user.setCreatedBy(creator);
        user.setCreatedBy(creator);
        user.setCreatedDate(ZonedDateTime.now());
        if (authorities != null && authorities.length > 0) {
            user.setAuthorities(new HashSet<>(Arrays.asList(authorities)));
        }
        return user;
    }

    private static Authority authority(String role) {
        Authority authority = new Authority();
        authority.setName(role);
        return authority;
    }
}
