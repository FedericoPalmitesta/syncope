package org.apache.syncope.core.provisioning.java.propagation;

import java.util.HashSet;
import java.util.Set;
import org.apache.syncope.common.lib.types.AnyTypeKind;
import org.apache.syncope.common.lib.types.ResourceOperation;
import org.apache.syncope.core.persistence.api.attrvalue.validation.InvalidPlainAttrValueException;
import org.apache.syncope.core.persistence.api.dao.GroupDAO;
import org.apache.syncope.core.persistence.api.dao.PlainSchemaDAO;
import org.apache.syncope.core.persistence.api.dao.UserDAO;
import org.apache.syncope.core.persistence.api.entity.AnyUtils;
import org.apache.syncope.core.persistence.api.entity.AnyUtilsFactory;
import org.apache.syncope.core.persistence.api.entity.EntityFactory;
import org.apache.syncope.core.persistence.api.entity.PlainSchema;
import org.apache.syncope.core.persistence.api.entity.group.GPlainAttr;
import org.apache.syncope.core.persistence.api.entity.group.Group;
import org.apache.syncope.core.persistence.api.entity.task.PropagationTask;
import org.apache.syncope.core.persistence.api.entity.task.TaskExec;
import org.apache.syncope.core.persistence.api.entity.user.UPlainAttr;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is required during setup of an External Resource based on the ConnId
 * <a href="https://github.com/Tirasa/ConnIdAzureBundle">Azure connector</a>.
 *
 * It manages:
 * <ol>
 * <li>the User id provided by Azure, which will need to be used for all subsequent operations</li>
 * <li>the Group id provided by Azure, which will need to be used for all subsequent operations</li>
 * </ol>
 */
public class AzurePropagationActions extends DefaultPropagationActions {

    private static final Logger LOG = LoggerFactory.getLogger(AzurePropagationActions.class);

    @Autowired
    private PlainSchemaDAO plainSchemaDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private GroupDAO groupDAO;

    @Autowired
    private EntityFactory entityFactory;

    @Autowired
    private AnyUtilsFactory anyUtilsFactory;

    private final static String USER_MAIL_NICKNAME = "mailNickname";

    private final static String GROUP_MAIL_NICKNAME = "mailNickname";

    protected String getAzureIdSchema() {
        return "AzureUserId";
    }

    protected String getAzureGroupIdSchema() {
        return "AzureGroupId";
    }

    @Transactional
    @Override
    public void before(final PropagationTask task, final ConnectorObject beforeObj) {
        super.before(task, beforeObj);

        switch (task.getAnyTypeKind()) {
            case USER:
                User user = userDAO.find(task.getEntityKey());
                if (user != null) {
                    Set<Attribute> attributes = new HashSet<>(task.getAttributes());

                    // Ensure to set __NAME__ value to user's "mailNickname"
                    Name name = AttributeUtil.getNameFromAttributes(attributes);
                    if (name != null) {
                        attributes.remove(name);
                    }
                    attributes.add(
                            new Name(AttributeUtil.find(USER_MAIL_NICKNAME, attributes).getValue().get(0).toString()));

                    task.setAttributes(attributes);
                }

                break;
            case GROUP:
                Set<Attribute> attributes = new HashSet<>(task.getAttributes());

                // Ensure to set __NAME__ value to user's "mailNickname"
                Name name = AttributeUtil.getNameFromAttributes(attributes);
                if (name != null) {
                    attributes.remove(name);
                }
                attributes.add(
                        new Name(AttributeUtil.find(GROUP_MAIL_NICKNAME, attributes).getValue().get(0).toString()));

                task.setAttributes(attributes);
                break;
            default:
                LOG.debug("Not about user, or group, not doing anything");
                break;
        }
    }

    @Transactional
    @Override
    public void after(final PropagationTask task, final TaskExec execution, final ConnectorObject afterObj) {
        if (task.getOperation() == ResourceOperation.DELETE || task.getOperation() == ResourceOperation.NONE) {
            return;
        }

        if (AnyTypeKind.USER.equals(task.getAnyTypeKind())) {

            User user = userDAO.find(task.getEntityKey());
            if (user == null) {
                LOG.error("Could not find user {}, skipping", task.getEntityKey());
            } else {
                boolean modified = false;
                AnyUtils anyUtils = anyUtilsFactory.getInstance(user);

                // Azure User ID
                PlainSchema azureId = plainSchemaDAO.find(getAzureIdSchema());
                if (azureId == null) {
                    LOG.error("Could not find schema {}, skipping", getAzureIdSchema());
                } else {
                    // set back the __UID__ received by Azure
                    UPlainAttr attr = user.getPlainAttr(getAzureIdSchema());
                    if (attr == null) {
                        attr = entityFactory.newEntity(UPlainAttr.class);
                        attr.setSchema(azureId);
                        attr.setOwner(user);
                        user.add(attr);

                        try {
                            attr.add(afterObj.getUid().getUidValue(), anyUtils);
                            modified = true;
                        } catch (InvalidPlainAttrValueException e) {
                            LOG.error("Invalid value for attribute {}: {}",
                                    azureId.getKey(), afterObj.getUid().getUidValue(), e);
                        }
                    } else {
                        LOG.debug("User {} has already {} assigned: {}",
                                user, azureId.getKey(), attr.getValuesAsStrings());
                    }
                }

                if (modified) {
                    userDAO.save(user);
                }
            }
        } else if (AnyTypeKind.GROUP.equals(task.getAnyTypeKind())) {

            Group group = groupDAO.find(task.getEntityKey());
            if (group == null) {
                LOG.error("Could not find group {}, skipping", task.getEntityKey());
            } else {
                boolean modified = false;
                AnyUtils anyUtils = anyUtilsFactory.getInstance(group);

                // Azure Group ID
                PlainSchema azureId = plainSchemaDAO.find(getAzureGroupIdSchema());
                if (azureId == null) {
                    LOG.error("Could not find schema {}, skipping", getAzureGroupIdSchema());
                } else {
                    // set back the __UID__ received by Azure
                    GPlainAttr attr = group.getPlainAttr(getAzureGroupIdSchema());
                    if (attr == null) {
                        attr = entityFactory.newEntity(GPlainAttr.class);
                        attr.setSchema(azureId);
                        attr.setOwner(group);
                        group.add(attr);

                        try {
                            attr.add(afterObj.getUid().getUidValue(), anyUtils);
                            modified = true;
                        } catch (InvalidPlainAttrValueException e) {
                            LOG.error("Invalid value for attribute {}: {}",
                                    azureId.getKey(), afterObj.getUid().getUidValue(), e);
                        }
                    } else {
                        LOG.debug("Group {} has already {} assigned: {}",
                                group, azureId.getKey(), attr.getValuesAsStrings());
                    }
                }

                if (modified) {
                    groupDAO.save(group);
                }
            }
        }
    }

}
