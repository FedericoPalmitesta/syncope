/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.syncope.core.persistence.dao.impl;

import java.util.Collections;
import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.syncope.core.persistence.beans.role.SyncopeRole;
import org.syncope.core.persistence.beans.user.SyncopeUser;
import org.syncope.core.persistence.dao.SyncopeUserDAO;

@Repository
public class SyncopeUserDAOImpl extends AbstractDAOImpl
        implements SyncopeUserDAO {

    @Override
    public SyncopeUser find(Long id) {
        return entityManager.find(SyncopeUser.class, id);
    }

    @Override
    public List<SyncopeUser> findAll() {
        Query query = entityManager.createQuery("SELECT e FROM SyncopeUser e");
        return query.getResultList();
    }

    @Override
    @Transactional
    public SyncopeUser save(SyncopeUser syncopeUser) {
        return entityManager.merge(syncopeUser);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SyncopeUser user = find(id);

        for (SyncopeRole role : user.getRoles()) {
            user.removeRole(role);
        }
        user.setRoles(Collections.EMPTY_SET);

        entityManager.remove(user);
    }
}
