package ru.hatterhat.keycloak.local;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.util.JsonSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ru.hatterhat.keycloak.local.props.KeycloakServerProperties;

public class EmbeddedKeycloakApplication extends KeycloakApplication {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedKeycloakApplication.class);

    public static KeycloakServerProperties keycloakServerProperties;

    public EmbeddedKeycloakApplication() {
        super();
        createMasterRealmAdminUser();
        createRealm();
        createUsers();
    }

    private void createUsers() {
        KeycloakSession session = getSessionFactory().create();
        try {
            session.getTransactionManager().begin();
            RealmModel realmModel = session.realms().getRealm(keycloakServerProperties.getRealmId());
            UserModel chatUser1 = session.users().addUser(realmModel, "cu1");
            chatUser1.setEnabled(true);
            UserCredentialModel usrCredModel1 = UserCredentialModel.password("cu1");
            session.userCredentialManager().updateCredential(realmModel, chatUser1, usrCredModel1);
            UserModel chatUser2 = session.users().addUser(realmModel, "cu2");
            chatUser2.setEnabled(true);
            UserCredentialModel usrCredModel2 = UserCredentialModel.password("cu2");
            session.userCredentialManager().updateCredential(realmModel, chatUser2, usrCredModel2);
            session.getTransactionManager().commit();
        } catch (Exception ex) {
            LOG.warn("Couldn't create user: {}", ex.getMessage());
            session.getTransactionManager().rollback();
        } finally {
            session.close();
        }
    }

    private void createMasterRealmAdminUser() {
        KeycloakSession session = getSessionFactory().create();
        ApplianceBootstrap applianceBootstrap = new ApplianceBootstrap(session);
        KeycloakServerProperties.AdminUser admin = keycloakServerProperties.getAdminUser();
        try {
            session.getTransactionManager().begin();
            applianceBootstrap.createMasterRealmUser(admin.getUsername(), admin.getPassword());
            session.getTransactionManager().commit();
        } catch (Exception ex) {
            LOG.warn("Couldn't create keycloak master admin user: {}", ex.getMessage());
            session.getTransactionManager().rollback();
        } finally {
            session.close();
        }

    }

    private void createRealm() {
        KeycloakSession session = getSessionFactory().create();
        try {
            session.getTransactionManager().begin();
            RealmManager manager = new RealmManager(session);
            Resource lessonRealmImportFile = new ClassPathResource(keycloakServerProperties.getRealmImportFile());
            manager.importRealm(
                    JsonSerialization.readValue(lessonRealmImportFile.getInputStream(), RealmRepresentation.class));
            session.getTransactionManager().commit();
        } catch (Exception ex) {
            LOG.warn("Failed to import Realm json file: {}", ex.getMessage());
            session.getTransactionManager().rollback();
        } finally {
            session.close();
        }

    }




}
